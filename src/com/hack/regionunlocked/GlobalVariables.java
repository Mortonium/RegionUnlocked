package com.hack.regionunlocked;

public class GlobalVariables {
	
	private static String regionSetting = "";

	public static String getRegionString() {
		return regionSetting;
	}
	
	public static void setRegionSetting(String s){
		regionSetting = s;
	}
}
