package com.reward.dne.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.preference.PreferenceManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.inputmethod.InputMethodManager
import com.reward.dne.application.RewardApplication
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

object UtilsDefault {

    val STATUS_SUCCESS = 200
    val STATUS_TOKENEXPIRE = 401
    val STATUS_ERROR = 402
    val NORESULT = 400
    val TOKEN_NOT_PROVIDED = 404
    val PAYTM_FAILURE = 500
    val PAYTM_SUCCESS = "DE_001"

    internal var letter = Pattern.compile("[a-zA-z]")
    internal var digit = Pattern.compile("[0-9]")

    val SHOULD_PRINT_LOG = false

    val sBuildType = BuildType.PROD

    fun debugLog(tag: String, message: String) {
        if (SHOULD_PRINT_LOG) {
            Log.d(tag, message)
        }
    }

    fun errorLog(tag: String, message: String) {
        if (SHOULD_PRINT_LOG) {
            //
        }
        Log.e(tag, message)
    }

    fun infoLog(tag: String, message: String) {
        if (SHOULD_PRINT_LOG) {
            Log.i(tag, message)
        }
    }


    private val SHARED_PREFERENCE_UTILS = "Reward"
    private val FCMKEYSHAREDPERFRENCES ="Fcmpreference"

    private var sharedPreferences: SharedPreferences? = null
    private var sharedPreferencesfcm : SharedPreferences? = null

    private fun initializeSharedPreference() {

        sharedPreferences = RewardApplication.instance.getSharedPreferences(SHARED_PREFERENCE_UTILS,
                Context.MODE_PRIVATE)
    }

    private fun initializeSharedPreferencefcm() {

        sharedPreferencesfcm = RewardApplication.instance.getSharedPreferences(FCMKEYSHAREDPERFRENCES,
                Context.MODE_PRIVATE)
    }

    fun updateSharedPreferenceFCM(key: String, value: String) {
        if (sharedPreferencesfcm == null) {
            initializeSharedPreferencefcm()
        }

        val editor = sharedPreferencesfcm!!.edit()
        editor.putString(key, value)
        editor.commit()
    }

    fun updateSharedPreferenceString(key: String, value: String) {
        if (sharedPreferences == null) {
            initializeSharedPreference()
        }

        val editor = sharedPreferences!!.edit()
        editor.putString(key, value)
        editor.commit()
    }


    fun updateSharedPreferenceInt(key: String, value: Int) {
        if (sharedPreferences == null) {
            initializeSharedPreference()
        }

        val editor = sharedPreferences!!.edit()
        editor.putInt(key, value)
        editor.commit()
    }


    fun updateSharedPreferenceBoolean(key: String, value: Boolean) {
        if (sharedPreferences == null) {
            initializeSharedPreference()
        }

        val editor = sharedPreferences!!.edit()
        editor.putBoolean(key, value)
        editor.commit()
    }


    fun setLoggedIn(context: Context, value: Boolean) {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        val ed = sp.edit()
        ed.putBoolean(Constants.LOGIN_STATUS, value)
        ed.commit()
    }

    fun isLoggedIn(context: Context): Boolean {
        val sp = PreferenceManager.getDefaultSharedPreferences(context)
        return sp.getBoolean(Constants.LOGIN_STATUS, false)
    }

    fun getSharedPreferenceValuefcm (key: String?): String? {
        if (sharedPreferencesfcm == null) {
            initializeSharedPreferencefcm()
        }

        return if (key != null) {
            sharedPreferencesfcm!!.getString(key, null)
        } else {
            null
        }
    }

    fun getSharedPreferenceString(key: String?): String? {
        if (sharedPreferences == null) {
            initializeSharedPreference()
        }

        return if (key != null) {
            sharedPreferences!!.getString(key, null)
        } else {
            null
        }
    }

    fun getSharedPreferenceInt(key: String?): Int {
        if (sharedPreferences == null) {
            initializeSharedPreference()
        }
        return if (key != null) {
            sharedPreferences!!.getInt(key, 0)
        } else {
            0
        }
    }

    fun getSharedPreferenceBoolean(key: String?): Boolean {
        if (sharedPreferences == null) {
            initializeSharedPreference()
        }
        return key != null && sharedPreferences!!.getBoolean(key, false)
    }



    fun checkNull(data: String?): String {
        return data ?: ""
    }

//    public static int checkIntNull(int data){
//        if (data==null){
//            return 0;
//        }else {
//            return data;
//        }
//    }

    fun ok(password: String): Boolean {
        val hasLetter = letter.matcher(password)
        val hasDigit = digit.matcher(password)
        return hasLetter.find() && hasDigit.find()
    }

    fun printException(exception: Exception) {
        exception.printStackTrace()
    }

    fun isNetworkConnected(): Boolean {

        val manager = RewardApplication.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val allNetworks = manager?.allNetworks?.let { it } ?: return false
            allNetworks.forEach { network ->
                val info = manager.getNetworkInfo(network)
                if (info.state == NetworkInfo.State.CONNECTED) return true
            }
        } else {
            val allNetworkInfo = manager?.allNetworkInfo?.let { it } ?: return false
            allNetworkInfo.forEach { info ->
                if (info.state == NetworkInfo.State.CONNECTED) return true
            }
        }
        return false
    }

    fun isOnline(): Boolean {
        var haveConnectedWifi = false
        var haveConnectedMobile = false
        val cm = RewardApplication.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (cm != null){
            if (Build.VERSION.SDK_INT < 23) {
                val netInfo = cm.activeNetworkInfo
                if (netInfo != null) {
                    return (netInfo.isConnected() && (netInfo.getType() == ConnectivityManager.TYPE_WIFI || netInfo.getType() == ConnectivityManager.TYPE_MOBILE));
                }
            } else {
                val netInfo = cm.activeNetwork
                if (netInfo != null) {
                    val nc = cm.getNetworkCapabilities(netInfo);

                    return (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
                }
            }
        }

        return haveConnectedWifi || haveConnectedMobile
    }

    /*fun isOnline(): Boolean {
        val cm = RewardApplication.instance.getSystemService(
                Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }*/

    enum class BuildType {
        QA, PROD
    }

    fun isTablet(context: Context): Boolean {
        return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    fun isEmailValid(email: String): Boolean {

        val ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$"
        val p = java.util.regex.Pattern.compile(ePattern)
        val m = p.matcher(email)
        return m.matches()

    }

    fun clearSession() {

        if (sharedPreferences == null) {
            initializeSharedPreference()
        }
        sharedPreferences!!.edit().clear().apply()
    }

    fun validate(password: String): Boolean {
        return password
                .matches("(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%&*()_+=|<>?{}\\[\\]~-])".toRegex())
    }

    fun isEmailPassword(email: String): Boolean {

        val flag: Boolean
        val expression = "(?=.*[a-z])(?=.*\\d)"

        val inputStr = email.trim { it <= ' ' }
        val pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(inputStr)

        flag = matcher.matches()
        return flag

    }


    fun checkScreensize(context: Activity): Int {
        val dm = DisplayMetrics()
        context.windowManager.defaultDisplay.getMetrics(dm)
        val width = dm.widthPixels
        val height = dm.heightPixels
        val dens = dm.densityDpi
        val wi = width.toDouble() / dens.toDouble()
        val hi = height.toDouble() / dens.toDouble()
        val x = Math.pow(wi, 2.0)
        val y = Math.pow(hi, 2.0)
        val screenInches = Math.sqrt(x + y)
        val finalVal = Math.round(screenInches).toDouble()

        return finalVal.toInt()
    }


    fun hideKeyboardForFocusedView(activity: Activity) {
        val inputManager = activity
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = activity.currentFocus
        if (view != null) {
            inputManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun showAllShareApp(): List<ResolveInfo> {
        var mApps: List<ResolveInfo> = ArrayList()
        val intent = Intent(Intent.ACTION_SEND, null)
        intent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
        intent.type = "text/plain"
        val pManager = RewardApplication.instance.getPackageManager()
        mApps = pManager.queryIntentActivities(intent,
                PackageManager.COMPONENT_ENABLED_STATE_DEFAULT)
        return mApps
    }


    @SuppressLint("SimpleDateFormat")
    fun currentDate(): String {
        val c = Calendar.getInstance()
        val df = SimpleDateFormat("dd MMM yyyy")
        return df.format(c.time)
    }
}