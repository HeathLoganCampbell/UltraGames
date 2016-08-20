package com.bevelio.bevelio.commons.updater;

public enum UpdateType {
	TICK(49l),
	SLOW(200l),
	SLOWER(500l),
	SLOWEST(750l),
	SECOND(1000l),
	SEC_10(10000l), //BECAUSE BOBBY WANTED IT IN THE SERVER MANAGER CLASS
	SEC_30(30000l),
	MINUTE(60000l),
	MIN_2(120000l),
	MIN_4(240000l),
	MIN_8(480000l),
	HOUR(3600000l);
	
	private long last;
	private long time;
	
	UpdateType(long time) {
		this.time = time;
	}
	
	public boolean elapsed() {
		if(System.currentTimeMillis() - last > time) {
			this.last = System.currentTimeMillis();
			return true;
		}
		return false;
	}
	
	public long getMilliSeconds() {
		return time;
	}
}
