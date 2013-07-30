package com.untamedears.EnderExpansion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class LoadInventories {
	private Map<Location,Inventory> inventory= new HashMap<Location,Inventory>();
	private Map<Player, Location> blockloc= new HashMap<Player, Location>();
	
public void addInventory(Location loc, Inventory inv){
	inventory.put(loc, inv);
}
public void removeItem(Location loc, int slot){
	// get back to this
}
public Inventory getInventory(Location loc){
	return inventory.get(loc);
}
public void deleteInventory(Location loc){
	World world=loc.getWorld();
		Inventory inv= inventory.get(loc);
		for (int i=0;i<36;i++){
		world.dropItemNaturally(loc, inv.getItem(i));
		}
		inventory.remove(loc);
	}

public Location getBlock(Player player){
	return blockloc.get(player);
}
public void setBlock(Player player, Location loc){
	blockloc.put(player, loc);
}
public void removePlayer(Player player){
	blockloc.remove(player);
}
public void createInventory(Location loc){
	inventory.put(loc, null);
}
}
