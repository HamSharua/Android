data class TimelineItem(
    val timelineId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userIcon: String = "",
    val comment: String = "",
    val imageUrl: String = "",
    var likeCount: Long = 0,
    var commentCount: Long = 0,
    var isLiked: Boolean = false,
    val datetime: com.google.firebase.Timestamp? = null,
    val challengeId: Long = 0L
)
