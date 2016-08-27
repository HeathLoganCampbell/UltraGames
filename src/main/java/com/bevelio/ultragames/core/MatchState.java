package com.bevelio.ultragames.core;

public enum MatchState 
{
	LOADING(-1), WAITING(-1), STARTING(9), LIVE(60 * 10), FINISHING(9), ENDED(-1);
	// WAITING 1s -> STARTING 15s -> LIVE Xs -> FINISHING 15s -> WAITING 1s -> STARTING 15s -> ...
	private int seconds;
	
	MatchState(int seconds)
	{
		this.seconds = seconds;
	}
	
	public int getSeconds()
	{
		return this.seconds;
	}
	
	public boolean isTimable()
	{
		return this.getSeconds() != -1;
	}
}
