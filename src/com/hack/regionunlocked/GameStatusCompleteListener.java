package com.hack.regionunlocked;

public interface GameStatusCompleteListener {

	public void onGameStatusComplete();
	
	public void onGameStatusError(GameStatusException e);
	
}
