package com.aboolean

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aboolean.safety.R
import com.aboolean.usertype.LastKnowLocation
import com.aboolean.usertype.ProxyConfiguration
import com.aboolean.usertype.RemoteSafetySdk
import com.aboolean.usertype.SafetySdk
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GlobalScope.launch {
            RemoteSafetySdk.instance.init(this@MainActivity,
                    lastKnowLocation = LastKnowLocation(20.0, 20.0),
                    proxyConfiguration = ProxyConfiguration("https://sosmex-tools.azurewebsites.net/Imeis/",
                            endPoint = "Create"))
        }
    }
}