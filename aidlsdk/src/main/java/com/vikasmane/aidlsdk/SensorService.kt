package com.vikasmane.aidlsdk

import RotationalDataInterface
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData

class SensorService : LifecycleService(), SensorEventListener {

    companion object {
        //Live data that exposes
        val sensorData = MutableLiveData<FloatArray>()
        private const val SENSOR_DELAY = 8 * 1000 // 8ms
    }

    private var sensorManager: SensorManager? = null
    private var rotationSensor: Sensor? = null

    /**
     * OrientationInterface has an inner abstract class named Stub that extends Binder
     * and implements methods from the AIDL interface.
     * This extends the Stub class and implements the methods.
     */
    private val myBinder: RotationalDataInterface.Stub = object : RotationalDataInterface.Stub() {
        override fun getRotationalData(): String {
            createSensorManager()
            return "Fetching data from sensors"
        }
    }

    /**
     * Returns binder object
     */
    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return myBinder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        sensorManager?.unregisterListener(this)
        return super.onUnbind(intent)
    }

    /**
     * Initializes SensorManager and rotationSensor objects
     */
    private fun createSensorManager() {
        if (sensorManager == null) {
            sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
            rotationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            //If rotation sensor is not null, registers the sensor listener
            rotationSensor?.let {
                addRotationSensorListener()
            }
        }
    }

    /**
     * Register sensor listener
     */
    private fun addRotationSensorListener() {
        sensorManager?.registerListener(
            this,
            rotationSensor,
            SENSOR_DELAY
        )
    }

    /**
     * Updates the live data with sensor data for Sensor.TYPE_ROTATION_VECTOR
     */
    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        sensorEvent?.let {
            if (it.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                sensorData.value = it.values
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }
}
