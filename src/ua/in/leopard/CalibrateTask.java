package ua.in.leopard;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class CalibrateTask extends AsyncTask<Void, String, Void> {
	
	private Context myContext;
	private ProgressDialog pd;
	public static int MIN_CALIBRATE_VALUE = 400;
	
	public CalibrateTask(Context myContext) {
		this.myContext = myContext;
	}
	
	public void newView(Context myContext){
		this.myContext = myContext;
		this.pd = ProgressDialog.show(this.myContext, this.myContext.getString(R.string.calibrate_dialog_title), this.myContext.getString(R.string.calibrate_dialog_message), true, false);
	}
	
	public void closeView(){
		this.pd.dismiss();
	}
	
	@Override
    protected void onPreExecute() {
		this.pd = ProgressDialog.show(this.myContext, this.myContext.getString(R.string.calibrate_dialog_title), this.myContext.getString(R.string.calibrate_dialog_message), true, false);
    }
	
	@Override
	protected Void doInBackground(Void... params) {
		CalibrateSensors calibrateSens = new CalibrateSensors(this.myContext);
		calibrateSens.startCalibration();
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		calibrateSens.stopCalibration();
		float newCalibrateValue = calibrateSens.getMaxShake();
		if (newCalibrateValue > MIN_CALIBRATE_VALUE){
			Settings mySettings = new Settings(this.myContext);
			int newValue = 0;
			if (newCalibrateValue > 1000){
				int resVal = (int)(newCalibrateValue / 1000);
				newValue = (int)(newCalibrateValue - resVal * 100);
			} else {
				newValue = (int)(newCalibrateValue - 100);
			}
			mySettings.setShakeThreshold(newValue);
		}
		
		return null;
	}
	
	@Override
	protected void onProgressUpdate(String... message) {
		if (pd != null){
			try {
				pd.setMessage(message[0]);
			} catch(Exception e){
				pd = null;
				if (!isCancelled()){
					cancel(true);
				}
			}
		}
	}
	
	@Override
	protected void onPostExecute(Void unused) {
		try {
			pd.dismiss();
		} catch(Exception e){
			pd = null;
		}
		BeepManager beep = new BeepManager(this.myContext);
		beep.playBeep();
	}

}
