package com.untamedears.EnderExpansion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class LoadInventories {
	private Map<Player, Location> blockloc= new HashMap<Player, Location>();
	

public Location getBlock(Player player){
	return blockloc.get(player); 
}
public void setBlock(Player player, Location loc){
	blockloc.put(player, loc);
}
public void removePlayer(Player player){
	blockloc.remove(player);
}



}
