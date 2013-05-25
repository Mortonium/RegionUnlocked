package com.hack.regionunlocked;

public interface GameStatusCompleteListener {

	public void onGameStatusComplete();
	
	public void onGameStatusError(Exception e);
	
	public void setString(String s);
	
}
