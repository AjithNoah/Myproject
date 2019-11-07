package com.reward.dne.fragment


import android.content.Intent
import android.os.Bundle
import android.provider.Settings
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
import com.reward.dne.model.CheckUserResponseModel
import com.reward.dne.model.InputParams
import com.reward.dne.model.SignUpResponseModel
import com.reward.dne.retrofit.RewardAPI
import com.reward.dne.utils.Constants
import com.reward.dne.utils.UtilsDefault
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_signup.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class SignUpFragment : BaseFragment() {

    var TAG: String = "SignUpFragment"
    @Inject
    lateinit var rewardAPI: RewardAPI
    var referralcode = ""

    var signUpResponseModel: SignUpResponseModel? = null
    var checkUserResponseModel: CheckUserResponseModel? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_signup, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        if (bundle != null) {
            referralcode = bundle.getString("referral")!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        RewardApplication.instance.getComponent().inject(this)
        loginBack = 2


        editMobileSignup.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                var text = p0.toString()
                if (text.length == 10) {
                    viewEditPhone.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.green))
                    imgTick.visibility = View.VISIBLE
                } else {
                    viewEditPhone.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.colorPrimary))
                    imgTick.visibility = View.GONE
                }
            }

        })


        relativeCreate.setOnClickListener {
            if (editMobileSignup.length() != 10) {
                editMobileSignup.error = getString(R.string.enter_valid_mobile)
            } else {
                var mobile = editMobileSignup.text.toString().trim()
                checkUserExist(mobile)
            }
        }

        relativeLogin.setOnClickListener {
            (activity as LoginActivity).push(LoginFragment())
        }

        signUpParent.setOnTouchListener { _, _ ->
            UtilsDefault.hideKeyboardForFocusedView(activity!!)
            false
        }


    }


    private fun checkUserExist(mobile: String) {
        if (UtilsDefault.isOnline()) {
            showProgress()
            val checkuserparams = InputParams()
            checkuserparams.mobile = mobile
            rewardAPI.checkuser(checkuserparams).enqueue(object : Callback<CheckUserResponseModel> {

                override fun onResponse(call: Call<CheckUserResponseModel>, response: Response<CheckUserResponseModel>) {
                    hideProgress()
                    checkUserResponseModel = response.body()
                    Log.i(TAG, response.body().toString())
                    if (response.body() != null && response.isSuccessful && response.code() == 200) {

                        if (checkUserResponseModel?.status == UtilsDefault.STATUS_SUCCESS) {

                            val header = response.headers().get(getString(R.string.token))
                            if (header != null) {
                                UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, header)
                            } else {
                                UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN).toString())
                            }

                            if (checkUserResponseModel?.data == 1) {

                                Toast.makeText(activity, checkUserResponseModel!!.message.toString(), Toast.LENGTH_SHORT).show()

                            } else {
                                val android_id = Settings.Secure.getString(activity!!.contentResolver, Settings.Secure.ANDROID_ID)

                                try {
                                    val otpFragment = OtpFragment()
                                    val args = Bundle()
                                    args.putString("mobile",mobile)
                                    args.putBoolean("isFromLogin",false)
                                    args.putString("referral",referralcode)
                                    args.putString("deviceID",android_id)
                                    otpFragment.arguments = args
                                    (activity as LoginActivity).push(otpFragment)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }

                        } else {
                            Toast.makeText(activity, checkUserResponseModel!!.message.toString(), Toast.LENGTH_SHORT).show()

                        }
                    } else {
                        Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onFailure(call: Call<CheckUserResponseModel>, t: Throwable) {
                    hideProgress()
                    Toast.makeText(activity, getString(R.string.server_error), Toast.LENGTH_SHORT).show()

                }

            })

        } else {
            Toast.makeText(activity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()

        }

    }

}
