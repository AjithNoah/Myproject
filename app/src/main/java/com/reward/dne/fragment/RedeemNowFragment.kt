package com.reward.dne.fragment


import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.reward.dne.R
import com.reward.dne.activity.MainActivity
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_redeem_now.*
import android.provider.ContactsContract
import android.content.Intent
import android.util.Log
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v4.app.ActivityCompat
import android.provider.Settings
import android.support.v7.widget.LinearLayoutManager
import android.widget.*
import com.reward.dne.adapter.AdapterRedeemHistory
import com.reward.dne.application.RewardApplication
import com.reward.dne.model.InputParams
import com.reward.dne.model.OperatorResponse
import com.reward.dne.model.OperatorSpinner
import com.reward.dne.model.RedeemHistoryResponse
import com.reward.dne.retrofit.RewardAPI
import com.reward.dne.utils.Constants
import com.reward.dne.utils.UtilsDefault
import kotlinx.android.synthetic.main.fragment_redeem_history.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


class RedeemNowFragment : BaseFragment() {

    @Inject
    lateinit var rewardAPI: RewardAPI
    private var adapterOperator: ArrayAdapter<OperatorSpinner>? = null
    var operatorResponse:OperatorResponse? = null
    private var REQUESTCODE = 1
    private var CONTACT_PERMISSION_REQUESTCODE = 123
    var operatorID = 0
    var operatorList = ArrayList<OperatorSpinner>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_redeem_now, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        RewardApplication.instance.getComponent().inject(this)
        (activity as MainActivity).headerName.text = getString(R.string.redeem)

        textavailableAmount.text = "₹" + UtilsDefault.getSharedPreferenceInt(Constants.MY_EARNED_AMOUNT)

        if (userVisibleHint) {
            getOperator("cellular")
        }

        rdgOperator.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rdPrepaid -> {
                    relDth.visibility = View.GONE
                    relMob.visibility = View.VISIBLE
                    getOperator("cellular")
                }
                R.id.rdPostpaid -> {
                    relDth.visibility = View.GONE
                    relMob.visibility = View.VISIBLE
                    getOperator("cellular")
                }
                R.id.rdDth -> {
                    relDth.visibility = View.VISIBLE
                    relMob.visibility = View.GONE
                    getOperator("dth")
                }
            }
        }


        imgContact.setOnClickListener {
            if (checkPermission()){
                val contactPickerIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                startActivityForResult(contactPickerIntent, REQUESTCODE)
            }
        }
        relativeProceed.setOnClickListener {
            Toast.makeText(activity, getString(R.string.under_developement), Toast.LENGTH_SHORT).show()
        }

        spinnerOperator.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(p0: AdapterView<*>?) {
                // onSelectedNothing
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val model:OperatorSpinner = parent!!.selectedItem as OperatorSpinner
                operatorID = model.id!!

            }
        }


    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && isResumed) {
            getOperator("cellular")
        }
    }

    private fun getOperator(type:String) {

        if (UtilsDefault.isOnline()) {
            showProgress()
            val checkuserparams = InputParams()
            checkuserparams.rechargetype = type
            checkuserparams.user_id = UtilsDefault.getSharedPreferenceInt(Constants.USERID)
            rewardAPI.rechargetype(checkuserparams).enqueue(object : Callback<OperatorResponse> {

                override fun onResponse(call: Call<OperatorResponse>, response: Response<OperatorResponse>) {
                    hideProgress()
                    operatorResponse = response.body()
                    Log.i(ContentValues.TAG, response.body().toString())
                    if (response.body() != null && response.isSuccessful && response.code() == 200) {

                        val header = response.headers().get(getString(R.string.token))
                        if (header != null) {
                            UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, header)
                        } else {
                            UtilsDefault.updateSharedPreferenceString(Constants.JWT_TOKEN, UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN).toString())
                        }
                        if (operatorResponse?.status == UtilsDefault.STATUS_SUCCESS) {
                            operatorList.clear()
                            if (operatorResponse?.data?.size!! > 0 && operatorResponse?.data!=null){

                                for (i in 0..operatorResponse?.data!!.size.minus(1)){
                                    operatorList.add(OperatorSpinner(operatorResponse?.data!![i].id,operatorResponse?.data!![i].name))
                                }

                                adapterOperator = ArrayAdapter(activity!!, R.layout.custom_spinner_item,operatorList)
                                spinnerOperator.adapter = adapterOperator

                            }

                        } else {
                            Toast.makeText(activity, operatorResponse?.message, Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        Toast.makeText(activity, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<OperatorResponse>, t: Throwable) {
                    hideProgress()
                    Toast.makeText(activity, getString(R.string.server_error), Toast.LENGTH_SHORT).show()

                }


            })

        } else {
            Toast.makeText(activity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()

        }
    }

    private fun checkPermission(): Boolean {

        if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), CONTACT_PERMISSION_REQUESTCODE)
            return false
        }

        return true
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            CONTACT_PERMISSION_REQUESTCODE ->
                if(ActivityCompat.shouldShowRequestPermissionRationale(activity!!, Manifest.permission.READ_CONTACTS)){
                    //denied
                    Log.e("denied", "denied");
                }else{
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                        //allowed
                        val contactPickerIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
                        startActivityForResult(contactPickerIntent, REQUESTCODE)
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(REQUESTCODE == requestCode) {
            if (resultCode == Activity.RESULT_OK){
                val uri = data?.data
                val contentResolver  = activity!!.contentResolver
                val contentCursor = contentResolver.query(uri, null, null, null, null)

                if (contentCursor!=null){
                    if (contentCursor.moveToFirst()){
                        val id = contentCursor.getString(contentCursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                        val hasPhone = contentCursor.getString(contentCursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))

                        if (hasPhone == "1") {
                            val phones = activity!!.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null)
                            phones.moveToFirst()
                            val contactNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            Log.i("phoneNUmber", "The phone number is $contactNumber")
                            var phone = contactNumber.toString()
                            if (phone.startsWith("+91")){
                                phone =  phone.replace("\\s+","").replaceFirst("+91","").trim()
                            }
                            phone = phone.replace("\\D+".toRegex(), "")
                            Log.i("phoneNUmber", phone)
                            edtMobile.setText(phone)
                        }

                    }
                }

            }
        }
    }


}
