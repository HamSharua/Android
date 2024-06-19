package com.example.challengeme.data

import android.provider.BaseColumns

object ChallengeContract {
    object PostEntry : BaseColumns {
        const val TABLE_NAME = "post"
        const val COLUMN_NAME_ID = "id"
        const val COLUMN_NAME_USER = "user"
        const val COLUMN_NAME_COMMENT = "comment"
        const val COLUMN_NAME_DATE = "date"
    }
}
