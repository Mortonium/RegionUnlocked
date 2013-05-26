package com.hack.regionunlocked;

import android.os.Bundle;
import android.view.View;
import android.view.ViewDebug.FlagToString;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;

public class ResultsActivity extends Activity implements GameStatusCompleteListener  {
	
	GameStatus scanStatus;
	
	@Override
	public void setString(String s){
		TextView textView1 = (TextView) findViewById(R.id.textView1);
		textView1.setText(s);
	}
	
	@Override
	public void onGameStatusComplete(){
		if (scanStatus.wasSuccessful()){
			setString(scanStatus.getSupportAsText());
		}else{
			setString("Not found in databases.");
		}
	}
	
	@Override
	public void onGameStatusError(Exception ex){
		System.out.println("Because fuck you, that's why.");
		setString(ex.toString());
	}
	
	@Override
	public void onBackPressed() {
		 //this.startActivity(new Intent(ResultsActivity.this,MainActivity.class));
		
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
	}
	
	public void setScanResult(GameStatus status){
		scanStatus = status;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.results);
		
		String barcode = getIntent().getStringExtra("barcode");
		String result = "Looking up " + barcode + ".";
		
		try{
			scanStatus = new GameStatus(barcode, this);
			scanStatus.execute();
		}catch (Exception e){
			result = e.toString();
		}
		setString(result);
		
		Button scanagainButton = (Button) findViewById(R.id.scanagainButton);
		
		scanagainButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent clickIntent = new Intent(getApplicationContext(), ScanBarcodeActivity.class);
				startActivityForResult(clickIntent, 1);
			}
		});
		
		Button mainButton = (Button) findViewById(R.id.saveButton);
		mainButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				//NEED TO CALL SAVE CLASS WHEN IT IS MADE
				scanStatus.write();
			}
		});
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
