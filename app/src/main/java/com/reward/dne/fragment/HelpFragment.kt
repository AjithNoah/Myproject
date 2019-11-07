package com.reward.dne.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.reward.dne.R
import com.reward.dne.activity.MainActivity
import com.reward.dne.activity.MainActivity.Companion.mainBack
import com.reward.dne.application.RewardApplication
import com.reward.dne.model.InputParams
import com.reward.dne.model.OutputResponse
import com.reward.dne.retrofit.RewardAPI
import com.reward.dne.utils.Constants
import com.reward.dne.utils.UtilsDefault
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_help.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class HelpFragment : BaseFragment() {

    @Inject
    lateinit var rewardAPI: RewardAPI
    var outputResponse:OutputResponse? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_help, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        RewardApplication.instance.getComponent().inject(this)
        mainBack = 1
        (activity as MainActivity).headerName.text = getString(R.string.help)

        relativeSendNow.setOnClickListener {
            val name = edtName.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val sub = edtSubject.text.toString().trim()
            val description = edtDescription.text.toString().trim()

            when {
                name == "" -> edtName.error = "Enter name"
                !UtilsDefault.isEmailValid(email) -> edtEmail.error = "Enter valid email"
                sub == "" -> edtSubject.error = "Enter subject"
                description == "" -> edtDescription.error = "Enter description"
                else -> helpSubmit()
            }
        }

    }

    fun helpSubmit() {
        if (UtilsDefault.isOnline()) {
            showProgress()
            val helpParams = InputParams()
            helpParams.user_id = UtilsDefault.getSharedPreferenceInt(Constants.USERID)
            helpParams.name = edtName.text.toString().trim()
            helpParams.subject = edtSubject.text.toString().trim()
            helpParams.message = edtDescription.text.toString().trim()
            helpParams.email = edtEmail.text.toString().trim()
            rewardAPI.helpSubmit(helpParams).enqueue(object : Callback<OutputResponse> {
                override fun onResponse(call: Call<OutputResponse>, response: Response<OutputResponse>) {
                    hideProgress()
                    if (response.body() != null && response.isSuccessful && response.code() == 200) {
                        outputResponse = response.body()
                        if (outputResponse!!.status == UtilsDefault.STATUS_SUCCESS) {

                            val header = response.headers().get(getString(R.string.token))
                            if (header != null) {
                                UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, header)
                            } else {
                                UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN).toString())
                            }

                            edtName.setText("")
                            edtEmail.setText("")
                            edtSubject.setText("")
                            edtDescription.setText("")
                            Toast.makeText(activity, outputResponse!!.message, Toast.LENGTH_SHORT).show()
                        } else {
                            hideProgress()
                            Toast.makeText(context, outputResponse!!.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    else {
                        Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<OutputResponse>, t: Throwable) {
                    hideProgress()
                    Toast.makeText(context, getString(R.string.server_error), Toast.LENGTH_SHORT).show()

                }
            })
        } else {
            Toast.makeText(context, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }

}
