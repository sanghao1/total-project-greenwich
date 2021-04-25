package com.example.chatappclone.Notifications

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Client
{
    object Client
    {
        private var retroit:Retrofit? =null

        fun getClient(uri:String) : Retrofit
        {
            if (retroit == null)
            {
                retroit = Retrofit.Builder()
                    .baseUrl(uri)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retroit!!
        }

    }
}