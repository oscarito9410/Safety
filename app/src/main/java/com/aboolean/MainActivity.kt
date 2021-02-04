package com.aboolean

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aboolean.safety.R
import com.aboolean.usertype.LastKnowLocation
import com.aboolean.usertype.RemoteSafetySdk
import com.aboolean.usertype.SafetySdk
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GlobalScope.launch {
            RemoteSafetySdk.instance.init(this@MainActivity,
            lastKnowLocation = LastKnowLocation(20.0, 20.0))
        }
    }
}