package com.murat.util

 class Constants {
     companion object {
         const val KEY_NAME = "name"
         const val KEY_EMAIL = "email"
         const val KEY_PASSWORD = "password"
         const val KEY_PREFERENCE_NAME = "chatAppPreference"
         const val KEY_IS_SIGNED_IN = "isSignedIn"
         const val KEY_USER_ID = "userId"
         const val KEY_IMAGE = "image"
         const val KEY_COLLECTION_USERS = "users"
         const val KEY_FCM_TOKEN = "fcmToken"
         const val KEY_USER = "users"
         const val KEY_COLLECTION_CHAT = "chat"
         const val KEY_SENDER_ID = "senderId"
         const val KEY_RECEIVER_ID = "receiverId"
         const val KEY_MESSAGE = "message"
         const val KEY_TIMES_TEMP = "timesTemp"
         const val KEY_COLLECTION_CONVERSATIONS = "conversation"
         const val KEY_SENDER_NAME = "senderName"
         const val KEY_RECEIVER_NAME = "receiverName"
         const val KEY_SENDER_IMAGE = "senderImage"
         const val KEY_RECEIVER_IMAGE = "receiverImage"
         const val KEY_LAST_MESSAGE = "lastMessage"
         const val KEY_AVAILABILITY = "availability"
         private const val REMOTE_MSG_AUTHORIZATION = "Authorization"
         private const val REMOTE_MSG_CONTENT_TYPE = "Content-Tape"
         const val REMOTE_MSG_REGISTRATION_IDS = "registration_ids"
         const val REMOTE_MSG_DATA = "data"





     private var remoteMsgHeaders : HashMap<String,String>?= null
         fun getRemoteMsgHeaders( ): HashMap<String, String> {

             if (remoteMsgHeaders == null){
                 remoteMsgHeaders = HashMap()
                 val hM : HashMap<String,String> = this.remoteMsgHeaders!!
                 hM[REMOTE_MSG_AUTHORIZATION] = "key = AAAANfWE6jY:APA91bEpLLqFGaFjt8J-i5Ni9XWDMRHuJm-TDGv1XcVCcFRDBY0NedeD39dOf3e1WPTJWfGjavRFhe-agEofs9rSY3--Bx_lk0LyrfvwbwOq1wqZTSIITQMQePs4_YbiMq9Kz80ec3o0"
                 hM[REMOTE_MSG_CONTENT_TYPE] = "application/json"
             }
             return remoteMsgHeaders as HashMap<String, String>
         }
         }





}