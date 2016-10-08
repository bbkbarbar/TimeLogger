package hu.barbar.timelogger.util;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import hu.barbar.timelogger.TimeLoggerApp;

public class DeviceHandler {
	
	public static final String TAG = "TimeLogger.DeviceHandler";
	
	private TimeLoggerApp myParent = null;

	public DeviceHandler(TimeLoggerApp parent) {
		
		this.myParent = parent;
		
	}

	
	public void setBluetoothState(boolean state) {
		
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); 
		
		if(!mBluetoothAdapter.isEnabled() && state){
			mBluetoothAdapter.enable();
		}
		if(mBluetoothAdapter.isEnabled() && state == false){
			mBluetoothAdapter.disable();
		}
		
	}


	public void setAutoOrientationState(Context context, boolean enabled) {
          Settings.System.putInt( context.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, enabled ? 1 : 0);
    }
	
	
}
