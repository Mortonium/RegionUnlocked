package com.hack.regionunlocked;

import java.util.Map;
import java.util.HashMap;

public class RegionSupportStatusSet {
	public GameRegion gameRegion;
	public Map<GameRegion,RegionSupportStatus> supportStatuses;
	public RegionSupportStatusSet() {
		supportStatuses = new Map<GameRegion,RegionSupportStatus>();
	}
	public RegionSupportStatusSet(GameRegion gameRegion) {
		this.gameRegion = gameRegion;
		supportStatuses = new Map<GameRegion,RegionSupportStatus>();
	}
}