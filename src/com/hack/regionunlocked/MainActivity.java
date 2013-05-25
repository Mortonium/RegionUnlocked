package com.hack.regionunlocked;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ImageButton scanButton = (ImageButton) findViewById(R.id.scanButton);
	    scanButton.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		startActivity(new Intent(getApplicationContext(), ScanBarcodeActivity.class));
			 }
		 });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
