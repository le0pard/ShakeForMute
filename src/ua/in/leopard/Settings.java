package ua.in.leopard;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {
	private SharedPreferences prefs;
	private static final String OPT_ON_OFF = "on_off";
	private static final boolean OPT_ON_OFF_DEF = false;
	private static final String OPT_SHAKE_THRESHOLD = "shake_threshold";
	private static final int OPT_SHAKE_THRESHOLD_DEF = 800;
	private static final String OPT_VIBRATION = "vibration";
	private static final boolean OPT_VIBRATION_DEF = true;
	
	private static final String OPT_SHOW_CALIBRATE_HELP = "is_show_calibrate_help";
	private static final Boolean OPT_SHOW_CALIBRATE_HELP_DEF = false;
	
	public Settings(Context context){
		prefs = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public void setOnOffStatus(Boolean status){
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(OPT_ON_OFF, status);
		editor.commit();
	}
	
	public Boolean getOnOffStatus() {
	   return prefs.getBoolean(OPT_ON_OFF, OPT_ON_OFF_DEF);
    }
	
	public static Boolean getOnOffStatus(Context context) {
	   return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_ON_OFF, OPT_ON_OFF_DEF);
    }
	
	public void changeOnOffStatus(){
		if (this.getOnOffStatus()){
			this.setOnOffStatus(false);
		} else {
			this.setOnOffStatus(true);
		}
	}
	
	
	public void setShakeThreshold(int shake_threshold){
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(OPT_SHAKE_THRESHOLD, shake_threshold);
		editor.commit();
	}
	
	public int getShakeThreshold() {
	   return prefs.getInt(OPT_SHAKE_THRESHOLD, OPT_SHAKE_THRESHOLD_DEF);
    }
	
	public static int getShakeThreshold(Context context) {
	   return PreferenceManager.getDefaultSharedPreferences(context).getInt(OPT_SHAKE_THRESHOLD, OPT_SHAKE_THRESHOLD_DEF);
    }
	
	public int getShakeThresholdForSeekBar() {
	   int ret_val = this.getShakeThreshold() - CalibrateTask.MIN_CALIBRATE_VALUE;
	   if (ret_val < 0){
		   ret_val = 0;
	   }
	   return ret_val;
    }
	
	public void setShakeThresholdFromSeekBar(int progress){
		this.setShakeThreshold(progress + CalibrateTask.MIN_CALIBRATE_VALUE);
	}
	
	public void setVirbation(Boolean status){
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(OPT_VIBRATION, status);
		editor.commit();
	}
	
	public Boolean getVirbation() {
	   return prefs.getBoolean(OPT_VIBRATION, OPT_VIBRATION_DEF);
    }
	
	public static Boolean getVirbation(Context context) {
	   return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_VIBRATION, OPT_VIBRATION_DEF);
    }
	
	
	
	
	public void setShowCalibrateHelp(Boolean status){
		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean(OPT_SHOW_CALIBRATE_HELP, status);
		editor.commit();
	}
	
	public Boolean getShowCalibrateHelp() {
	   return prefs.getBoolean(OPT_SHOW_CALIBRATE_HELP, OPT_SHOW_CALIBRATE_HELP_DEF);
    }
	
	public static Boolean getShowCalibrateHelp(Context context) {
	   return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_SHOW_CALIBRATE_HELP, OPT_SHOW_CALIBRATE_HELP_DEF);
    }
	
}
