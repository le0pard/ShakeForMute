package ua.in.leopard;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MutePhoneStateListener extends PhoneStateListener {
	private Context myContext;
	private Intent myIntent;
	
	public MutePhoneStateListener(Context context, Intent intent){
		this.myContext = context;
		this.myIntent = myIntent;
	}
	
	public void onCallStateChanged(int state, String incomingNumber){
		switch(state){
	    	case TelephonyManager.CALL_STATE_RINGING:
	    		Log.d("DEBUG", "RINGING");
	    		AudioManager audioMan = (AudioManager) this.myContext.getSystemService(Context.AUDIO_SERVICE);
	    		audioMan.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_LOWER, AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
	    		break;
	    }
	}
	
}
