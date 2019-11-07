package com.reward.dne.retrofit

import android.content.Context
import android.util.Log
import com.reward.dne.BuildConfig
import com.reward.dne.utils.Constants
import com.reward.dne.utils.UtilsDefault
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class RetrofitModule(val context : Context?, val  baseUrl:String) {


    @Provides
    @Singleton
    internal fun provideInterceptor(): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            var request: Request? = null
            try {

                if (UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN) != null) {
                    var token = ""
                    if (UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN)!!.contains("Bearer")) {
                        token = UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN)!!
                    } else {
                        token = "Bearer " + UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN)
                    }
                    request = chain.request().newBuilder()
                            .addHeader("Content-Type", "application/json")
                            .addHeader("Authorization", token)
                            .method(original.method(), original.body())
                            .build()
                    Log.d("Authorization", "intercept: " + "Bearer "
                            + UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN))
                } else {
                    request = chain.request().newBuilder()
                            .addHeader("Content-Type", "application/json")
                            .method(original.method(), original.body())
                            .build()
                    Log.d("Authorization", "intercept: " + "Bearer "
                            + UtilsDefault.getSharedPreferenceString(Constants.JWT_TOKEN))
                }


            } catch (authFailureError: Exception) {
                authFailureError.printStackTrace()
            }

            chain.proceed(request!!)
        }
    }

    @Provides
    @Singleton
    internal fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder()
        return gsonBuilder.create()
    }

    @Provides
    @Singleton
    internal fun provideOkHttpClient(interceptor: Interceptor): OkHttpClient {

        val okHttpBuilder = OkHttpClient.Builder()
        okHttpBuilder.interceptors().add(interceptor)
        /*if (context != null) {
            okHttpBuilder.interceptors().add(ChuckInterceptor(context))
        }*/
        okHttpBuilder.readTimeout(60, TimeUnit.SECONDS)
        okHttpBuilder.connectTimeout(60, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            okHttpBuilder.interceptors().add(logging)
        }

        return okHttpBuilder.build()
    }

    @Provides
    @Singleton
    internal fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(okHttpClient)
                .build()
    }
}