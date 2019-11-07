package com.reward.dne.dagger

import com.reward.dne.activity.AppDetailActivity
import com.reward.dne.activity.RedeemActivity
import com.reward.dne.activity.SplashActivity
import com.reward.dne.application.RewardApplication
import com.reward.dne.fragment.*
import com.reward.dne.retrofit.RetrofitModule
import com.reward.dne.fragment.SignUpFragment
import dagger.Component
import retrofit2.Retrofit
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(AppModule::class, RetrofitModule::class))
interface ApplicationComponent {

    fun inject(application: RewardApplication)
    fun retrofit(): Retrofit
    fun inject(application: SignUpFragment) {

    }
    fun inject(loginFragment: LoginFragment)

    fun inject(helpFragment: HelpFragment)

    fun inject(faqFragment: FAQFragment)

    fun inject(earningsFragment: EarningsFragment) {

    }

    fun inject(myProfileFragment: MyProfileFragment) {

    }
    fun inject(offerFragment: OfferFragment){

    }
    fun inject(earningHistoryFragment: EarningHistoryFragment){

    }

    fun inject(appDetailActivity: AppDetailActivity){

    }

    fun inject(pendingFragment: PendingFragment){

    }

    fun inject(completedFragment: CompletedFragment){

    }

    fun inject(hotAppsFragment: HotAppsFragment) {

    }

    fun inject(otpFragment: OtpFragment) {

    }

    fun inject(installAppFragment: InstallAppFragment) {

    }

    fun inject(formFragment: FormFragment) {

    }

    fun inject(storiesFragment: StoriesFragment) {

    }

    fun inject(redeemHistoryFragment: RedeemHistoryFragment) {

    }

    fun inject(redeemNowFragment: RedeemNowFragment) {

    }

    fun inject(redeemActivity: RedeemActivity) {

    }

    fun inject(splashActivity: SplashActivity) {

    }

}



