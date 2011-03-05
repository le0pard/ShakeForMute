package ua.in.leopard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;

public class ShakeForMute extends Activity implements OnClickListener {
	private Settings mySettings;
	private Button onOffButton, calibrateButton;
	private CalibrateTask calibrateTask;
	private CheckBox vibrateCheckbox;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mySettings = new Settings(this);
        
        restoreBackgroudCalibrate();
        
        onOffButton = (Button) findViewById(R.id.on_off_button);
        onOffButton.setOnClickListener(this);
        calibrateButton = (Button) findViewById(R.id.calibrate_button);
        calibrateButton.setOnClickListener(this);
        vibrateCheckbox = (CheckBox) findViewById(R.id.vibrate_checkbox);
        vibrateCheckbox.setOnClickListener(this);
        
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }
    
    private void restoreBackgroudCalibrate(){
    	if (getLastNonConfigurationInstance()!=null) {
    		calibrateTask = (CalibrateTask)getLastNonConfigurationInstance();
    		if(calibrateTask.getStatus() == AsyncTask.Status.RUNNING) {
    			calibrateTask.newView(this);
    		}
    	}
    }
    
    @Override
    public Object onRetainNonConfigurationInstance() {
    	if(calibrateTask != null && calibrateTask.getStatus() == AsyncTask.Status.RUNNING) {
    		calibrateTask.closeView();
    	}
    	return(calibrateTask);
    }
    
    
    @Override
    protected void onResume() {
       super.onResume();
       updateView();
    }

    @Override
    protected void onPause() {
       super.onPause();
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		  case R.id.on_off_button:
			 mySettings.changeOnOffStatus();
			 updateView();
	         break;
		  case R.id.calibrate_button:
			 startCalibration();
	         break;
		  case R.id.vibrate_checkbox:
			 mySettings.setVirbation(((CheckBox) v).isChecked());
	         break;
		}
	}
	
	private void startCalibration(){
		if (!mySettings.getShowCalibrateHelp()){
			new AlertDialog.Builder(this)
			.setTitle(getString(R.string.calibrate_help_title))
			.setMessage(getString(R.string.calibrate_help_msg))
			.setNeutralButton(getString(R.string.ok_button), new DialogInterface.OnClickListener() {
	
				@Override
				public void onClick(DialogInterface dialog, int which) {
					taskCalibration();
				}
	
			})
			.setOnCancelListener(new DialogInterface.OnCancelListener() {
	
				@Override
				public void onCancel(DialogInterface dialog) {
					// do nothing – it will close on its own
				}
				
			})
			.show();
			mySettings.setShowCalibrateHelp(true);
		} else {
			taskCalibration();
		}
	}
	
	private void taskCalibration(){
		if(calibrateTask == null || 
				(calibrateTask != null && calibrateTask.getStatus() == AsyncTask.Status.FINISHED)) {
			calibrateTask = new CalibrateTask(this);
			calibrateTask.execute();
		}
	}
	
	private void updateView(){
		if (mySettings.getOnOffStatus()){
        	onOffButton.setText("on");
        } else {
        	onOffButton.setText("off");
        }
		calibrateButton.setText(Integer.toString(mySettings.getShakeThreshold()));
		vibrateCheckbox.setChecked(mySettings.getVirbation());
	}
}