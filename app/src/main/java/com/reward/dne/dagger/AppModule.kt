package com.reward.dne.dagger

import android.content.Context
import com.reward.dne.application.RewardApplication
import com.reward.dne.retrofit.RewardAPI
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
class AppModule(private val app:RewardApplication) {

    @Provides
    @Singleton
    fun provideContext(): Context = app

    @Provides
    fun provideRWRDApiInterface(retrofit: Retrofit): RewardAPI {
        return retrofit.create<RewardAPI>(RewardAPI::class.java)
    }
}