package com.vikasmane.aidldemo

import RotationalDataInterface
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.vikasmane.aidlsdk.SensorService

class MainActivity : AppCompatActivity() {
    private lateinit var sensorDataText: TextView
    private lateinit var rotationalServiceIntent: Intent
    private lateinit var rotationalServiceConnection: ServiceConnection
    private lateinit var connectionButton: Button
    private var isServiceConnected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sensorDataText = findViewById(R.id.sensorDataTextView)
        connectionButton = findViewById(R.id.serviceConnectButton)
        setObservers()
        connectionButton.setOnClickListener {
            if (isServiceConnected) {
                unBindService()
            } else {
                bindService()
            }
        }
    }

    private fun setObservers() {
        SensorService.sensorData.observe(this, {
            sensorDataText.text = it.contentToString()
        })
    }

    override fun onPause() {
        super.onPause()
        //Unbinding service on pause
        unBindService()
    }

    private fun bindService() {
        rotationalServiceIntent = Intent(this, SensorService::class.java)
        rotationalServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(componentName: ComponentName?, binder: IBinder?) {
                val orientationInterface = RotationalDataInterface.Stub.asInterface(binder)
                sensorDataText.text = orientationInterface?.rotationalData
                isServiceConnected = true
                updateButtonText()
            }

            override fun onServiceDisconnected(componentName: ComponentName?) {
                isServiceConnected = false
                updateButtonText()
            }
        }
        bindService(
            rotationalServiceIntent,
            rotationalServiceConnection,
            Context.BIND_AUTO_CREATE
        )
    }

    /**
     * Unbind the service and stop the service
     */
    private fun unBindService() {
        if (isServiceConnected) {
            unbindService(rotationalServiceConnection)
            stopService(rotationalServiceIntent)
            isServiceConnected = false
            updateButtonText()
        }
    }

    /**
     * Updates button text according to service connection state
     */
    private fun updateButtonText() {
        if (isServiceConnected)
            connectionButton.text = getString(R.string.disconnect_to_sensor_service)
        else
            connectionButton.text = getString(R.string.connect_to_sensor_service)
    }
}