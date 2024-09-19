import com.google.firebase.Timestamp

data class NotificationItem(
    val type: String,
    val userId: String,
    val timelineId: String,
    val timestamp: Timestamp?
)