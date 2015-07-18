package com.example.test2;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

public class Zaxis extends Service implements SensorEventListener {
	SensorManager sensor_manager;
	Sensor zaxis;
	SharedPreferences shared_preferences;

	String packagename1=null;
    
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		String packagename = intent.getStringExtra("Package");
		packagename1=packagename;
		
		sensor_manager=(SensorManager) getSystemService(SENSOR_SERVICE);
		zaxis = sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		shared_preferences = getSharedPreferences("user3", MODE_PRIVATE);
		boolean status = shared_preferences.getBoolean("checkArray[2]", false);

		if (status == true) {
			sensor_manager.registerListener(this, zaxis,
					SensorManager.SENSOR_DELAY_NORMAL);
		} else
			sensor_manager.unregisterListener(this, zaxis);
		return START_STICKY;
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {

	}

	@Override
	public void onSensorChanged(SensorEvent arg0) {

		float[] sp = arg0.values;
		if (sp[2] > 13) {
			Intent launchIntent = getPackageManager()
					.getLaunchIntentForPackage(packagename1);
			startActivity(launchIntent);
		}
	}
}
