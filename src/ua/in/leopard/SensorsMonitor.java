package ua.in.leopard;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.util.Log;

public class SensorsMonitor implements SensorEventListener {
	
	private static final String TAG = SensorsMonitor.class.getSimpleName();
	
	private SensorManager mSensorManager;
	private final Sensor mAccelerometer;
	private Context myContext;
	private AudioManager audioMan;
	private Boolean isMutted = false;
	
	private long lastUpdate = -1;
	private float x, y, z;
	private float last_x, last_y, last_z;
	
	public SensorsMonitor(Context context){
		this.myContext = context;
		mSensorManager = (SensorManager) this.myContext.getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		audioMan = (AudioManager) this.myContext.getSystemService(Context.AUDIO_SERVICE);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		//nothing
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (Settings.getOnOffStatus(this.myContext)){
			synchronized (this) {
		        switch (event.sensor.getType()){
		            case Sensor.TYPE_ACCELEROMETER:
		            	long curTime = System.currentTimeMillis();
		            	if ((curTime - lastUpdate) > 100) {
		            		long diffTime = (curTime - lastUpdate);
		            		lastUpdate = curTime;
		            		x = event.values[SensorManager.DATA_X];
							y = event.values[SensorManager.DATA_Y];
							z = event.values[SensorManager.DATA_Z];
							float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;
							if (speed > Settings.getShakeThreshold(this.myContext)) {
					    		this.muteVolume();
							}
							last_x = x;
							last_y = y;
							last_z = z;
						}
		            	break;
		        }
			}
		}
	}
	
	private void muteVolume(){
		if (!isMutted && audioMan.getRingerMode() == AudioManager.RINGER_MODE_NORMAL){
			isMutted = true;
			if (Settings.getVirbation(this.myContext)){
				audioMan.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
			} else {
				audioMan.setRingerMode(AudioManager.RINGER_MODE_SILENT);
			}
		}
	}
	
	private void unmuteVolume(){
		if (isMutted){
			isMutted = false;
			audioMan.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
		}
	}
	
	public void resumeSensors(){
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	public void pauseSensors(){
		mSensorManager.unregisterListener(this);
		this.unmuteVolume();
	}

}
