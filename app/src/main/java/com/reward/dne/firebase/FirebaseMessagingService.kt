package com.reward.dne.firebase

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.reward.dne.R
import com.reward.dne.activity.MainActivity
import com.reward.dne.utils.Constants
import com.reward.dne.utils.UtilsDefault
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

open class FirebaseMessagingService : FirebaseMessagingService() {

    var deviceToken: String = ""
    var title: String = ""
    var message:String = ""
    internal var notificationManager: NotificationManager? = null
    internal var typeID: String? = null
    internal var intent: Intent? = null
    internal var TAG = "FirebaseMessaging"
    internal var imageUri = ""
    internal var type = ""
    internal var orderid: String? = ""
    internal var productid = ""
    internal var bitmap: Bitmap? = null

    override fun onNewToken(p0: String?) {
        super.onNewToken(p0)

        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            deviceToken = instanceIdResult.token
            Log.i("fcmtoken", deviceToken)
            UtilsDefault.updateSharedPreferenceFCM(Constants.FCM_KEY, deviceToken)
        }

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)


        // Check if message contains a data payload.
        if (remoteMessage?.data!!.isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.notification!!.body!!)
        }

        //The message which i send will have keys named [message, image, AnotherActivity] and corresponding values.
        //You can change as per the requirement.

        //message will contain the Push Message

        if (remoteMessage != null) {

            if (remoteMessage.data["body"] != null) {
                message = remoteMessage.data["body"]!!
            }
            if (remoteMessage.data["title"] != null) {
                title = remoteMessage.data["title"]!!
            }


        }

       // bitmap = getBitmapfromUrl(imageUri)

        try {
            sendNotification(message,title)
            Log.i("onmessage", "message"+message)
            /*if (UtilsDefault.getSharedPreferenceIntValue(Constants.USER_ID) !== 0) {
                if (UtilsDefault.getSharedPreferenceValue(Constants.RECEIVE_NOTIFICATION) != null && UtilsDefault.getSharedPreferenceValue(Constants.RECEIVE_NOTIFICATION).equals("1")) {
                    if (remoteMessage.data["image"] == null || remoteMessage.data["image"] == "") {
                        sendNotification(message, bitmap, title, type)
                        Log.i("onmessage", "message")
                    } else {
                        showBigNotification(bitmap, title, message)
                        Log.i("onmessage", "bitmap")
                    }
                }

            }*/
        } catch (e: Exception) {
            Log.i("onmessage", "message"+e)
            Log.i("onmessage", "message")
            e.printStackTrace()
        }


    }

    private fun sendNotification(msg: String, title: String) {

        val channelId = "REWARD_CHANNEL_ID"
        val channelName = "REWARDAPP"


        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("rewardNotification", 1)
        intent.setAction("dummy_unique_action_identifyer" + "dummy")
       // intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        Log.d("rewar","reward")
        val pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, "")
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.applogo))
                .setSmallIcon(R.mipmap.applogo)
                .setContentTitle(UtilsDefault.checkNull(title))
                .setContentText(UtilsDefault.checkNull(msg))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setStyle(NotificationCompat.BigTextStyle().bigText(msg))
                .setColor(resources.getColor(R.color.colorPrimary))
                .setDefaults(Notification.DEFAULT_SOUND or Notification.DEFAULT_LIGHTS or Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, channelName,
                    NotificationManager.IMPORTANCE_HIGH)
            channel.description = msg
            notificationManager.createNotificationChannel(channel)
            notificationBuilder.setChannelId(channelId)
        }
        val notificationID = (Date().time / 1000L % Integer.MAX_VALUE).toInt()
        notificationManager.notify(notificationID, notificationBuilder.build())
    }

    fun getBitmapfromUrl(imageUrl: String): Bitmap? {
        try {
            val url = URL("" + imageUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input = connection.inputStream
            return BitmapFactory.decodeStream(input)

        } catch (e: Exception) {
            // TODO Auto-generated catch block
            e.printStackTrace()
            return null

        }

    }
}