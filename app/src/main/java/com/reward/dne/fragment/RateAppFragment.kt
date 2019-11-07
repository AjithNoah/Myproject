package com.reward.dne.fragment


import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PatternMatcher
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.reward.dne.R
import com.reward.dne.activity.MainActivity
import com.reward.dne.activity.MainActivity.Companion.mainBack
import kotlinx.android.synthetic.main.app_bar_main.*
import java.util.regex.Pattern


class RateAppFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_rate_app, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       mainBack = 1
        (activity as MainActivity).headerName.text = getString(R.string.rate_my_app)


    }



}
