package com.hack.regionunlocked;

import android.os.Bundle;
import android.view.ViewDebug.FlagToString;
import android.widget.TextView;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;

public class ResultsActivity extends Activity {
	
	@Override
	public void onBackPressed() {
		 //this.startActivity(new Intent(ResultsActivity.this,MainActivity.class));
		
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.results);
		/*ImageButton scanButton = (ImageButton) findViewById(R.id.scanButton);
	    scanButton.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		Intent clickIntent = new Intent(getApplicationContext(), ScanBarcodeActivity.class);
	    		startActivityForResult(clickIntent, 1);
			 }
		 });*/
		
	}
}
