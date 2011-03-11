package ua.in.leopard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageButton;

public class ShakeForMute extends Activity implements OnClickListener {
	private Settings mySettings;
	private ImageButton onOffButton, calibrateButton;
	private CalibrateTask calibrateTask;
	private CheckBox vibrateCheckbox;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mySettings = new Settings(this);
        
        restoreBackgroudCalibrate();
        
        onOffButton = (ImageButton) findViewById(R.id.on_off_button);
        onOffButton.setOnClickListener(this);
        calibrateButton = (ImageButton) findViewById(R.id.calibrate_button);
        calibrateButton.setOnClickListener(this);
        vibrateCheckbox = (CheckBox) findViewById(R.id.vibrate_checkbox);
        vibrateCheckbox.setOnClickListener(this);
        
        this.setVolumeControlStream(AudioManager.STREAM_RING);
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
			 if (mySettings.getOnOffStatus()){
				 startCalibration();
			 } else {
				 new AlertDialog.Builder(this)
					.setTitle(getString(R.string.calibrate_off_title))
					.setMessage(getString(R.string.calibrate_off_message))
					.setNeutralButton(getString(R.string.ok_button), new DialogInterface.OnClickListener() {
			
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// do nothing – it will close on its own
						}
			
					})
					.setOnCancelListener(new DialogInterface.OnCancelListener() {
			
						@Override
						public void onCancel(DialogInterface dialog) {
							// do nothing – it will close on its own
						}
						
					})
					.show();
			 }
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
					mySettings.setShowCalibrateHelp(true);
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
			onOffButton.setImageResource(R.drawable.dashboard_button_on_off);
			calibrateButton.setImageResource(R.drawable.dashboard_button_calibrate_on);
		} else {
			onOffButton.setImageResource(R.drawable.dashboard_button_off_on);
			calibrateButton.setImageResource(R.drawable.dashboard_button_calibrate_off);
		}
		
		vibrateCheckbox.setChecked(mySettings.getVirbation());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
      super.onCreateOptionsMenu(menu);
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.menu, menu);
      return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
      	case R.id.menu_howto_button:
         startActivity(new Intent(this, HowTo.class));
         return true;
      	case R.id.menu_about_button:
      	 startActivity(new Intent(this, About.class));
      	 return true;
      	default:
	     return super.onOptionsItemSelected(item);
      }
	}
	
}