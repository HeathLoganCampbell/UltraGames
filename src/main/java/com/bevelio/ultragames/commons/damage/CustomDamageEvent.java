package com.bevelio.ultragames.commons.damage;

import java.util.ArrayList;
import java.util.HashMap;

import javax.annotation.Nonnull;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;

public class CustomDamageEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private EntityDamageEvent.DamageCause eventCause;
	private double initialDamage;
	private ArrayList<DamageChange> damageMult = new ArrayList<>();
	private ArrayList<DamageChange> damageMod = new ArrayList<>();

	private ArrayList<String> cancellers = new ArrayList<>();

	private HashMap<String, Double> knockbackMod = new HashMap<>();
	private LivingEntity damagedEntity;
	private Player damagedPlayer;
	private LivingEntity damagerEntity;
	private Player damagerPlayer;
	private Projectile projectile;
	private Location knockbackOrigin = null;

	private boolean ignoreArmor = false;
	private boolean ignoreRate = false;
	private boolean knockback = true;
	private boolean damagedBrute = false;
	private boolean damageToLevel = true;

	public CustomDamageEvent(LivingEntity damagee, LivingEntity damager, Projectile projectile, EntityDamageEvent.DamageCause cause, double damage, boolean knockback, boolean ignoreRate, boolean ignoreArmor, String initialSource, String initialReason, boolean cancelled) {
	    this.eventCause = cause;

	    this.initialDamage = damage;

	    this.damagedEntity = damagee;
	    if ((this.damagedEntity != null) && ((this.damagedEntity instanceof Player))) this.damagedPlayer = ((Player)this.damagedEntity);

	    this.damagerEntity = damager;
	    if ((this.damagerEntity != null) && ((this.damagerEntity instanceof Player))) this.damagerPlayer = ((Player)this.damagerEntity);

	    this.projectile = projectile;

	    this.knockback = knockback;
	    this.ignoreRate = ignoreRate;
	    this.ignoreArmor = ignoreArmor;

	    if ((initialSource != null) && (initialReason != null)) 
	    	addMod(initialSource, initialReason, 0.0D, true);
	    
	 	if (this.eventCause == EntityDamageEvent.DamageCause.FALL)
	    	this.ignoreArmor = true;
	    
	    if (cancelled)
	      setCancelled("Pre-Cancelled");
	  }
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	public void addMult(String source, String reason, double mod, boolean useAttackName) {
		this.damageMult.add(new DamageChange(source, reason, mod, useAttackName));
	}

	 
	public void addMod(String source, String reason, double mod, boolean useAttackName) {
	    this.damageMod.add(new DamageChange(source, reason, mod, useAttackName));
	}

	public void addKnockback(String reason, double d) {
		this.knockbackMod.put(reason, Double.valueOf(d));
	}

	public boolean isCancelled() {
		return !this.cancellers.isEmpty();
	}

	public void setCancelled(String reason) {
		this.cancellers.add(reason);
	}

	public double getDamage() {
		double damage = getDamageInitial();

		for (DamageChange mult : this.damageMod) 
			damage += mult.getDamage();
	    
	    for (DamageChange mult : this.damageMult) 
	      damage *= mult.getDamage();
	    return damage;
	}

	public LivingEntity getDamagedEntity() {
		return this.damagedEntity;
	}

	public Player getDamagedPlayer() {
		return this.damagedPlayer;
	}

	public LivingEntity getDamagerEntity(boolean ranged) {
		if (ranged) 
			return this.damagerEntity;
	    
	    if (this.projectile == null) 
	    	return this.damagerEntity;
	    return null;
	  }

	public Player getDamagerPlayer(boolean ranged) {
		if (ranged) 
			return this.damagerPlayer;
	    
	    if (this.projectile == null) 
	      return this.damagerPlayer;
	    
	    return null;
	}

	public Projectile getProjectile(){
		return this.projectile;
	}

	public EntityDamageEvent.DamageCause getCause() {
		return this.eventCause;
	}

	public double getDamageInitial() {
	    return this.initialDamage;
	}

	public void setIgnoreArmor(boolean ignore) {
		this.ignoreArmor = ignore;
	}

	public void setIgnoreRate(boolean ignore) {
		this.ignoreRate = ignore;
	}

	public void setKnockback(boolean knockback) {
		this.knockback = knockback;
	}

	public void setBrute() {
		this.damagedBrute = true;
	}

	public boolean isBrute() {
		return this.damagedBrute;
	}

	public String getReason() {
		String reason = "";

	    for (DamageChange change : this.damageMod) 
	    	if (change.useReason()) 
	    		reason = reason + ChatColor.WHITE + change.getReason() + ChatColor.GRAY + ", ";
	    
	    if (reason.length() > 0) {
	    	reason = reason.substring(0, reason.length() - 2);
	    	return reason;
	    }

	    return null;
	}

	public boolean isKnockback() {
		return this.knockback;
	}

	public boolean ignoreRate() {
		return this.ignoreRate;
	}

	public boolean ignoreArmor() {
		return this.ignoreArmor;
	}

	public void SetDamager(LivingEntity ent) {
		if (ent == null) return;
	    this.damagerEntity = ent;
	    this.damagerPlayer = null;
	    
	    if ((ent instanceof Player)) 
	    	this.damagerPlayer = ((Player)ent);
	}

	public void setDamaged(@Nonnull LivingEntity ent) {
		this.damagedEntity = ent;
	    this.damagedPlayer = null;
	    
	    if ((ent instanceof Player))
	    	this.damagedPlayer = ((Player)ent);
	}

	public void changeReason(String initial, String reason) {
		for (DamageChange change : this.damageMod)
			if (change.getReason().equals(initial))
				change.setReason(reason);
	}

	public void setKnockbackOrigin(Location loc){
		this.knockbackOrigin = loc;
	}

	public Location getKnockbackOrigin() {
		return this.knockbackOrigin;
	}

	public ArrayList<DamageChange> getDamageMod() {
		return this.damageMod;
	}

	public ArrayList<DamageChange> getDamageMult() {
	    return this.damageMult;
	}

	public HashMap<String, Double> getKnockback() {
		return this.knockbackMod;
	}

	public ArrayList<String> getCancellers() {
		return this.cancellers;
	}

	public void setDamageToLevel(boolean val) {
		this.damageToLevel = val;
	}

	public boolean displayDamageToLevel() {
		return this.damageToLevel;
	}


	@Deprecated
	public void setCancelled(boolean isCancelled) {
		setCancelled("No reason given because SOMEONE IS AN IDIOT");
	}
}
