package com.reward.dne.fragment


import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
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
import com.reward.dne.retrofit.RewardAPI
import com.reward.dne.utils.Constants
import com.reward.dne.utils.UtilsDefault
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_signup.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class LoginFragment : BaseFragment() {

    @Inject
    lateinit var rewardAPI: RewardAPI
    var loginResponse:LoginResponse? = null
    private var SMS_PERMISSION_REQUESTCODE = 15
    private var REQUESTCODE = 2

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        RewardApplication.instance.getComponent().inject(this)
        loginBack = 1

        editMobileLogin.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val text = p0.toString()
                if (text.length == 10){
                    viewEditPhoneLogin.setBackgroundColor(ContextCompat.getColor(activity!!,R.color.green))
                    imgTickLogin.visibility = View.VISIBLE
                }
                else {
                    viewEditPhoneLogin.setBackgroundColor(ContextCompat.getColor(activity!!,R.color.colorPrimary))
                    imgTickLogin.visibility = View.GONE
                }
            }

        })

        relativeRegister.setOnClickListener {
            try {
                (activity as LoginActivity).push(SignUpFragment())
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        relativeLoginNav.setOnClickListener {

            if (editMobileLogin.length()!= 10){
                editMobileLogin.error = "Enter valid mobile number"
            }
            else {
                /*if (checkPermission()){*/
                    login(editMobileLogin.text.toString())
                /*}*/
            }
        }
        loginParent.setOnTouchListener { view, motionEvent ->
            UtilsDefault.hideKeyboardForFocusedView(activity!!)
            false
        }


    }
    /*private fun checkPermission(): Boolean {

        if (ActivityCompat.checkSelfPermission(activity!!,Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.RECEIVE_SMS,Manifest.permission.READ_SMS), SMS_PERMISSION_REQUESTCODE)
            return false
        }

        return true
    }*/

    /*override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            SMS_PERMISSION_REQUESTCODE ->
                if(ActivityCompat.shouldShowRequestPermissionRationale(activity!!, Manifest.permission.READ_SMS)){
                    //denied
                    Log.e("denied", "denied");
                }else{
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        //allowed
                        if (editMobileLogin.length()!= 10){
                            editMobileLogin.error = "Enter valid mobile number"
                        }
                        else {
                            login(editMobileLogin.text.toString())
                        }
                        *//*val contactPickerIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                        startActivityForResult(contactPickerIntent, REQUESTCODE)*//*
                    } else{


                        val intent = Intent()
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        val uri = Uri.fromParts("package", activity!!.packageName, null)
                        intent.data = uri
                        startActivity(intent)
                        Toast.makeText(activity, getString(R.string.allow_permission_in_settings), Toast.LENGTH_SHORT).show()
                        //set to never ask again
                        Log.e("set to never ask again", "never ask");
                        //do something here.
                    }
                }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        }
    }*/


    private fun login(mobileNo: String) {
        if (UtilsDefault.isOnline()) {
            showProgress()
            val loginParams = InputParams()
            loginParams.mobile = mobileNo
            loginParams.fcm_key = UtilsDefault.getSharedPreferenceValuefcm(Constants.FCM_KEY)
            rewardAPI.login(loginParams).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    hideProgress()
                    if (response.body() != null && response.isSuccessful && response.code() == 200) {
                        loginResponse = response.body()
                        if (loginResponse!!.status == UtilsDefault.STATUS_SUCCESS) {

                            if (loginResponse!!.data != null) {

                                val header = response.headers().get(getString(R.string.token))
                                if (header != null) {
                                    UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, header)
                                } else {
                                    UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN).toString())
                                }

                                UtilsDefault.updateSharedPreferenceInt(Constants.USERID, loginResponse!!.data!!.id!!)
                                UtilsDefault.updateSharedPreferenceString(Constants.FIRST_NAME, loginResponse!!.data!!.firstname!!)
                                UtilsDefault.updateSharedPreferenceString(Constants.LAST_NAME, loginResponse!!.data!!.lastname!!)
                                UtilsDefault.updateSharedPreferenceString(Constants.EMAIL_ID, loginResponse!!.data!!.email!!)
                                UtilsDefault.updateSharedPreferenceString(Constants.MOBILE_NO, loginResponse!!.data!!.mobile!!)
                                UtilsDefault.updateSharedPreferenceInt(Constants.USER_TYPE, loginResponse!!.data!!.type!!)
                                UtilsDefault.updateSharedPreferenceString(Constants.AVATAR, loginResponse!!.data!!.avatar!!)
                                UtilsDefault.updateSharedPreferenceInt(Constants.GENDER, loginResponse!!.data!!.gender!!)
                                UtilsDefault.updateSharedPreferenceString(Constants.TOTAL_CREDITS, loginResponse!!.data!!.total_credit!!)
                                UtilsDefault.updateSharedPreferenceString(Constants.INVITE_CODE, loginResponse!!.data!!.invite_code!!)
                                UtilsDefault.updateSharedPreferenceString(Constants.MYREFFERAL_CODE, loginResponse!!.data!!.referral_code!!)
                                UtilsDefault.updateSharedPreferenceInt(Constants.STATUS, loginResponse!!.data!!.status!!)


                                try {
                                    val otpFragment = OtpFragment()
                                    val args = Bundle()
                                    args.putString("mobile",mobileNo)
                                    args.putBoolean("isFromLogin",true)
                                    otpFragment.arguments = args
                                    (activity as LoginActivity).push(otpFragment)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }

                            }
                            else {
                                Toast.makeText(context, loginResponse!!.message, Toast.LENGTH_SHORT).show()
                            }

                        } else {
                            Toast.makeText(context, loginResponse!!.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    else {
                        Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    hideProgress()
                    Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT).show()

                }
            })
        } else {
            Toast.makeText(context, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }


}
