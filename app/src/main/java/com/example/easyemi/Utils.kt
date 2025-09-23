package com.example.easyemi

import android.widget.EditText

fun EditText.isEmpty(): Boolean{
    return if (this.text.toString().isEmpty()){
        this.error = "This field is required"
        true
    }else{
        false
    }
}