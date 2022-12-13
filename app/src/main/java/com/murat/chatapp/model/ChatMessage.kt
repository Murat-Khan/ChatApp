package com.murat.chatapp.model

import java.util.Date

data class ChatMessage(
    var senderId : String? = null,
    var receiverId : String? = null,
    var message : String? = null,
    var dataTime : String? = null,
    var dateObject: Date = Date(),
    var conversionId : String? = null,
    var conversionName : String? = null,
    var conversionImage : String? = null,
) : java.io.Serializable




