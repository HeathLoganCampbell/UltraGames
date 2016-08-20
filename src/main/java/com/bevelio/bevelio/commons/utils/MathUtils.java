package com.bevelio.bevelio.commons.utils;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.util.Vector;

public class MathUtils {
	private static Random ran;
	private static double PI = 3.1415926;
	
	public static double getPI() {
		return PI;
	}
	
	public static double clamp(double value, double min, double max) {
		if(value > max) return max;
		if(value < min) return min;
		return value;
	}
	
	public static Random getRandom() {
		if(ran == null)
			ran = new Random();
		return ran;
	}
	
	public static int getRandom(int range) {
		return MathUtils.getRandom().nextInt(range);
	}
	
	public static double offset2d(Location a, Location b) {
		return offset2d(a.toVector(), b.toVector());
	}
	
	public static double offset2d(Vector a, Vector b) {
		a.setY(0);
	    b.setY(0);
	    return a.subtract(b).length();
	}
	
	public static double offset(Location a, Location b) {
	    return a.toVector().subtract(b.toVector()).length();
	}
	
	public static double offset(Vector a, Vector b) {
	    return a.subtract(b).length();
	}
	
	public static double round3DP(double number) {
		return (double) Math.round(number * 1000) / 1000;
	}

	public static double round2DP(double d) {
		return (double) Math.round(d * 100) / 100;
	}
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }
	    return true;
	}
	
	public static Location lookAt(Location loc, Location lookat) {
        loc = loc.clone();
        
        double dx = lookat.getX() - loc.getX();
        double dy = lookat.getY() - loc.getY();
        double dz = lookat.getZ() - loc.getZ();
        if (dx != 0) {
            if (dx < 0) {
                loc.setYaw((float) (1.5 * Math.PI));
            } else {
                loc.setYaw((float) (0.5 * Math.PI));
            }
            loc.setYaw((float) loc.getYaw() - (float) Math.atan(dz / dx));
        } else if (dz < 0) {
            loc.setYaw((float) Math.PI);
        }
        double dxz = Math.sqrt(Math.pow(dx, 2) + Math.pow(dz, 2));

        loc.setPitch((float) -Math.atan(dy / dxz));

        loc.setYaw(-loc.getYaw() * 180f / (float) Math.PI);
        loc.setPitch(loc.getPitch() * 180f / (float) Math.PI);
        return loc;
    }
}
