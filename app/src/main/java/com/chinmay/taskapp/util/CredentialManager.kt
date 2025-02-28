package com.chinmay.taskapp.util

import android.content.Context
import com.chinmay.taskapp.R
import com.google.auth.oauth2.UserCredentials
import java.io.InputStream

object CredentialsManager {
    fun loadCredentials(context: Context): UserCredentials {
        val inputStream: InputStream = context.resources.openRawResource(R.raw.gemini_api_key)
        return UserCredentials.fromStream(inputStream)
    }
}
