package com.reward.dne.fragment


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast

import com.reward.dne.R
import com.reward.dne.activity.LoginActivity
import com.reward.dne.activity.MainActivity
import com.reward.dne.activity.MainActivity.Companion.mainBack
import com.reward.dne.application.RewardApplication
import com.reward.dne.model.EarningResponse
import com.reward.dne.model.GetUserDataResponse
import com.reward.dne.model.InputParams
import com.reward.dne.model.UpdateProfileResponse
import com.reward.dne.retrofit.RewardAPI
import com.reward.dne.utils.Constants
import com.reward.dne.utils.UtilsDefault
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_earnings.*
import kotlinx.android.synthetic.main.fragment_help.*
import kotlinx.android.synthetic.main.fragment_my_profile.*
import kotlinx.android.synthetic.main.fragment_redeem_now.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import android.view.inputmethod.InputMethodManager.HIDE_IMPLICIT_ONLY
import android.content.Context.INPUT_METHOD_SERVICE
import android.support.v4.content.ContextCompat.getSystemService
import android.view.inputmethod.InputMethodManager


class MyProfileFragment : BaseFragment() {

    @Inject
    lateinit var rewardAPI: RewardAPI
    var TAG: String = "MyProfileFragment"
    var updateProfileResponse: UpdateProfileResponse? = null
    var getUserDataResponse:GetUserDataResponse? = null
    private var isEdit = false
    private var adapterGender: ArrayAdapter<String>? = null
    var gender = 0
    var invite = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_profile, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RewardApplication.instance.getComponent().inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainBack = 1
        (activity as MainActivity).textEditProfile.visibility = View.VISIBLE
        (activity as MainActivity).relativeWalletAmount.visibility = View.GONE
        (activity as MainActivity).headerName.text = getString(R.string.myprofile)
        (activity as MainActivity).textEditProfile.text = getString(R.string.edit)

        makeNonEdit(false)
        getuserData()



        (activity as MainActivity).textEditProfile.setOnClickListener {
            if (isEdit) {
                apiProfile()
            } else {
                isEdit = true
                Toast.makeText(activity, "editable", Toast.LENGTH_SHORT).show()
                (activity as MainActivity).textEditProfile.text = getString(R.string.update)
                makeNonEdit(true)
            }

        }

        relName.setOnClickListener {
            if (editProfileName.isClickable){
                editProfileName.requestFocus()
                val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
            }
        }

        adapterGender = ArrayAdapter(activity!!, R.layout.custom_spinner_item, context!!.resources.getStringArray(R.array.gender))
        spinGender.adapter = adapterGender!!

        spinGender.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> gender = 1
                    1 -> gender = 2
                }
            }

        }


    }

    private fun getuserData() {

        if (UtilsDefault.isOnline()) {
            showProgress()
            val checkuserparams = InputParams()
            checkuserparams.user_id = UtilsDefault.getSharedPreferenceInt(Constants.USERID)
            rewardAPI.getuserdata(checkuserparams).enqueue(object : Callback<GetUserDataResponse> {

                override fun onResponse(call: Call<GetUserDataResponse>, response: Response<GetUserDataResponse>) {
                    hideProgress()
                    getUserDataResponse = response.body()
                    Log.i(TAG, response.body().toString())
                    if (response.body() != null && response.isSuccessful && response.code() == 200) {

                        val header = response.headers().get(getString(R.string.token))
                        if (header != null) {
                            UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, header)
                        } else {
                            UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN).toString())
                        }

                        if (getUserDataResponse?.status == UtilsDefault.STATUS_SUCCESS) {

                            if (getUserDataResponse?.data!=null){

                                editProfileName.setText(getUserDataResponse?.data?.firstname)
                                editEmail.setText(getUserDataResponse?.data?.email)
                                editMobile.setText(getUserDataResponse?.data?.mobile)

                                editFriend.setText("${getUserDataResponse?.data?.invite_code}")

                                if (getUserDataResponse?.data?.invite_code.equals("")){
                                    relInvite.visibility = View.VISIBLE
                                }
                                else {
                                    invite = ""
                                    editFriend.setText("")
                                    relInvite.visibility = View.GONE
                                }



                                if (getUserDataResponse?.data?.gender == 1){
                                    spinGender.setSelection(0)
                                }
                                else if (getUserDataResponse?.data?.gender ==2) {
                                    spinGender.setSelection(1)
                                }

                                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                val date = dateFormat.parse(getUserDataResponse?.data?.created_at)
                                val dateFormat1 = SimpleDateFormat("dd/MM/yyyy")
                                try {
                                    val dateTime = dateFormat1.format(date)
                                    println("Current Date Time : $dateTime")
                                    textMembersince.text = "Member since $dateTime"
                                } catch (e: ParseException) {
                                    e.printStackTrace()
                                }
                            }

                        } else {

                            Toast.makeText(activity, getUserDataResponse!!.message.toString(), Toast.LENGTH_SHORT).show()

                        }
                    } else {

                        Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onFailure(call: Call<GetUserDataResponse>, t: Throwable) {
                    hideProgress()
                    Toast.makeText(activity, getString(R.string.server_error), Toast.LENGTH_SHORT).show()

                }


            })

        } else {
            Toast.makeText(activity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()

        }
    }

    private fun apiProfile() {
        val email = editEmail.text.toString().trim()
        val name = editProfileName.text.toString().trim()
        val invitecode = editFriend.text.toString().trim()

        if (name.equals("")) {
            editProfileName.error = "Enter valid name"
        }
        else if (!UtilsDefault.isEmailValid(email)) {
            editEmail.error = "Enter valid email"
        }
        else {
            invite = invitecode
            updateAPi(name,email,gender)
        }


    }

    private fun updateAPi(name:String,email:String,gender:Int) {
        if (UtilsDefault.isOnline()) {
            showProgress()
            val checkuserparams = InputParams()
            checkuserparams.email = email
            checkuserparams.firstname = name
            checkuserparams.gender = gender
            checkuserparams.invite_code = invite
            checkuserparams.user_id = UtilsDefault.getSharedPreferenceInt(Constants.USERID)
            rewardAPI.updateprofile(checkuserparams).enqueue(object : Callback<UpdateProfileResponse> {

                override fun onResponse(call: Call<UpdateProfileResponse>, response: Response<UpdateProfileResponse>) {
                    hideProgress()
                    updateProfileResponse = response.body()
                    Log.i(TAG, response.body().toString())
                    if (response.body() != null && response.isSuccessful && response.code() == 200) {

                        val header = response.headers().get(activity!!.getString(R.string.token))
                        if (header != null) {
                            UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, header)
                        } else {
                            UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN).toString())
                        }

                        if (updateProfileResponse?.status == UtilsDefault.STATUS_SUCCESS) {
                            isEdit = false

                            UtilsDefault.updateSharedPreferenceString(Constants.FIRST_NAME,updateProfileResponse?.data?.firstname!!)
                            UtilsDefault.updateSharedPreferenceString(Constants.EMAIL_ID,updateProfileResponse?.data?.email!!)
                            UtilsDefault.updateSharedPreferenceInt(Constants.GENDER,updateProfileResponse?.data?.gender!!)
                            UtilsDefault.updateSharedPreferenceString(Constants.INVITE_CODE,updateProfileResponse?.data?.invite_code!!)

                           // getuserData()

                            if (updateProfileResponse?.data?.invite_code.equals("")){
                                relInvite.visibility = View.VISIBLE
                            }
                            else {
                                invite = ""
                                editFriend.setText("")
                                relInvite.visibility = View.GONE
                            }

                            Toast.makeText(activity, "updated", Toast.LENGTH_SHORT).show()
                            (activity as MainActivity).textEditProfile.text = getString(R.string.edit)
                            makeNonEdit(false)

                        } else {
                            isEdit = true
                            Toast.makeText(activity, updateProfileResponse!!.message.toString(), Toast.LENGTH_SHORT).show()

                        }
                    } else {
                        isEdit = true
                        Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show()

                    }
                }

                override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                    hideProgress()
                    isEdit = true
                    Log.d("Failure",t.message)
                    Toast.makeText(activity, getString(R.string.server_error), Toast.LENGTH_SHORT).show()

                }


            })

        } else {
            Toast.makeText(activity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()

        }
    }


    private fun makeNonEdit(value: Boolean) {
        editEmail.isClickable = value
        editEmail.isFocusable = value
        editEmail.isEnabled = value
        editEmail.isFocusableInTouchMode = value

        editProfileName.isClickable = value
        editProfileName.isFocusable = value
        editProfileName.isEnabled = value
        editProfileName.isFocusableInTouchMode = value

        editFriend.isClickable = value
        editFriend.isEnabled = value
        editFriend.isFocusable = value
        editFriend.isFocusableInTouchMode = value

       // spinGender.isClickable = value
        spinGender.isEnabled = value
        // spinGender.isFocusableInTouchMode = value
    }


}
