package com.reward.dne.fragment


import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.reward.dne.R
import com.reward.dne.activity.LoginActivity
import com.reward.dne.activity.LoginActivity.Companion.loginBack
import com.reward.dne.activity.MainActivity
import com.reward.dne.application.RewardApplication
import com.reward.dne.model.InputParams
import com.reward.dne.model.LoginResponse
import com.reward.dne.model.OtpResponse
import com.reward.dne.model.SignUpResponseModel
import com.reward.dne.retrofit.RewardAPI
import com.reward.dne.utils.Constants
import com.reward.dne.utils.OtpTextWatcher
import com.reward.dne.utils.UtilsDefault
import kotlinx.android.synthetic.main.fragment_otp.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject
import android.os.CountDownTimer
import android.widget.RelativeLayout
import com.reward.dne.utils.CustomTextViewNormal
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_otp.view.*
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.support.v4.app.FragmentActivity
import android.support.v4.content.LocalBroadcastManager
import android.util.Base64
import android.widget.TextView
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.credentials.Credential
import com.google.android.gms.auth.api.credentials.HintRequest
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.reward.dne.smsretriever.AppSignatureHelper
import com.reward.dne.smsretriever.MySMSBroadcastReceiver
import com.reward.dne.utils.SmsListener
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*


class OtpFragment : BaseFragment(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, MySMSBroadcastReceiver.OTPReceiveListener {

    var TAG: String = "OtpFragment"
    @Inject
    lateinit var rewardAPI: RewardAPI
    var signUpResponseModel: SignUpResponseModel? = null
    var otpResponse:OtpResponse? = null
    var referralcode = ""
    var deviceId = ""
    var mobile = ""
    var isLogin = true
    var otp = ""
    var countDownTimer:CountDownTimer?=null
    lateinit var textTimer:CustomTextViewNormal
    lateinit var resendOTP:RelativeLayout


    var mCredentialsApiClient: GoogleApiClient? = null
    private val KEY_IS_RESOLVING = "is_resolving"
    private val RC_HINT = 2
    private var otpReceiver: MySMSBroadcastReceiver.OTPReceiveListener = this
    //lateinit var otpTxtView: TextView
    val smsBroadcast = MySMSBroadcastReceiver()

    private val HASH_TYPE = "SHA-256"
    val NUM_HASHED_BYTES = 9
    val NUM_BASE64_CHAR = 11

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_otp, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        RewardApplication.instance.getComponent().inject(this)
        loginBack = 2
        textTimer = view.findViewById(R.id.textTimer)
        resendOTP = view.findViewById(R.id.resendOTP)

        mCredentialsApiClient = GoogleApiClient.Builder(context!!)
                .addConnectionCallbacks(this)
                //.enableAutoManage(this, this)
                .enableAutoManage((activity as FragmentActivity), this)
                .addApi(Auth.CREDENTIALS_API)
                .build()

        val args = arguments
        mobile = args?.getString("mobile")!!
        isLogin = args.getBoolean("isFromLogin")
        if(!isLogin){
            referralcode = args.getString("referral")!!
            deviceId = args.getString("deviceID")!!
        }
        if (mobile !=""){
            sendOTP(mobile)
        }

        resendOTP.setOnClickListener {
            sendOTP(mobile)
            resendOTP.visibility = View.GONE
            if (countDownTimer!=null){
                countDownTimer!!.start()
            }
        }

        countDownTimer = object :CountDownTimer(30000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                textTimer.setText("otp time remaining: " + millisUntilFinished / 1000)
                //here you can have your logic to set text to edittext
            }

            override fun onFinish() {
                resendOTP.visibility = View.VISIBLE
                textTimer.setText("If OTP not recieved retry otp!")
            }

        }.start()

        textInfo.text = "Please enter verification code \n sent to +91 $mobile"

        etOtp1.addTextChangedListener(OtpTextWatcher(etOtp2, etOtp1))
        etOtp2.addTextChangedListener(OtpTextWatcher(etOtp3, etOtp1))
        etOtp3.addTextChangedListener(OtpTextWatcher(etOtp4, etOtp2))
        etOtp4.addTextChangedListener(OtpTextWatcher(etOtp4, etOtp3))


        relativeOtp.setOnClickListener {
            if (otp_pin_view.text.toString().length == 4){
                if (isLogin){
                    if (otp == otp_pin_view.text.toString()){
                        context!!.unregisterReceiver(smsBroadcast)
                        Toast.makeText(activity, getString(R.string.otp_verified), Toast.LENGTH_SHORT).show()
                        val intent = Intent(activity, MainActivity::class.java)
                        startActivity(intent)
                        activity!!.finish()
                    }
                    else {
                        Toast.makeText(activity, getString(R.string.incorrect_otp), Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    if (otp == otp_pin_view.text.toString()){
                        if (countDownTimer!=null){
                            countDownTimer!!.cancel()
                        }
                        Toast.makeText(activity, getString(R.string.otp_verified), Toast.LENGTH_SHORT).show()
                        signUpCall(mobile,deviceId)
                    }
                    else {
                        Toast.makeText(activity, getString(R.string.incorrect_otp), Toast.LENGTH_SHORT).show()
                    }

                }
            }
        }

        OtpParent.setOnTouchListener { view, motionEvent ->
            UtilsDefault.hideKeyboardForFocusedView(activity!!)
            false
        }



        /*e1.addTextChangedListener(GenericTextWatcher(e2, e1))
        e2.addTextChangedListener(GenericTextWatcher(e3, e1))
        e3.addTextChangedListener(GenericTextWatcher(e4, e2))
        e4.addTextChangedListener(GenericTextWatcher(e5, e3))
        e5.addTextChangedListener(GenericTextWatcher(e6, e4))
        e6.addTextChangedListener(GenericTextWatcher(e6, e5))*/
    }

    /*val broadCastReceiver = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {

            if (intent != null && intent.hasExtra("message")) {
                var youtOTPcode = intent.getStringExtra("message")
              //  Toast.makeText(context, ""+youtOTPcode, Toast.LENGTH_SHORT).show()
                otp_pin_view.setText(youtOTPcode)
                if (isLogin){
                    if (otp == otp_pin_view.text.toString()){
                        Toast.makeText(activity, getString(R.string.otp_verified), Toast.LENGTH_SHORT).show()
                        val intent = Intent(activity, MainActivity::class.java)
                        startActivity(intent)
                        activity!!.finish()
                    }
                    else {
                        Toast.makeText(activity, getString(R.string.incorrect_otp), Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    if (otp == otp_pin_view.text.toString()){
                        if (countDownTimer!=null){
                            countDownTimer!!.cancel()
                        }
                        Toast.makeText(activity, getString(R.string.otp_verified), Toast.LENGTH_SHORT).show()
                        signUpCall(mobile,deviceId)
                    }
                    else {
                        Toast.makeText(activity, getString(R.string.incorrect_otp), Toast.LENGTH_SHORT).show()
                    }

                }
                Log.d("otp",youtOTPcode)
            }
        }
    }*/

    override fun onResume() {
        super.onResume()
        //LocalBroadcastManager.getInstance(activity!!).registerReceiver(broadCastReceiver,IntentFilter("otp"))
    }

    override fun onPause() {
        super.onPause()
        //LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(broadCastReceiver);
    }

    private fun hash(packageName: String, signature: String): String? {
        val appInfo = "$packageName $signature"
        try {
            val messageDigest = MessageDigest.getInstance(HASH_TYPE)
            messageDigest.update(appInfo.toByteArray(StandardCharsets.UTF_8))
            var hashSignature = messageDigest.digest()

            // truncated into NUM_HASHED_BYTES
            hashSignature = Arrays.copyOfRange(hashSignature, 0, NUM_HASHED_BYTES)
            // encode into Base64
            var base64Hash = Base64.encodeToString(hashSignature, Base64.NO_PADDING or Base64.NO_WRAP)
            base64Hash = base64Hash.substring(0, NUM_BASE64_CHAR)

            Log.e(TAG, String.format("pkg: %s -- hash: %s", packageName, base64Hash))
            return base64Hash
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "hash:NoSuchAlgorithm", e)
        }

        return null
    }

    private fun sendOTP(mobile: String) {
        var packageID = "";
        try {
            // Get all package signatures for the current package
            val packageName = context!!.getPackageName()
            val packageManager = context!!.getPackageManager()
            val signatures = packageManager.getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES).signatures

            // For each signature create a compatible hash
            for (signature in signatures) {
                packageID = hash(packageName, signature.toCharsString())!!
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "Unable to find package to obtain hash.", e)
        }

        println("packageID ==> "+packageID)

        if (UtilsDefault.isOnline()) {
            showProgress()
            val loginParams = InputParams()
            loginParams.mobile = mobile
            loginParams.packageID = packageID
            rewardAPI.sendotp(loginParams).enqueue(object : Callback<OtpResponse> {
                override fun onResponse(call: Call<OtpResponse>, response: Response<OtpResponse>) {
                    hideProgress()
                    if (response.body() != null && response.isSuccessful && response.code() == 200) {
                        otpResponse = response.body()
                        if (otpResponse!!.status == UtilsDefault.STATUS_SUCCESS) {

                            val header = response.headers().get(getString(R.string.token))
                            if (header != null) {
                                UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, header)
                            } else {
                                UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN).toString())
                            }

                            if (otpResponse?.OTPNumber!=null) {
                                startSMSListener()

                                smsBroadcast.initOTPListener(this@OtpFragment)
                                val intentFilter = IntentFilter()
                                intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)

                                context!!.registerReceiver(smsBroadcast, intentFilter)
                                otp = otpResponse?.OTPNumber!!
                            }
                            else {
                                Toast.makeText(context, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show()
                            }

                        } else {
                            Toast.makeText(context, otpResponse!!.message, Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onFailure(call: Call<OtpResponse>, t: Throwable) {
                    hideProgress()
                    Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT).show()

                }
            })
        } else {
            Toast.makeText(context, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }



    private fun signUpCall(mobile: String, deviceId: String) {

        if (UtilsDefault.isOnline()) {
            showProgress()
            val offerParams = InputParams()
            offerParams.mobile = mobile
            offerParams.device_id = deviceId
            offerParams.invite_code = referralcode
            offerParams.fcm_key = UtilsDefault.getSharedPreferenceValuefcm(Constants.FCM_KEY)
            rewardAPI.signup(offerParams).enqueue(object : Callback<SignUpResponseModel> {

                override fun onResponse(call: Call<SignUpResponseModel>, response: Response<SignUpResponseModel>) {
                    hideProgress()
                    signUpResponseModel = response.body()
                    Log.i(TAG, response.body().toString())
                    if (response.body() != null && response.isSuccessful && response.code() == 200) {

                        val header = response.headers().get(getString(R.string.token))
                        if (header != null) {
                            UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, header)
                        } else {
                            UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN).toString())
                        }

                        if (signUpResponseModel?.status == UtilsDefault.STATUS_SUCCESS) {

                            if (signUpResponseModel?.data!=null){

                                UtilsDefault.updateSharedPreferenceInt(Constants.USERID, signUpResponseModel?.data?.id!!)
                                UtilsDefault.updateSharedPreferenceString(Constants.MYREFFERAL_CODE, signUpResponseModel?.data?.referral_code!!)
                                context!!.unregisterReceiver(smsBroadcast)
                                val intent = Intent(activity, MainActivity::class.java)
                                startActivity(intent)
                                activity!!.finish()
                                Toast.makeText(activity, signUpResponseModel!!.message.toString(), Toast.LENGTH_SHORT).show()
                            }
                            else {
                                Toast.makeText(context, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show()
                            }


                        } else {
                            Toast.makeText(activity, signUpResponseModel!!.message.toString(), Toast.LENGTH_SHORT).show()

                        }
                    } else {
                        Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onFailure(call: Call<SignUpResponseModel>, t: Throwable) {
                    hideProgress()
                    Toast.makeText(activity, getString(R.string.server_error), Toast.LENGTH_SHORT).show()

                }


            })

        } else {
            Toast.makeText(activity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()

        }
    }


    private fun startSMSListener() {

        val client = SmsRetriever.getClient(context!! /* context */)
        val task = client.startSmsRetriever()
        task.addOnSuccessListener {
            // Successfully started retriever, expect broadcast intent
            // ...
            //otpTxtView.text = "Waiting for the OTP"
            //Toast.makeText(context!!, "SMS Retriever starts", Toast.LENGTH_LONG).show()
        }

        task.addOnFailureListener {
            //otpTxtView.text = "Cannot Start SMS Retriever"
            //Toast.makeText(context!!, "Error", Toast.LENGTH_LONG).show()
        }
    }

    @SuppressLint("LongLogTag")
    private fun requestHint() {
        val hintRequest = HintRequest.Builder()
                .setPhoneNumberIdentifierSupported(true)
                .build()

        val intent = Auth.CredentialsApi.getHintPickerIntent(
                mCredentialsApiClient, hintRequest)

        try {
            startIntentSenderForResult(intent.intentSender, RC_HINT, null, 0, 0, 0,null)
        } catch (e: Exception) {
            Log.e("Error In getting Message", e.message)
        }

    }


    override fun onConnected(p0: Bundle?) {

    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }

    override fun onOTPReceived(otp1: String) {
        if (smsBroadcast != null) {
            LocalBroadcastManager.getInstance(context!!).unregisterReceiver(smsBroadcast)
        }
        //Toast.makeText(context!!, otp1, Toast.LENGTH_SHORT).show()
        //otpTxtView.text = "Your OTP is: $otp"
        Log.e("OTP Received", otp1)

        otp_pin_view.setText(otp1)
        if (isLogin){
            if (otp == otp_pin_view.text.toString()){
                Toast.makeText(activity, getString(R.string.otp_verified), Toast.LENGTH_SHORT).show()
                context!!.unregisterReceiver(smsBroadcast)
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                activity!!.finish()
            }
            else {
                Toast.makeText(activity, getString(R.string.incorrect_otp), Toast.LENGTH_SHORT).show()
            }
        }
        else{
            if (otp == otp_pin_view.text.toString()){
                if (countDownTimer!=null){
                    countDownTimer!!.cancel()
                }
                Toast.makeText(activity, getString(R.string.otp_verified), Toast.LENGTH_SHORT).show()
                signUpCall(mobile,deviceId)
            }
            else {
                Toast.makeText(activity, getString(R.string.incorrect_otp), Toast.LENGTH_SHORT).show()
            }

        }
        Log.d("otp ===> ",otp)

    }

    override fun onOTPTimeOut() {
        //otpTxtView.setText("Timeout")
        //Toast.makeText(context!!, " SMS retriever API Timeout", Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_HINT && resultCode == Activity.RESULT_OK) {

            /*You will receive user selected phone number here if selected and send it to the server for request the otp*/
            var credential: Credential = data!!.getParcelableExtra(Credential.EXTRA_KEY)

        }
    }


}
