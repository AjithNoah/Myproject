package com.reward.dne.utils

import android.text.Editable
import android.widget.EditText
import android.text.TextWatcher
// 4 3
class OtpTextWatcher(private val etNext: EditText, private val etPrev: EditText) : TextWatcher {

    override fun afterTextChanged(editable: Editable) {
    }
    override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}

    override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
        val text = arg0.toString()
        if (text.length == 1)
            etNext.requestFocus()
        else if (text.length == 0)
            etPrev.requestFocus()

    }
}