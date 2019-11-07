package com.reward.dne.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.reward.dne.R
import com.reward.dne.activity.BaseActivity
import com.reward.dne.activity.LoginActivity
import com.reward.dne.activity.MainActivity
import com.reward.dne.utils.UtilsDefault

abstract class BaseFragment : Fragment() {


    fun showProgress() {
        showProgress(R.string.Loading___)
    }

    fun showProgress(stringId: Int) {
        val activity = activity as BaseActivity?
        if (activity != null) {
            activity.showProgress(stringId)
        }
    }
    fun hideProgress() {
        val activity = activity as BaseActivity?
        activity?.hideProgress()
    }

    fun logout(context: Context,activity: MainActivity){
        UtilsDefault.clearSession()
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity.finishAffinity()
    }

}