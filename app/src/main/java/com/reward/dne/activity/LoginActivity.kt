package com.reward.dne.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.util.Log
import android.widget.Toast
import com.reward.dne.R
import com.reward.dne.fragment.LoginFragment
import com.reward.dne.fragment.SignUpFragment
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks


class LoginActivity : BaseActivity() {

    companion object {
        var loginBack: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val intent = intent
        if (intent != null) {
            /*val deviceCheck = intent.getIntExtra("isDeviceExist",2)
            if (deviceCheck ==1){
                push(LoginFragment())
            }
            else {
                handleDeepLink(intent)
            }*/
            handleDeepLink(intent)

        } else {
            push(LoginFragment())
        }

    }



    fun popBackStackImmediate() {
        val fragmentManager = supportFragmentManager
        fragmentManager.popBackStack()
        fragmentManager.executePendingTransactions()
    }

    fun popAllBackstack() {
        val fragmentManager = supportFragmentManager
        val backCount = fragmentManager.backStackEntryCount
        if (backCount > 0) {
            fragmentManager.popBackStackImmediate(fragmentManager.getBackStackEntryAt(0).id,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    fun push(fragment: Fragment, title: String?) {
        val fragmentManager = supportFragmentManager
        val tag = fragment.javaClass.canonicalName

        if (title != null) {
            try {
                fragmentManager.beginTransaction()
                        .replace(R.id.loginContainer, fragment, tag)
                        .addToBackStack(title)
                        .commit()
            } catch (ile: IllegalStateException) {
                fragmentManager.beginTransaction()
                        .replace(R.id.loginContainer, fragment, tag)
                        .addToBackStack(title)
                        .commitAllowingStateLoss()
            }

        } else {
            try {
                fragmentManager.beginTransaction()
                        .replace(R.id.loginContainer, fragment, tag).commit()
            } catch (ile: IllegalStateException) {
                fragmentManager.beginTransaction()
                        .replace(R.id.loginContainer, fragment, tag).commitAllowingStateLoss()
            }

        }

    }

    private fun handleDeepLink(intent: Intent) {
        FirebaseAnalytics.getInstance(this)
        FirebaseDynamicLinks.getInstance().getDynamicLink(intent)
                .addOnSuccessListener(this) { pendingDynamicLinkData ->
                    var deepLink: Uri? = null
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.link
                    }
                    if (deepLink != null) {

                        val deepLinkResponse = deepLink.toString()
                        val split = deepLinkResponse.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                        val start = split[0] // https://www.downloadandearn.com/-
                        val end = split[1] //545464654

                        val args = Bundle()
                        args.putString("referral", end)
                        val signUpFragment = SignUpFragment()
                        signUpFragment.arguments = args
                        push(signUpFragment)
                        Log.d("Login", "Promotion applied")
                        Log.d("Main", deepLinkResponse.toString())
                    } else {
                        push(LoginFragment())
                        Log.d("Login", "No Promotion applied")
                    }

                }
                .addOnFailureListener(this) { e ->
                    Log.d("Main", "getDynamicLink:onFailure", e)
                }
    }

    fun push(fragment: Fragment?) {
        if (fragment == null) {
            return
        }
        push(fragment, null)
    }

    override fun onBackPressed() {
        if (loginBack == 2) {
            push(LoginFragment())
        } else {
            super.onBackPressed()
        }
    }
}
