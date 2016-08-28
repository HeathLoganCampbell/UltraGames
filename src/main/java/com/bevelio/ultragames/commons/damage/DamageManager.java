package com.bevelio.ultragames.commons.damage;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Fish;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.bevelio.ultragames.commons.utils.GearUtils;

import net.minecraft.server.v1_8_R3.DamageSource;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.MobEffectList;

public class DamageManager implements Listener
{
	protected Field lastDamageByPlayerTime;
	protected Method getArmorMethod;
	public boolean useSimpleWeaponDamage = false;
	public boolean disableDamageChanges = false;

	public DamageManager(JavaPlugin plugin) {
		
		Bukkit.getPluginManager().registerEvents(this, plugin);
		
		try {
	      this.lastDamageByPlayerTime = EntityLiving.class.getDeclaredField("lastDamageByPlayerTime");
	      this.lastDamageByPlayerTime.setAccessible(true);
	      this.getArmorMethod = EntityLiving.class.getDeclaredMethod("damageArmor", new Class[] { Float.TYPE });
	      this.getArmorMethod.setAccessible(true);
	    } catch (Exception e) {
	      System.out.println("Problem getting access to EntityLiving: " + e.getMessage());
	    }
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.HIGHEST)
	public void StartDamageEvent(EntityDamageEvent event) {
		boolean preCancel = false;
		if (event.isCancelled()) preCancel = true;
		if (!(event.getEntity() instanceof LivingEntity)) return;
		
		LivingEntity damagee = getDamagedEntity(event);
	    LivingEntity damager = getDamagerEntity(event, true);
	    Projectile projectile = getProjectile(event);
		
	    if ((projectile instanceof Fish)) return;
	    
	    if (!this.disableDamageChanges) WeaponDamage(event, damager);
		double damage = event.getDamage();
		if ((projectile != null) && ((projectile instanceof Arrow)))	
		{
			damage = projectile.getVelocity().length() * 3.0D;
			projectile.remove();
		}
		
		callDamageEvent(damagee, damager, projectile, event.getCause(), damage, true, false, false, null, null, preCancel);
		
		event.setCancelled(true);
	}
	
	private LivingEntity getDamagedEntity(EntityDamageEvent e) {
		if ((e.getEntity() instanceof LivingEntity)) 
			return (LivingEntity) e.getEntity();
		return null;
	}
	
	private LivingEntity getDamagerEntity(EntityDamageEvent e, boolean isRanged)
	{
		if (!(e instanceof EntityDamageByEntityEvent)) return null;
		EntityDamageByEntityEvent eventEE = (EntityDamageByEntityEvent) e;
		if ((eventEE.getDamager() instanceof LivingEntity))  return (LivingEntity)eventEE.getDamager();
		if (!isRanged) return null;
		if (!(eventEE.getDamager() instanceof Projectile)) return null;
		Projectile projectile = (Projectile)eventEE.getDamager();
		if (projectile.getShooter() == null) return null;
		if (!(projectile.getShooter() instanceof LivingEntity)) return null;
		return (LivingEntity)projectile.getShooter();
	}
	
	private Projectile getProjectile(EntityDamageEvent e)
	{
	    if (!(e instanceof EntityDamageByEntityEvent)) return null;
	    EntityDamageByEntityEvent eventEE = (EntityDamageByEntityEvent) e;
	    if ((eventEE.getDamager() instanceof Projectile)) return (Projectile)eventEE.getDamager();
	    return null;
	}

	private void WeaponDamage(EntityDamageEvent e, LivingEntity ent) 
	{
		if (!(ent instanceof Player)) return;
	    if (e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;

	    Player damager = (Player) ent;

	    if (this.useSimpleWeaponDamage) 
	    {
	    	if (e.getDamage() > 1.0D)  e.setDamage(e.getDamage() - 1.0D);
	    	if ((GearUtils.isWeapon(damager.getItemInHand())) && (damager.getItemInHand().getType().name().contains("GOLD_")))
	    		e.setDamage(e.getDamage() + 2.0D);
	    	return;
	    }

	    if ((damager.getItemInHand() == null) || (!GearUtils.isWeapon(damager.getItemInHand()))) 
	    {
	    	e.setDamage(1.0D);
	    	return;
	    }

	    Material mat = damager.getItemInHand().getType();

	    int damage = 6;

	    if (mat.name().contains("WOOD")) damage -= 3;
	    else if (mat.name().contains("STONE")) damage -= 2;
	    else if (mat.name().contains("DIAMOND")) damage++;
	    else if (mat.name().contains("GOLD")) damage += 0;

	    e.setDamage(damage);
	}
	
	public void callDamageEvent(LivingEntity damagee, LivingEntity damager, Projectile proj, EntityDamageEvent.DamageCause cause, double damage, boolean knockback, boolean ignoreRate, boolean ignoreArmor, String source, String reason) {
		callDamageEvent(damagee, damager, proj, cause, damage, knockback, ignoreRate, ignoreArmor, source, reason, false);
	}

	public void callDamageEvent(LivingEntity damagee, LivingEntity damager, Projectile proj, EntityDamageEvent.DamageCause cause, double damage, boolean knockback, boolean ignoreRate, boolean ignoreArmor, String source, String reason, boolean cancelled) {
		Bukkit.getPluginManager().callEvent(
			new CustomDamageEvent(damagee, damager, proj, cause, damage, knockback, ignoreRate, ignoreArmor, source, reason, cancelled));
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void CancelDamageEvent(CustomDamageEvent event) 
	{
		if (event.getDamagedEntity().getHealth() <= 0.0D) 
		{
			event.setCancelled("0 Health");
			return;
		}

		if (event.getDamagedPlayer() != null)
		{
			Player damagee = event.getDamagedPlayer();

			if (damagee.getGameMode() == GameMode.CREATIVE)
			{
				event.setCancelled("Damagee in Creative");
				return;
			}

//			if (!event.ignoreRate()) {
//				if (!this._combatManager.Get(damagee.getName()).CanBeHurtBy(event.GetDamagerEntity(true))) {
//					event.SetCancelled("World/Monster Damage Rate");
//					return;
//				}
//			}
	    }

	    if (event.getDamagerPlayer(true) != null) 
	    {
	    	Player damager = event.getDamagerPlayer(true);

	    	if (damager.getGameMode() == GameMode.CREATIVE) 
	    	{
	    		event.setCancelled("Damager in Creative");
	    		return;
	    	}

//	      if ((!event.IgnoreRate()) &&  (!this._combatManager.Get(damager.getName()).CanHurt(event.GetDamageeEntity()))) {
//	        event.SetCancelled("PvP Damage Rate");
//	        return;
//	      }
	    }
	}
	
	@EventHandler
	public void handleEnchants(CustomDamageEvent event)
	{
		if (event.isCancelled()) return;
		Player damaged = event.getDamagedPlayer();
	    
	    if (damaged != null) 
	    {
	    	for (ItemStack stack : damaged.getInventory().getArmorContents())
	    	{
	    		if (stack != null)
	    		{
	    			Map<Enchantment, Integer> enchants = stack.getEnchantments();
	    			for (Enchantment e : enchants.keySet()) {
	    				if (e.equals(Enchantment.PROTECTION_ENVIRONMENTAL)) 
	    					event.addMod("Ench Prot", damaged.getName(), 0.5D * ((Integer)enchants.get(e)).intValue(), false);
	    	            
	    				else if ((e.equals(Enchantment.PROTECTION_FIRE)) 
	    						&& (event.getCause() == EntityDamageEvent.DamageCause.FIRE)
	    						&& (event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK) 
	    						&& (event.getCause() == EntityDamageEvent.DamageCause.LAVA)) 
	    					event.addMod("Ench Prot", damaged.getName(), 0.5D * ((Integer)enchants.get(e)).intValue(), false);
	    	            
	    	            else if ((e.equals(Enchantment.PROTECTION_FALL))
	    	            		 && (event.getCause() == EntityDamageEvent.DamageCause.FALL)) 
	    	            	event.addMod("Ench Prot", damaged.getName(), 0.5D * ((Integer)enchants.get(e)).intValue(), false);
	    	            
	    	            else if ((e.equals(Enchantment.PROTECTION_EXPLOSIONS))
	    	            		&& (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) 
	    	            	event.addMod("Ench Prot", damaged.getName(), 0.5D * ((Integer)enchants.get(e)).intValue(), false);
	    	            
	    	            else if ((e.equals(Enchantment.PROTECTION_PROJECTILE)) 
	    	            		&& (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE)) 
	    	            	event.addMod("Ench Prot", damaged.getName(), 0.5D * ((Integer)enchants.get(e)).intValue(), false);
	    			}
	    		}
	    	}
	    }
	    Player damager = event.getDamagerPlayer(true);
	    if (damager != null)
	    {
	    	ItemStack stack = damager.getItemInHand();
	        if (stack == null) return;
	        
	        Map<Enchantment, Integer> enchants = stack.getEnchantments();
	        for (Enchantment e : enchants.keySet()) 
	        {
	          if ((e.equals(Enchantment.ARROW_KNOCKBACK)) || (e.equals(Enchantment.KNOCKBACK))) 
	            event.addKnockback("Ench Knockback", 1.0D + 0.5D * ((Integer)enchants.get(e)).intValue());
	          
	          else if (e.equals(Enchantment.ARROW_DAMAGE)) 
	            event.addMod("Enchant", "Ench Damage", 0.5D * ((Integer)enchants.get(e)).intValue(), true);
	          
//	          else if (((e.equals(Enchantment.ARROW_FIRE)) || (e.equals(Enchantment.FIRE_ASPECT))) 
//	        		  &&  (this._conditionManager != null))
//	            this._conditionManager.Factory().Ignite("Ench Fire", event.GetDamageeEntity(), damager, 
//	              1.0D * ((Integer)enchants.get(e)).intValue(), false, false);
	        }
	    }
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void finalDamage(CustomDamageEvent event) 
	{
		if ((event.isCancelled()) && (event.getDamage() > 0.0D)) return;
		this.damage(event);
		
		if ((event.getProjectile() != null) && ((event.getProjectile() instanceof Arrow))) 
		{
			Player player = event.getDamagerPlayer(true);
			if(player == null) return;
			
			player.playSound(player.getLocation(), Sound.ORB_PICKUP, 0.5F, 0.5F);
			
			Player damagedPlayer = event.getDamagedPlayer();
			if(damagedPlayer.getLocation() != null) 
			{
				double distance = player.getLocation().distance(damagedPlayer.getLocation());			
//				MessageApi.sendMessage(player, CC.header + "you shot " + damagedPlayer.getDisplayName() + CC.header + " from " + CC.spec(distance + "") + CC.header + " blocks", MessageType.ACTION);
			}
		}
	}
	
	private boolean applyDamage(EntityLiving entityLiving, float damage, boolean ignoreArmor) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException 
	{
		if (!ignoreArmor) 
		{
			int j = 25 - entityLiving.aE();
			float k = damage * j;

			this.getArmorMethod.invoke(entityLiving, new Object[] { Float.valueOf(damage) });
			damage = k / 25.0F;
		}
		
		if(entityLiving.hasEffect(MobEffectList.RESISTANCE)) 
		{
		    int j = (entityLiving.getEffect(MobEffectList.RESISTANCE).getAmplifier() + 1) * 5;
		    int k = 25 - j;
		    int l = (int) (damage * k);
		    damage = l / 25;
		}
	
		float finalhealth = entityLiving.getHealth() - damage;
	    entityLiving.setHealth(finalhealth);
	    return false;
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent e) 
	{
		Player player = e.getEntity();
		player.setHealth(player.getMaxHealth());
		player.setFallDistance(0);
		player.setFireTicks(0);
	}
	
	private void handleDamage(LivingEntity damagee, LivingEntity damager, EntityDamageEvent.DamageCause cause, float damage, boolean ignoreArmor) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
	    EntityLiving entityDamagee = ((CraftLivingEntity) damagee).getHandle();
	    EntityLiving entityDamager = null;

	    if (damager != null) 
	      entityDamager = ((CraftLivingEntity) damager).getHandle();
	    entityDamagee.aG = 1.5F;
	    if (entityDamagee.noDamageTicks > entityDamagee.maxNoDamageTicks / 2.0F) 
	    {
	      if (damage <= entityDamagee.lastDamage) return;
	      
	      applyDamage(entityDamagee, damage - entityDamagee.lastDamage, ignoreArmor);
	      entityDamagee.lastDamage = damage;
	    } 
	    else
	    {
	      entityDamagee.lastDamage = damage;
	      entityDamagee.aw = entityDamagee.getHealth();

	      applyDamage(entityDamagee, damage, ignoreArmor);
	    }

	    if (entityDamager != null)
	      entityDamagee.b(entityDamager);
	    
	    if ((entityDamager != null) 
	    	&& ((entityDamager instanceof EntityHuman)))
	    {
	    	this.lastDamageByPlayerTime.setInt(entityDamagee, 100);
	    	entityDamagee.killer = ((EntityHuman)entityDamager);
	    }

	    if (entityDamagee.getHealth() <= 0.0F)
	    {
	    	if (entityDamager != null) {
	    		if ((entityDamager instanceof EntityHuman)) 
	    		{
	    			entityDamagee.die(DamageSource.playerAttack((EntityHuman) entityDamager));
	    		}
	    		else if ((entityDamager instanceof EntityLiving)) 
	    		{
	    			entityDamagee.die(DamageSource.mobAttack(entityDamager)); 
	    		}
	    		else 
	    		{
			    	entityDamagee.die(DamageSource.GENERIC);
	    		}
	    	} 
	    	else
	    	{ 
	    		entityDamagee.die(DamageSource.GENERIC);
	    	}
	    }
	}
	
	
	private void damage(CustomDamageEvent event)
	{
		if (event.getDamagedEntity() == null) return;
		if (event.getDamagedEntity().getHealth() <= 0.0D) return;
		
		if ((event.getDamagerPlayer(true) != null) && (event.displayDamageToLevel())) 
		{
			if (event.getCause() != EntityDamageEvent.DamageCause.THORNS) ;
				//event.getDamagerPlayer(true).setLevel((int) event.getDamage());
		}
		
		double bruteBonus = 0.0D;
		if (event.isBrute()) 
			if ((event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK)
					|| (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE)
					|| (event.getCause() == EntityDamageEvent.DamageCause.CUSTOM))
				bruteBonus = Math.min(8.0D, event.getDamage() * 2.0D);
	        
	      try {
	    	  handleDamage(event.getDamagedEntity(), event.getDamagerEntity(true), event.getCause(), (float)(event.getDamage() + bruteBonus), event.ignoreArmor());
	      } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
	    	  e.printStackTrace();
	      }
	      
	      
//		      if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE)
//		    	  ((CraftLivingEntity)event.getDamagedEntity()).getHandle().p(((CraftLivingEntity)event.getDamagedEntity()).getHandle().aZ() + 1);

	      if ((event.isKnockback()) && (event.getDamagerEntity(true) != null)) 
	      {
	    	  double knockback = event.getDamage() + 0.5;
	    	  if (knockback < 2.0D) knockback = 2.0D;
	    	  knockback = Math.log10(knockback);
	    	  
	    	  for (Iterator<Double> localIterator = event.getKnockback().values().iterator(); localIterator.hasNext(); ) 
	    	  { 
	    		  double cur = ((Double)localIterator.next()).doubleValue();
	    		  knockback *= cur;
	    	  }
	    	  
	    	  
	    	  
	    	  Location origin = event.getDamagerEntity(true).getLocation();
	          if (event.getKnockbackOrigin() != null)
	        	  origin = event.getKnockbackOrigin();
	          //attacked.setVelocity(attacked.getVelocity().add(attacked.getLocation().toVector().subtract(attacker.getLocation().toVector()).normalize().multiply(knockback)));
	         Vector trajectory = event.getDamagedEntity().getLocation().subtract(origin).toVector();

	          
	         trajectory.normalize().multiply(knockback);
	         trajectory.setY((trajectory.getY() + Math.abs(0.4D * knockback) + 0.2));
	         
	         if(trajectory.getY() > 0.6)
	         {
	        	 trajectory.setY(0.6);
	         }
	          
	         event.getDamagedEntity().setVelocity(trajectory);
	         event.getDamagedEntity().setFallDistance(0.0f);
	         
	         event.getDamagedEntity().getVelocity().add(event.getDamagedEntity().getLocation().toVector().subtract(origin.toVector()).normalize().multiply(0.7));
	         
	      }
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void damageSound(CustomDamageEvent e) {
		if (e.isCancelled()) return;
		
		LivingEntity damaged = e.getDamagedEntity();
	    if (damaged == null) return;
	    
	    Sound sound = Sound.HURT_FLESH;
	    float vol = 1.0F;
	    float pitch = 1.0F;
	    
	    if(damaged instanceof Player)
	    {
	    	ItemStack chest = ((Player) damaged).getInventory().getChestplate();
	    	if(chest != null)
	    	{
	    		switch(chest.getType())
	    		{
	    			case DIAMOND_CHESTPLATE:
	    				sound = Sound.ANVIL_LAND;
	    				break;
	    			case IRON_CHESTPLATE:
	    				sound = Sound.IRONGOLEM_HIT;
	    				break;
	    			case GOLD_CHESTPLATE:
    					sound = Sound.BLAZE_HIT;
    					break;
	    			case CHAINMAIL_CHESTPLATE:
    					sound = Sound.BLAZE_HIT;
    					break;
	    			case LEATHER_CHESTPLATE:
	    				sound = Sound.EAT;
	    				pitch = 0.1f;
	    				break;
	    			default:
	    				break;
	    		}
	    	}
	    }
	    
	    if(!e.isCancelled())
	    	if(e.getCancellers().size() == 0)
	    	{
	    		damaged.playEffect(EntityEffect.HURT);
	    		damaged.getWorld().playSound(damaged.getLocation(), sound, vol, pitch);
	    	}
	}
}
