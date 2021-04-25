package com.example.chatappclone.Notifications

import android.accessibilityservice.GestureDescription
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.example.chatappclone.MessageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessaging :FirebaseMessagingService()
{
    override fun onMessageReceived(mRemoteMessage: RemoteMessage)
    {
        super.onMessageReceived(mRemoteMessage)
        val sented = mRemoteMessage.data["sented"]

        val user = mRemoteMessage.data["user"]
        val sharePref = getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        val currenOnLinrUser = sharePref.getString("currentUser", "none")
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser!=null && sented==firebaseUser.uid)
        {
            if (currenOnLinrUser != user)
            {
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
                {
                   /*sendOreNotification(mRemoteMessage)*/
                }
                else
                {
                    sendNotification(mRemoteMessage)
                }
            }
        }
    }

    private fun sendNotification(mRemoteMessage: RemoteMessage)
    {

        val user = mRemoteMessage.data["user"]
        val icon = mRemoteMessage.data["icon"]
        val title = mRemoteMessage.data["title"]
        val body = mRemoteMessage.data["body"]

        val notification = mRemoteMessage.notification
        var j = user!!.replace("[\\D]".toRegex(), "").toInt()
        val intent = Intent(this, MessageActivity::class.java)
        val bundle = Bundle()
        bundle.putString("userid", user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

        val pendingIntent = PendingIntent.getActivity(this, j , intent,PendingIntent.FLAG_ONE_SHOT)
        val defautSount = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val builder:NotificationCompat.Builder=NotificationCompat.Builder(this)
            .setSmallIcon(icon!!.toInt())
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defautSount)
            .setContentIntent(pendingIntent)
        val noti=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager



        var i = 0
        if (j> 0)
        {
            j = i
        }
        noti.notify(j,builder.build())

    }

    private fun sendOreNotification(mRemoteMessage: RemoteMessage)
    {
        val user = mRemoteMessage.data["user"]
        val icon = mRemoteMessage.data["icon"]
        val title = mRemoteMessage.data["title"]
        val body = mRemoteMessage.data["body"]

        val notification = mRemoteMessage.notification
        var j = user!!.replace("[\\D]".toRegex(), "").toInt()
        val intent = Intent(this, MessageActivity::class.java)
        val bundle = Bundle()
        bundle.putString("userid", user)
        intent.putExtras(bundle)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

        val pendingIntent = PendingIntent.getActivity(this, j , intent,PendingIntent.FLAG_ONE_SHOT)
        val defautSount = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val oreNotification = OrenNotification(this)
        val builder:Notification.Builder =oreNotification.getOrenNotification(title, body,pendingIntent,defautSount,icon)
        var i = 0
        if (j> 0)
        {
            j = i
        }
        /*oreNotification.getManager!!.notify(j,builder.build())*/

    }
}