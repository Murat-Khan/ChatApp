package com.murat.listeners

import com.murat.chatapp.model.User

interface ConversionListener {
    fun onConversionClicked(user: User)
}