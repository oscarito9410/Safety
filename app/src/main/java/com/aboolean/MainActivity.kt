package com.aboolean

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aboolean.safety.R
import com.aboolean.usertype.RemoteSafetySdk
import com.aboolean.usertype.SafetySdk

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}