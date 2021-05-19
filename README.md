# AIDLServices
Displays data related to TYPE_ROTATION_VECTOR sensor using <a href="https://developer.android.com/guide/components/aidl#Create">AIDL</a><br>
- Consist of aidlsdk module that expose sensor data
- Sample app to show the sensor data

## Usage
### aidlsdk module
- Bind Service
```kotlin
class SensorService : LifecycleService() //Bound Sevice with exposes sensor data using liveData

 val sensorData = MutableLiveData<FloatArray>() //Exposes rotational vector sensor data to the observer.
```
### App usage
- Add <service> in your apps AndroidManifest.xml
```kotlin
<service
       android:name="com.vikasmane.aidlsdk.SensorService"
       android:enabled="true"
       android:exported="true" />
```
- To use the service in your app bind to the service 
```kotlin
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
```
- The actual sensor listener is attached on calling rotationalData of orientationInterface after service is connected
```kotlin
orientationInterface?.rotationalData //Starts the sensor listener
```
- This will start pushing the sensor data into the sensorData liveData
```kotlin
SensorService.sensorData.observe(this, {
            sensorDataText.text = it.contentToString()
        })
```

# App screenshot
<img src="https://user-images.githubusercontent.com/7870133/118788400-63eb0180-b8b1-11eb-9714-1ae3d921215c.png" width="400" height="790">
