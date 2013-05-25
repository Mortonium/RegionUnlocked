package com.hack.regionunlocked;

import java.util.HashMap;

public class GameStatus {
	
	private String upcCode;
	private String name;
	private GameRegion gameRegion;
	private HashMap<GameRegion,RegionSupportStatus> supportStatuses;
	
	public GameStatus(String upcCode) {
		this.upcCode = upcCode;
	}
	
}