package com.example.challengeme.ui.timeline.model

import java.util.Date

data class TimelineItem(
    val challenge_id: Int = 0,
    val comment: String = "",
    val datetime: Date = Date(),
    val image: String = "",  // 追加
    val timeline_id: String = "",
    val user_id: Int = 0
)
