package com.bevelio.bevelio.kit;

public enum ArmorType
{
	HELMET(0), CHESTPLATE(1), LEGGINGS(2), BOOTS(3);
	
	int id;
	
	ArmorType(int id)
	{
		this.id = id;
	}
	
	public int getId()
	{
		return this.id;
	}
	
	@Override
	public String toString()
	{
		return this.id + "";
	}
}
