package com.murat.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object  ApiClient {

    val BASE_URL = "https://fcm.googleapis.com/fcm/"
    private var retrofit : Retrofit? = null
    val client :Retrofit
        get() {
           if (retrofit == null){
               retrofit = Retrofit.Builder()
                   .baseUrl(BASE_URL)
                   .addConverterFactory(GsonConverterFactory.create())
                   .build()
           }
            return retrofit!!
        }

    /* private var retrofit : Retrofit? = null

    fun getClient():Retrofit{
        if (retrofit == null){
           retrofit = Retrofit.Builder().baseUrl("https://fcm.googleapis.com/fcm/")
              .addConverterFactory(ScalarsConverterFactory.create())
              .build()
        }
        return retrofit!!
     }*/
}