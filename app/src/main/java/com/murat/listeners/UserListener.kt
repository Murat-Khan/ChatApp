package com.murat.listeners

import com.murat.chatapp.model.User

interface UserListener {
    fun onUserClicked(user:User)
}