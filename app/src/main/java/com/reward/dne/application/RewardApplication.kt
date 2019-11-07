package com.reward.dne.application

import android.content.Context
import android.content.res.Resources
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import com.reward.dne.BuildConfig
import com.reward.dne.dagger.AppModule
import com.reward.dne.dagger.DaggerApplicationComponent
import com.reward.dne.retrofit.RetrofitModule
import com.google.firebase.analytics.FirebaseAnalytics
import com.reward.dne.dagger.ApplicationComponent
import com.reward.dne.smsretriever.AppSignatureHelper


class RewardApplication : MultiDexApplication(){

    init {
        instance = this
    }

    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    val TAG = RewardApplication::class.java.simpleName
    lateinit var mComponent: ApplicationComponent

    companion object {
        lateinit var instance: RewardApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        mComponent = DaggerApplicationComponent.builder()
                .appModule(AppModule(this))
                .retrofitModule(RetrofitModule(instance, BuildConfig.REWARD_BASE_URL))
                .build()
        mComponent.inject(this)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        MultiDex.install(this)
        var appSignature = AppSignatureHelper(this)
        appSignature.appSignatures
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)

    }

    fun getComponent(): ApplicationComponent {
        return mComponent
    }

    fun getAppResources(): Resources {
        return instance.resources
    }

    fun getAppString(resourceId: Int): String {
        return getAppResources().getString(resourceId)
    }

    override fun onTerminate() {
        super.onTerminate()
    }

    override fun onLowMemory() {
        super.onLowMemory()
    }

    fun getGlobalContext(): Context? {
        // TODO Auto-generated method stub
        return instance
    }

   /* class AppController : Application() {

        init {
            instance = this
        }

        companion object {
            private var instance: AppController? = null

            fun applicationContext() : AppController {
                return instance as AppController
            }
        }

        override fun onCreate() {
            super.onCreate()
        }
    }*/


}