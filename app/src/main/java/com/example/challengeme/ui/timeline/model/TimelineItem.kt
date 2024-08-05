package com.example.challengeme.ui.timeline.model

import java.util.Date

data class TimelineItem(
    val challenge_id: Int = 0,
    val comment: String = "",
    val datetime: Date = Date(),
    val image: String = "",
    val timeline_id: String = "",
    val user_id: String = ""
)
