data class Comment(
    val userId: String = "",
    val commentText: String = "",
    val commentedAt: com.google.firebase.Timestamp? = null
)
