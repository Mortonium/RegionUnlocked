package com.hack.regionunlocked;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class Settings extends Activity {
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
		
		final GlobalVariables setting = new GlobalVariables();

		final RadioButton PAL = (RadioButton) findViewById(R.id.radPal);
		final RadioButton NSTCJ = (RadioButton) findViewById(R.id.radNTSCJ);
		final RadioButton NSTCUC = (RadioButton) findViewById(R.id.radNTSCUC);

		RadioGroup options = (RadioGroup) findViewById(R.id.radioGroup1);
		options.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(RadioGroup group, int id) {
				if (id == (R.id.radPal)) {
					setting.setReigonSetting((String) PAL.getText());
				} else if (id == (R.id.radNTSCJ)) {
					setting.setReigonSetting((String) NSTCJ.getText());
				} else if (id == (R.id.radNTSCUC)) {
					setting.setReigonSetting((String) NSTCUC.getText());
				}
				
				System.out.println(setting.getReigonString());
			}
		});
		
		ImageButton back = (ImageButton) findViewById(R.id.imageButton1);
		back.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				finish();
			}
		});
	}
}
