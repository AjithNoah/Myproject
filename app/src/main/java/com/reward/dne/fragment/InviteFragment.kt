package com.reward.dne.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.reward.dne.R
import com.reward.dne.activity.MainActivity
import com.reward.dne.activity.MainActivity.Companion.mainBack
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_invite.*
import android.widget.Toast
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.PackageInfo
import android.net.Uri
import android.content.ComponentName
import android.support.v4.text.HtmlCompat
import android.text.Html
import com.reward.dne.utils.Constants
import com.reward.dne.utils.UtilsDefault
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import kotlinx.android.synthetic.main.activity_app_detail.*
import android.support.v4.content.ContextCompat.startActivity

class InviteFragment : BaseFragment() {

    var referralcode = ""
    var link = ""
    var msg = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_invite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainBack = 1
        (activity as MainActivity).headerName.text = getString(R.string.invite_to_earn)
        if (!UtilsDefault.getSharedPreferenceString(Constants.MYREFFERAL_CODE).equals("")) {
            referralcode = UtilsDefault.getSharedPreferenceString(Constants.MYREFFERAL_CODE)!!
            textMyReferral.text = referralcode
        }

        val dynamiclink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://downloadandearn.com/-$referralcode"))
                .setDynamicLinkDomain("rewarddne.page.link")
                // Open links with this app on Android
                .setAndroidParameters(DynamicLink.AndroidParameters.Builder("com.reward.dne").build())
                .buildDynamicLink()

        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(dynamiclink.uri.toString()))
                .buildShortDynamicLink()
                .addOnSuccessListener { result ->
                    // Short link created
                    val shortLink = result.shortLink
                    val flowchartLink = result.previewLink
                    if (shortLink != null) {
                        link = shortLink.toString()
                        msg = "Earn mobile recharge by exploring new apps!" +
                                " \n Download RewardApp to get started!" +
                                " \n Please sign up here " + "\n \n" + link
                    } else {
                        link = ""
                        Toast.makeText(activity, getString(R.string.there_is_a_problem_in_generating_link), Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    link = ""
                    Toast.makeText(activity, getString(R.string.there_is_a_problem_in_generating_link), Toast.LENGTH_SHORT).show()

                }

        imgFb.setOnClickListener {
            when {
                appInstalledOrNot("com.facebook.orca") -> {

                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(Intent.EXTRA_TEXT, msg)
                    sendIntent.type = "text/plain"
                    sendIntent.setPackage("com.facebook.orca")
                    try {
                        startActivity(sendIntent)
                    } catch (ex: android.content.ActivityNotFoundException) {
                        Toast.makeText(context, "Please Install Facebook Messenger", Toast.LENGTH_LONG).show()
                    }
                }
                appInstalledOrNot("com.facebook.mlite") -> {

                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_SEND
                    sendIntent.putExtra(Intent.EXTRA_TEXT, msg)
                    sendIntent.type = "text/plain"
                    sendIntent.setPackage("com.facebook.mlite")

                    try {
                        startActivity(sendIntent)
                    } catch (ex: android.content.ActivityNotFoundException) {
                        Toast.makeText(context, "Please Install Facebook Messenger", Toast.LENGTH_LONG).show()
                    }
                }
                else -> Toast.makeText(context, "Please Install Facebook Messenger", Toast.LENGTH_LONG).show()
            }


        }
        imgTwitter.setOnClickListener {

            when {
                appInstalledOrNot("com.twitter.android") -> try {
                    val tweetIntent = Intent(Intent.ACTION_SEND)
                    tweetIntent.putExtra(Intent.EXTRA_TEXT, msg)
                    tweetIntent.type = "text/plain"
                    tweetIntent.setPackage("com.twitter.android")
                    startActivity(tweetIntent)

                } catch (e: Exception) {
                    // no Twitter app, revert to browser
                    // intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/dotcominfoway"))
                    Toast.makeText(activity, "Please install Twitter App", Toast.LENGTH_SHORT).show()
                }
                appInstalledOrNot("com.twitter.android.lite") -> try {
                    val tweetIntent = Intent(Intent.ACTION_SEND)
                    tweetIntent.putExtra(Intent.EXTRA_TEXT, msg)
                    tweetIntent.type = "text/plain"
                    tweetIntent.setPackage("com.twitter.android.lite")
                    startActivity(tweetIntent)

                } catch (e: Exception) {
                    // no Twitter app, revert to browser
                    // intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/dotcominfoway"))
                    Toast.makeText(activity, "Please install Twitter App", Toast.LENGTH_SHORT).show()
                }
                else -> Toast.makeText(activity, "Please install Twitter App", Toast.LENGTH_SHORT).show()
            }

        }
        imgMail.setOnClickListener {
            if (link != "") {

                var email = ""
                var subject = "Download and Earn"
                val msg = "<HTML><BODY>Earn mobile recharge by exploring new apps!<br>Download <b><font color=\"red\">" + "RewardApp" + "</font></b>" +
                        " to get started!<br>Please sign up here.<br><br><a href=" + link + ">" + link + "</a><br><br>" +
                        "<br>Thank You</BODY></HTML>"
                val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(msg))
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)

                startActivity(Intent.createChooser(emailIntent, getString(R.string.app_name)))
            } else {
                Toast.makeText(activity, getString(R.string.there_is_a_problem_in_generating_link), Toast.LENGTH_SHORT).show()
            }

        }
        imgWhatsapp.setOnClickListener {
            try {

                val waIntent = Intent(Intent.ACTION_SEND)
                waIntent.type = "text/plain"
                waIntent.setPackage("com.whatsapp")
                waIntent.putExtra(Intent.EXTRA_TEXT, msg)
                startActivity(Intent.createChooser(waIntent, "Share with"))

            } catch (e: Exception) {
                Toast.makeText(activity, "Please install WhatsApp", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun appInstalledOrNot(uri: String): Boolean {
        val pm = activity!!.packageManager
        var app_installed = false
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            app_installed = true
        } catch (e: PackageManager.NameNotFoundException) {
            app_installed = false
        }

        return app_installed
    }


}
