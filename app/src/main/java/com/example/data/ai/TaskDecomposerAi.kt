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
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
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
                // Fallback to intelligent local rules
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
            Break the following task into 3 to 5 tiny, bite-sized, non-intimidating micro-steps.
            Each step should take between 3 and 10 minutes.
            Task: "$title"
            Details: "$description"
            
            Return JSON in this EXACT schema:
            [
              {
                "title": "Step title (e.g. Open relevant tabs)",
                "durationMinutes": 5,
                "encouragementTip": "Gentle encouraging one-liner"
              }
            ]
            Return only the raw JSON array without markdown formatting.
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
                    title = stepObj.optString("title", "Step ${i + 1}"),
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
            lower.contains("report") || lower.contains("write") || lower.contains("essay") || lower.contains("doc") -> listOf(
                MicroStep(taskId = taskId, title = "Open blank document & add heading", durationMinutes = 3, isDone = false, orderIndex = 0, encouragementTip = "Just looking at the blank page is the hardest part. You got this!"),
                MicroStep(taskId = taskId, title = "Jot down 3 bullet points / key arguments", durationMinutes = 7, isDone = false, orderIndex = 1, encouragementTip = "No full sentences needed yet, just raw thoughts."),
                MicroStep(taskId = taskId, title = "Draft rough first paragraph", durationMinutes = 10, isDone = false, orderIndex = 2, encouragementTip = "Remember: messy first drafts are perfect drafts."),
                MicroStep(taskId = taskId, title = "Review and fix quick typos", durationMinutes = 5, isDone = false, orderIndex = 3, encouragementTip = "Final sprint! You're almost done.")
            )
            lower.contains("clean") || lower.contains("room") || lower.contains("organize") || lower.contains("tidy") -> listOf(
                MicroStep(taskId = taskId, title = "Pick up 5 items off the desk/floor", durationMinutes = 3, isDone = false, orderIndex = 0, encouragementTip = "Micro-win: 5 items only!"),
                MicroStep(taskId = taskId, title = "Throw away any visible trash or cups", durationMinutes = 4, isDone = false, orderIndex = 1, encouragementTip = "Look at that instant visual reward."),
                MicroStep(taskId = taskId, title = "Put loose papers into one single stack", durationMinutes = 5, isDone = false, orderIndex = 2, encouragementTip = "You don't need to file them yet, just stack them."),
                MicroStep(taskId = taskId, title = "Wipe down the main surface", durationMinutes = 3, isDone = false, orderIndex = 3, encouragementTip = "Fresh clean workspace unlocked!")
            )
            lower.contains("email") || lower.contains("inbox") || lower.contains("message") -> listOf(
                MicroStep(taskId = taskId, title = "Open inbox and star top 2 urgent emails", durationMinutes = 3, isDone = false, orderIndex = 0, encouragementTip = "Don't read everything, just scan."),
                MicroStep(taskId = taskId, title = "Send 1-sentence reply to email #1", durationMinutes = 5, isDone = false, orderIndex = 1, encouragementTip = "Keep it brief and polite. Done is better than perfect."),
                MicroStep(taskId = taskId, title = "Send 1-sentence reply to email #2", durationMinutes = 5, isDone = false, orderIndex = 2, encouragementTip = "Two off your mind. Huge win!"),
                MicroStep(taskId = taskId, title = "Archive or snooze the rest", durationMinutes = 2, isDone = false, orderIndex = 3, encouragementTip = "Inbox peace restored.")
            )
            lower.contains("study") || lower.contains("read") || lower.contains("learn") -> listOf(
                MicroStep(taskId = taskId, title = "Find reading material & set 10m timer", durationMinutes = 3, isDone = false, orderIndex = 0, encouragementTip = "Set up your focus zone."),
                MicroStep(taskId = taskId, title = "Skim headings and highlighted text", durationMinutes = 7, isDone = false, orderIndex = 1, encouragementTip = "Prime your brain with the big picture."),
                MicroStep(taskId = taskId, title = "Read section 1 and write 1 summary line", durationMinutes = 10, isDone = false, orderIndex = 2, encouragementTip = "Active recall locks it in."),
                MicroStep(taskId = taskId, title = "Stretch and drink a sip of water", durationMinutes = 2, isDone = false, orderIndex = 3, encouragementTip = "Brain refreshed!")
            )
            else -> listOf(
                MicroStep(taskId = taskId, title = "Gather materials & remove 1 distraction", durationMinutes = 3, isDone = false, orderIndex = 0, encouragementTip = "Clear path = clear mind."),
                MicroStep(taskId = taskId, title = "Do the easiest 5-minute piece first", durationMinutes = 5, isDone = false, orderIndex = 1, encouragementTip = "Low friction start unlocks the flow state."),
                MicroStep(taskId = taskId, title = "Focus sprint on the core task", durationMinutes = 12, isDone = false, orderIndex = 2, encouragementTip = "You're in the zone now."),
                MicroStep(taskId = taskId, title = "Wrap up & celebrate your progress", durationMinutes = 3, isDone = false, orderIndex = 3, encouragementTip = "Every step completed rewires momentum!")
            )
        }
    }
}
