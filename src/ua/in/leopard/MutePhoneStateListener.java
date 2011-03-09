package ua.in.leopard;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MutePhoneStateListener extends PhoneStateListener {
	private Context myContext;
	private SensorsMonitor mySensorsMonitor;
	
	public MutePhoneStateListener(Context context, SensorsMonitor sensorsMonitor){
		this.myContext = context;
		this.mySensorsMonitor = sensorsMonitor;
	}
	
	public void onCallStateChanged(int state, String incomingNumber){
		switch(state){
	    	case TelephonyManager.CALL_STATE_RINGING:
	    		this.mySensorsMonitor.resumeSensors();
	    		break;
	    	default:
	    		this.mySensorsMonitor.pauseSensors();
	    }
	}
	
}
