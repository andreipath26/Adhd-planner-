package com.example.data.ai

import com.example.BuildConfig
import com.example.data.model.MicroStep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object TaskDecomposerAi {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun decomposeTask(
        taskId: Long,
        taskTitle: String,
        taskDescription: String
    ): List<MicroStep> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val aiResult = callGeminiForDecomposition(taskId, taskTitle, taskDescription, apiKey)
                if (aiResult.isNotEmpty()) {
                    return@withContext aiResult
                }
            } catch (e: Exception) {
                // Fallback to intelligent local rules on network/parsing failure
            }
        }
        return@withContext generateSmartFallbackDecomposition(taskId, taskTitle, taskDescription)
    }

    private fun callGeminiForDecomposition(
        taskId: Long,
        title: String,
        description: String,
        apiKey: String
    ): List<MicroStep> {
        val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
        val prompt = """
            You are an expert ADHD executive dysfunction coach and micro-step decomposer.
            Break the following complex task into 3 to 5 tiny, bite-sized, non-intimidating sub-tasks.
            Each sub-task should take between 3 and 10 minutes to complete.
            Task Name: "$title"
            Task Description: "$description"
            
            Return JSON in this EXACT schema:
            [
              {
                "title": "Sub-task title (e.g. Open required documents and notes)",
                "durationMinutes": 5,
                "encouragementTip": "Gentle encouraging one-liner"
              }
            ]
            Return ONLY the raw JSON array without markdown formatting.
        """.trimIndent()

        val jsonBody = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", prompt)
                        })
                    })
                })
            })
        }

        val request = Request.Builder()
            .url(url)
            .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
            .build()

        val response = httpClient.newCall(request).execute()
        if (!response.isSuccessful) return emptyList()

        val responseBody = response.body?.string() ?: return emptyList()
        val rootObj = JSONObject(responseBody)
        val candidates = rootObj.optJSONArray("candidates") ?: return emptyList()
        val firstCandidate = candidates.optJSONObject(0) ?: return emptyList()
        val content = firstCandidate.optJSONObject("content") ?: return emptyList()
        val parts = content.optJSONArray("parts") ?: return emptyList()
        val text = parts.optJSONObject(0)?.optString("text") ?: return emptyList()

        val cleanJson = text.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        val stepsArray = JSONArray(cleanJson)
        val steps = mutableListOf<MicroStep>()
        for (i in 0 until stepsArray.length()) {
            val stepObj = stepsArray.getJSONObject(i)
            steps.add(
                MicroStep(
                    taskId = taskId,
                    title = stepObj.optString("title", "Sub-task ${i + 1}"),
                    durationMinutes = stepObj.optInt("durationMinutes", 5).coerceIn(2, 20),
                    isDone = false,
                    orderIndex = i,
                    encouragementTip = stepObj.optString("encouragementTip", "You're building momentum!")
                )
            )
        }
        return steps
    }

    private fun generateSmartFallbackDecomposition(
        taskId: Long,
        title: String,
        description: String
    ): List<MicroStep> {
        val lower = "$title $description".lowercase()
        return when {
            lower.contains("report") || lower.contains("write") || lower.contains("essay") || lower.contains("doc") || lower.contains("article") -> listOf(
                MicroStep(taskId = taskId, title = "Open blank document & type title header", durationMinutes = 3, isDone = false, orderIndex = 0, encouragementTip = "Just looking at the blank page is the hardest part. You got this!"),
                MicroStep(taskId = taskId, title = "Jot down 3 bullet points / key outline items", durationMinutes = 7, isDone = false, orderIndex = 1, encouragementTip = "No full sentences needed yet, just raw thoughts."),
                MicroStep(taskId = taskId, title = "Draft rough first section / paragraph", durationMinutes = 10, isDone = false, orderIndex = 2, encouragementTip = "Remember: messy first drafts are perfect drafts."),
                MicroStep(taskId = taskId, title = "Review and polish quick sentences", durationMinutes = 5, isDone = false, orderIndex = 3, encouragementTip = "Final sprint! You're almost done.")
            )
            lower.contains("clean") || lower.contains("room") || lower.contains("organize") || lower.contains("tidy") || lower.contains("garage") -> listOf(
                MicroStep(taskId = taskId, title = "Pick up 5 items off the desk/floor", durationMinutes = 3, isDone = false, orderIndex = 0, encouragementTip = "Micro-win: 5 items only!"),
                MicroStep(taskId = taskId, title = "Throw away any visible trash or wrappers", durationMinutes = 4, isDone = false, orderIndex = 1, encouragementTip = "Look at that instant visual reward."),
                MicroStep(taskId = taskId, title = "Stack loose items or books into one spot", durationMinutes = 5, isDone = false, orderIndex = 2, encouragementTip = "You don't need to file them yet, just stack them."),
                MicroStep(taskId = taskId, title = "Wipe down the main workspace surface", durationMinutes = 3, isDone = false, orderIndex = 3, encouragementTip = "Fresh clean workspace unlocked!")
            )
            lower.contains("email") || lower.contains("inbox") || lower.contains("message") || lower.contains("slack") -> listOf(
                MicroStep(taskId = taskId, title = "Open inbox and star top 2 urgent messages", durationMinutes = 3, isDone = false, orderIndex = 0, encouragementTip = "Don't read everything, just scan."),
                MicroStep(taskId = taskId, title = "Send 1-sentence reply to message #1", durationMinutes = 5, isDone = false, orderIndex = 1, encouragementTip = "Keep it brief and polite. Done is better than perfect."),
                MicroStep(taskId = taskId, title = "Send 1-sentence reply to message #2", durationMinutes = 5, isDone = false, orderIndex = 2, encouragementTip = "Two off your mind. Huge win!"),
                MicroStep(taskId = taskId, title = "Archive or snooze the rest for later", durationMinutes = 2, isDone = false, orderIndex = 3, encouragementTip = "Inbox peace restored.")
            )
            lower.contains("study") || lower.contains("read") || lower.contains("learn") || lower.contains("course") -> listOf(
                MicroStep(taskId = taskId, title = "Gather materials & set 5m timer", durationMinutes = 3, isDone = false, orderIndex = 0, encouragementTip = "Set up your calm focus zone."),
                MicroStep(taskId = taskId, title = "Skim headings and highlighted text", durationMinutes = 7, isDone = false, orderIndex = 1, encouragementTip = "Prime your brain with the big picture."),
                MicroStep(taskId = taskId, title = "Read section 1 and jot 1 takeaway note", durationMinutes = 10, isDone = false, orderIndex = 2, encouragementTip = "Active recall locks it in."),
                MicroStep(taskId = taskId, title = "Stretch and drink a glass of water", durationMinutes = 3, isDone = false, orderIndex = 3, encouragementTip = "Brain refreshed!")
            )
            lower.contains("code") || lower.contains("bug") || lower.contains("dev") || lower.contains("feature") || lower.contains("app") -> listOf(
                MicroStep(taskId = taskId, title = "Reproduce or write down expected behavior", durationMinutes = 4, isDone = false, orderIndex = 0, encouragementTip = "Clear specification halves the debugging effort."),
                MicroStep(taskId = taskId, title = "Locate relevant file and function", durationMinutes = 5, isDone = false, orderIndex = 1, encouragementTip = "Zero in on the specific code path."),
                MicroStep(taskId = taskId, title = "Implement minimal draft change", durationMinutes = 10, isDone = false, orderIndex = 2, encouragementTip = "Focus on working code first, polish later."),
                MicroStep(taskId = taskId, title = "Run verification tests and verify fix", durationMinutes = 5, isDone = false, orderIndex = 3, encouragementTip = "Green build! Great achievement.")
            )
            lower.contains("plan") || lower.contains("project") || lower.contains("tax") || lower.contains("finance") || lower.contains("bill") -> listOf(
                MicroStep(taskId = taskId, title = "Gather required logins and documents", durationMinutes = 5, isDone = false, orderIndex = 0, encouragementTip = "Everything in one place reduces friction."),
                MicroStep(taskId = taskId, title = "Check first requirement or line item", durationMinutes = 6, isDone = false, orderIndex = 1, encouragementTip = "Just one number or item at a time."),
                MicroStep(taskId = taskId, title = "Complete main form submission / calculation", durationMinutes = 10, isDone = false, orderIndex = 2, encouragementTip = "Heavy lifting is done."),
                MicroStep(taskId = taskId, title = "File confirmation receipt / record", durationMinutes = 3, isDone = false, orderIndex = 3, encouragementTip = "Mental load lifted!")
            )
            else -> listOf(
                MicroStep(taskId = taskId, title = "Gather materials & eliminate 1 distraction", durationMinutes = 3, isDone = false, orderIndex = 0, encouragementTip = "Clear path = clear mind."),
                MicroStep(taskId = taskId, title = "Do the easiest 3-minute piece first", durationMinutes = 5, isDone = false, orderIndex = 1, encouragementTip = "Low friction start unlocks the flow state."),
                MicroStep(taskId = taskId, title = "Focus sprint on the core task", durationMinutes = 10, isDone = false, orderIndex = 2, encouragementTip = "You're in the zone now."),
                MicroStep(taskId = taskId, title = "Wrap up & celebrate your progress", durationMinutes = 3, isDone = false, orderIndex = 3, encouragementTip = "Every step completed rewires momentum!")
            )
        }
    }
}

