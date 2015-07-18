package com.example.test2;

import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ListActivity {

	String[] Item = { "X-DIRECTION", "Y-DIRECTION", "Z-DIRECTION", "PROXIMITY" };
	boolean[] set_values = { false, false, false, false };
	boolean[] checkarray = new boolean[Item.length];
	boolean status1, status2, status3, status4;

	SharedPreferences shared_preferences1, shared_preferences2,
			shared_preferences3, shared_preferences4;
	SharedPreferences.Editor shared_preferences_editor1,
			shared_preferences_editor2, shared_preferences_editor3,
			shared_preferences_editor4;
	int counter = 1;
	Intent xaxis, yaxis, zaxis, proximity;

	private PackageManager packageManager = null;
	private List<ApplicationInfo> applist = null;
	private ApplicationAdapter listadaptor = null;
	String PackageName = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		shared_preferences1 = getSharedPreferences("user1", Context.MODE_PRIVATE);
		shared_preferences2 = getSharedPreferences("user2", Context.MODE_PRIVATE);
		shared_preferences3 = getSharedPreferences("user3", Context.MODE_PRIVATE);
		shared_preferences4 = getSharedPreferences("user4", Context.MODE_PRIVATE);
		shared_preferences_editor1 = shared_preferences1.edit();
		shared_preferences_editor2 = shared_preferences2.edit();
		shared_preferences_editor3 = shared_preferences3.edit();
		shared_preferences_editor4 = shared_preferences4.edit();
		status1 = shared_preferences1.getBoolean("checkArray[0]", false);
		status2 = shared_preferences2.getBoolean("checkArray[1]", false);
		status3 = shared_preferences3.getBoolean("checkArray[2]", false);
		status4 = shared_preferences4.getBoolean("checkArray[3]", false);

		packageManager = getPackageManager();
		
		new LoadApplications().execute();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);

		return true;
	}

	public void start(String packageName) {
		counter++;
		
		xaxis = new Intent(getBaseContext(), Xaxis.class);
		startService(xaxis);
		yaxis = new Intent(getBaseContext(), Yaxis.class);
		startService(yaxis);
		zaxis = new Intent(getBaseContext(), Zaxis.class);
		startService(zaxis);
		proximity = new Intent(getBaseContext(), Proximity.class);
		startService(proximity);
		
		PackageName = packageName;	
		showDialog(1);

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == 1) {
			Builder builder = new Builder(this);
			builder.setTitle("Sensor Selection");

			set_values[0] = status1;
			set_values[1] = status2;
			set_values[2] = status3;
			set_values[3] = status4;

			builder.setMultiChoiceItems(Item, set_values,
					new OnMultiChoiceClickListener() {

						@Override
						public void onClick(DialogInterface arg0, int arg1,
								boolean arg2) {
							
							if (arg2 == true) {
								checkarray[arg1] = arg2;
								if (arg1==0) {
									
									shared_preferences_editor1.putBoolean(
											"checkArray[0]", arg2);
									xaxis.putExtra("Package", PackageName);
									if (counter >= 2) {
										startService(xaxis);
									}

								}
								if (arg1==1) {
									shared_preferences_editor2.putBoolean(
											"checkArray[1]", arg2);
							        yaxis.putExtra("Package", PackageName);
									if (counter >= 2) {
										startService(yaxis);
									}
								}
								if (arg1==2) {
									shared_preferences_editor3.putBoolean(
											"checkArray[2]", arg2);
									zaxis.putExtra("Package", PackageName);
									if (counter >= 2) {
										startService(zaxis);
									}
								}
								if (arg1==3) {
									shared_preferences_editor4.putBoolean(
											"checkArray[3]", arg2);
									proximity.putExtra("Package", PackageName);
									if (counter >= 2) {
										startService(proximity);
									}
								}
								shared_preferences_editor1.commit();
								shared_preferences_editor2.commit();
								shared_preferences_editor3.commit();
								shared_preferences_editor4.commit();

							} else {
								checkarray[arg1] = arg2;
								if (arg1==0) {
									shared_preferences_editor1.putBoolean(
											"checkArray[0]", arg2);
									if (counter >= 2) {
										startService(xaxis);
									}
								}
								if (arg1==1) {
									shared_preferences_editor2.putBoolean(
											"checkArray[1]", arg2);
									if (counter >= 2) {
										startService(yaxis);
									}
								}
								if (arg1==2) {
									shared_preferences_editor3.putBoolean(
											"checkArray[2]", arg2);
									if (counter >= 2) {
										startService(zaxis);
									}
								}
								if (arg1==3) {
									shared_preferences_editor4.putBoolean(
											"checkArray[3]", arg2);
									if (counter >= 2) {
										startService(proximity);
									}
								}
								shared_preferences_editor1.commit();
								shared_preferences_editor2.commit();
								shared_preferences_editor3.commit();
								shared_preferences_editor4.commit();
							}
						}
					});
			return builder.create();
		}
		return super.onCreateDialog(id);

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		ApplicationInfo app = applist.get(position);
		try {
			Intent intent = packageManager
					.getLaunchIntentForPackage(app.packageName);

			if (null != intent) {

				start(app.packageName);

			}
		} catch (ActivityNotFoundException e) {
			Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG)
					.show();
		} catch (Exception e) {
			Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG)
					.show();
		}
	}

	private List<ApplicationInfo> checkForLaunchIntent(
			List<ApplicationInfo> list) {
		ArrayList<ApplicationInfo> applist = new ArrayList<ApplicationInfo>();
		for (ApplicationInfo info : list) {
			try {
				if (null != packageManager
						.getLaunchIntentForPackage(info.packageName)) {
					applist.add(info);

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return applist;
	}

	private class LoadApplications extends AsyncTask<Void, Void, Void> {
		private ProgressDialog progress = null;

		@Override
		protected Void doInBackground(Void... params) {
			applist = checkForLaunchIntent(packageManager
					.getInstalledApplications(PackageManager.GET_META_DATA));
			listadaptor = new ApplicationAdapter(MainActivity.this,
					R.layout.activity_main, applist);

			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Void result) {
			setListAdapter(listadaptor);
			progress.dismiss();
			super.onPostExecute(result);
		}

		@Override
		protected void onPreExecute() {
			progress = ProgressDialog.show(MainActivity.this, null,
					"Loading application info...");
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
	}

}
