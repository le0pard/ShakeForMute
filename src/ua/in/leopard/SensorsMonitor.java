package ua.in.leopard;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;

public class SensorsMonitor implements SensorEventListener {
	
	private static final String TAG = SensorsMonitor.class.getSimpleName();
	
	private SensorManager mSensorManager;
	private final Sensor mAccelerometer;
	private Context myContext;
	private Boolean isWorking = false;
	private AudioManager audioMan;
	private Boolean isMutted = false;
	
	private long lastUpdate = -1;
	private float x, y, z;
	private float last_x, last_y, last_z;
	private static final int SHAKE_THRESHOLD = 800;
	
	public SensorsMonitor(Context context){
		this.myContext = context;
		mSensorManager = (SensorManager) this.myContext.getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		audioMan = (AudioManager) this.myContext.getSystemService(Context.AUDIO_SERVICE);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		//nothing
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (isWorking){
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
							if (speed > SHAKE_THRESHOLD) {
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
		if (!isMutted){
			//audioMan.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_LOWER, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
			audioMan.setStreamMute(AudioManager.STREAM_RING, true);
			isMutted = true;
		}
	}
	
	private void unmuteVolume(){
		if (isMutted){
			audioMan.setStreamMute(AudioManager.STREAM_RING, false);
			isMutted = false;
		}
	}
	
	public void resumeSensors(){
		isWorking = true;
	}
	
	public void pauseSensors(){
		isWorking = false;
		this.unmuteVolume();
	}

}
