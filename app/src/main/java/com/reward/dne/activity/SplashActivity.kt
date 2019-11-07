package com.reward.dne.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.reward.dne.R
import com.reward.dne.application.RewardApplication
import com.reward.dne.model.DeviceResponse
import com.reward.dne.model.InputParams
import com.reward.dne.model.PostBackResponse
import com.reward.dne.retrofit.RewardAPI
import com.reward.dne.utils.Constants
import com.reward.dne.utils.UtilsDefault
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

class SplashActivity : BaseActivity() {

    private val SPLASH_DELAY: Long = 2000 //2 seconds
    @Inject
    lateinit var rewardAPI: RewardAPI
    var deviceResponse:DeviceResponse? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        RewardApplication.instance.getComponent().inject(this)

        val w = window
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        Handler().postDelayed({

            if (UtilsDefault.getSharedPreferenceString(Constants.SESSION_STATUS)!=null &&
                    !UtilsDefault.getSharedPreferenceString(Constants.SESSION_STATUS).equals("")){

                val intent = Intent(this@SplashActivity,MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else{
                val intent = Intent(this@SplashActivity,LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

        },SPLASH_DELAY)

       /* if (UtilsDefault.getSharedPreferenceString(Constants.SESSION_STATUS)!=null &&
                !UtilsDefault.getSharedPreferenceString(Constants.SESSION_STATUS).equals("")){
            Handler().postDelayed({
                val intent = Intent(this@SplashActivity,MainActivity::class.java)
                startActivity(intent)
                finish()

            },SPLASH_DELAY)
        }
        else{
            checkDeviceApi()
        }*/


    }

   /* private fun checkDeviceApi() {
        if (UtilsDefault.isOnline()) {
            val offerParams = InputParams()
            val android_id = Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
            offerParams.device_id = android_id

            rewardAPI.checkdevice(offerParams).enqueue(object : Callback<DeviceResponse> {
                override fun onResponse(call: Call<DeviceResponse>, response: Response<DeviceResponse>) {
                    if (response.body() != null && response.isSuccessful && response.code() == 200) {
                        deviceResponse = response.body()
                        if (deviceResponse!!.status == UtilsDefault.STATUS_SUCCESS) {

                            if (deviceResponse!!.data == 1){
                                val intent = Intent(this@SplashActivity,LoginActivity::class.java)
                                intent.putExtra("isDeviceExist",1)
                                startActivity(intent)
                                finish()
                            }
                            else {
                                val intent = Intent(this@SplashActivity,LoginActivity::class.java)
                                intent.putExtra("isDeviceExist",0)
                                startActivity(intent)
                                finish()
                            }


                        } else {
                            Toast.makeText(this@SplashActivity, deviceResponse!!.message, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@SplashActivity, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<DeviceResponse>, t: Throwable) {
                    Toast.makeText(this@SplashActivity, getString(R.string.server_error), Toast.LENGTH_SHORT).show()

                }
            })
        } else {
            Toast.makeText(this@SplashActivity, getString(R.string.no_internet), Toast.LENGTH_SHORT).show()
        }
    }*/
}
