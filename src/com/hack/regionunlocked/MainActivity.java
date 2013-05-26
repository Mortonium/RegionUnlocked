package com.hack.regionunlocked;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


public class MainActivity extends Activity {
	
	GameStatus scanStatus;
	
	@Override
	public void onBackPressed() {
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button scanButton = (Button) findViewById(R.id.scanButton);

		//onActivityResult(1, RESULT_OK, "885370429671");
		scanButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent clickIntent = new Intent(getApplicationContext(), ScanBarcodeActivity.class);
				startActivityForResult(clickIntent, 1);
			}
		});
		
		Button settingsButton = (Button) findViewById(R.id.settingsButton);
		settingsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(), Settings.class));
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {
			
			if(resultCode == RESULT_OK){
				Intent resultsIntent = new Intent(getApplicationContext(), ResultsActivity.class);
				resultsIntent.putExtra("barcode", data.getStringExtra("barcode"));
				startActivity(resultsIntent);
			}
			if (resultCode == RESULT_CANCELED) {
				// Do nothing! Wait for the user to initiate another go.
			}
		}
	}
}
