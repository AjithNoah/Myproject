package com.reward.dne.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.util.Log
import android.support.v4.content.LocalBroadcastManager


class SmsListener : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {


        val bundle = intent!!.getExtras()

        try {

            if (bundle != null) {

                val pdusObj = bundle.get("pdus") as Array<Any>

                for (i in pdusObj.indices) {

                    val currentMessage = SmsMessage.createFromPdu(pdusObj[i] as ByteArray)
                    val phoneNumber = currentMessage.displayOriginatingAddress

                    val message = currentMessage.displayMessageBody.replace("\\D".toRegex(), "")

                    //message = message.substring(0, message.length()-1);
                    Log.i("SmsReceiver", "senderNum: $phoneNumber; message: $message")

                    val myIntent = Intent("otp")
                    myIntent.putExtra("message", message)
                    myIntent.putExtra("number", phoneNumber)
                    LocalBroadcastManager.getInstance(context!!).sendBroadcast(myIntent)
                    // Show Alert

                } // end for loop
            } // bundle is null

        } catch (e: Exception) {
            Log.e("SmsReceiver", "Exception smsReceiver$e")

        }


        /*if (intent!!.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            val bundle = intent.getExtras()           //---get the SMS message passed in---
            var msg_from: String
            if (bundle != null) {
                //---retrieve the SMS message received---
                try {
                    val pdus = bundle.get("pdus") as Array<Any>
                    val msgs = arrayOfNulls<SmsMessage>(pdus.size)
                    for (i in msgs.indices) {
                        if (Build.VERSION.SDK_INT <= 22) {
                            msgs[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                        } else {
                            msgs[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray, bundle.getString("format"))
                        }
                        msg_from = msgs[i]!!.getOriginatingAddress()
                        if (msg_from.contains("QPNotice")) {
                            val msgBody = msgs[i]!!.getMessageBody()
                            //String pinNo = msgBody.substring(msgBody.indexOf('"') + 1, msgBody.indexOf('"', msgBody.indexOf('"') + 2));
                            val pinNo = msgBody.replace("[^0-9]".toRegex(), "")
                            Log.d("SMS", "From -$msg_from : Body- $msgBody")
                            //CodeVerification.insertCode(pinNo);

                            // Broadcast to Auto read Code sms
                            val DISPLAY_MESSAGE_ACTION = context!!.getPackageName() + ".CodeSmsReceived"
                            val intentCodeSms = Intent(DISPLAY_MESSAGE_ACTION)
                            intentCodeSms.putExtra("varificationCode", pinNo)
                            context.sendBroadcast(intentCodeSms)
                        }
                    }
                } catch (e: Exception) {
                    Log.d("Exception caught", e.message)
                }

            }
        }*/

    }
}