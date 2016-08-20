package com.bevelio.ultragames.commons.damage;

public class DamageChange {
	private String source, reason;
	private double modifier;
	private boolean useReason;
	
	public DamageChange(String source, String reason, double modifier, boolean useReason) {
		this.source = source;
		this.reason = reason;
		this.modifier = modifier;
		this.useReason = useReason;
	}

	/**
	 * @return the source
	 */
	public String getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * @param reason the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * @return the modifier
	 */
	public double getDamage() {
		return modifier;
	}

	/**
	 * @param modifier the modifier to set
	 */
	public void setModifier(double modifier) {
		this.modifier = modifier;
	}

	/**
	 * @return the useReason
	 */
	public boolean useReason() {
		return useReason;
	}

	/**
	 * @param useReason the useReason to set
	 */
	public void setUseReason(boolean useReason) {
		this.useReason = useReason;
	}
}
