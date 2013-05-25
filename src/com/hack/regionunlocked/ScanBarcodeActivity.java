package com.hack.regionunlocked;

import com.mirasense.scanditsdk.ScanditSDKAutoAdjustingBarcodePicker;
import com.mirasense.scanditsdk.interfaces.ScanditSDK;
import com.mirasense.scanditsdk.interfaces.ScanditSDKListener;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class ScanBarcodeActivity extends Activity 
						  implements ScanditSDKListener {
	
	private ScanditSDK mBarcodePicker;
	
	public static final String sScanditSdkAppKey = 
			"j99gyMU7EeKEhv2QjwGjjbx+velVUkQJvtc+Rd2oKSI";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initializeAndStartBarcodeScanning();
		mBarcodePicker.startScanning();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void didScanBarcode(String barcode, String symbology) {
		mBarcodePicker.stopScanning();
		TextView mainText = (TextView) findViewById(R.id.mainTxt);
		mainText.setText(barcode);
		setContentView(R.layout.activity_main);
	}

	@Override
	public void didManualSearch(String entry) {}

	@Override
	public void didCancel() {}
	
	public void initializeAndStartBarcodeScanning() {
        // Switch to full screen.
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                             WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        // We instantiate the automatically adjusting barcode picker that will
        // choose the correct picker to instantiate. Be aware that this picker
        // should only be instantiated if the picker is shown full screen as the
        // legacy picker will rotate the orientation and not properly work in
        // non-fullscreen.
        ScanditSDKAutoAdjustingBarcodePicker picker = new ScanditSDKAutoAdjustingBarcodePicker(
                    this, sScanditSdkAppKey, ScanditSDKAutoAdjustingBarcodePicker.CAMERA_FACING_BACK);
        
        // Add both views to activity, with the scan GUI on top.
        setContentView(picker);
        mBarcodePicker = picker;
        
        // Register listener, in order to be notified about relevant events 
        // (e.g. a successfully scanned bar code).
        mBarcodePicker.getOverlayView().addListener(this);
        
        // show search bar in scan user interface
        mBarcodePicker.getOverlayView().showSearchBar(true);
    }

}
