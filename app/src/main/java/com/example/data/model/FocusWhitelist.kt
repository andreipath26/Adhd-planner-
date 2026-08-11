package com.example.data.model

data class WhitelistContact(
    val id: String,
    val name: String,
    val relation: String,
    val isAllowed: Boolean = true
)

data class WhitelistApp(
    val id: String,
    val appName: String,
    val category: String,
    val isAllowed: Boolean = true
)

enum class FocusAudioTrack(val label: String, val description: String, val iconEmoji: String) {
    OFF("Silent Zen", "Pure silence with visual breath ring", "🤫"),
    WHITE_NOISE("White Noise", "Continuous broadband ambient hum", "🌊"),
    BINAURAL_ALPHA("Binaural 40Hz", "Gamma-wave ADHD concentration pulse", "🎧"),
    GENTLE_RAIN("Gentle Rain", "Soft rhythmic rainfall against window", "🌧️"),
    MELLOW_TICK("Gentle Clock Tick", "Subtle cadence to ground time passage", "⏱️")
}
