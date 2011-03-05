package ua.in.leopard;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class CalibrateSensors implements SensorEventListener {
	
	private static final String TAG = CalibrateSensors.class.getSimpleName();
	
	private SensorManager mSensorManager;
	private final Sensor mAccelerometer;
	private Context myContext;
	private float maxCalibration = -1;
	
	private long lastUpdate = -1;
	private float x, y, z;
	private float last_x, last_y, last_z;
	
	public CalibrateSensors(Context context){
		this.myContext = context;
		mSensorManager = (SensorManager) this.myContext.getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
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
							if (maxCalibration < speed){
								maxCalibration = speed;
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
	
	public void startCalibration(){
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	public void stopCalibration(){
		mSensorManager.unregisterListener(this);
	}
	
	public float getMaxShake(){
		return maxCalibration;
	}

}
