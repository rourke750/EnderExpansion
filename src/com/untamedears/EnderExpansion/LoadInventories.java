package com.untamedears.EnderExpansion;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.entity.Player;

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
