package com.example.chatappclone.Notifications

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import android.os.Build
import android.service.quicksettings.Tile
import com.google.firebase.messaging.RemoteMessage

class OrenNotification(base:Context?):ContextWrapper(base)
{

    private var notificationManager:NotificationManager?=null




    companion object
    {
        private const val CHANNEL_ID="com.example.chatappclone"
        private const val CHANNEL_NAME="ChatApp"
    }

    init {

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {
            createChanel()
        }
    }
    @TargetApi(Build.VERSION_CODES.O)
    private fun createChanel()
    {
        val chanel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )

        chanel.enableLights(false)
        chanel.enableVibration(true)
        chanel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        getManager!!.createNotificationChannel(chanel)
    }

    val getManager:NotificationManager?get()
    {
        if (notificationManager==null)
        {
            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
        return notificationManager
    }
    @TargetApi(Build.VERSION_CODES.O)
    fun getOrenNotification(
        title:String?,
        body:String? ,
        pendingIntent:PendingIntent? ,
        soundUri:Uri? ,
        icon:String?):Notification.Builder
    {
        return Notification.Builder(applicationContext, CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(icon!!.toInt())
            .setSound(soundUri)
            .setAutoCancel(true)

    }



}

