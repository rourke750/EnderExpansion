package com.untamedears.EnderExpansion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LoadInventories {
	private Map<Player, Location> blockloc= new HashMap<Player, Location>();
	private Map<Location, List<Player>> playerlist= new HashMap<Location, List<Player>>();

public Location getBlock(Player player){
	return blockloc.get(player); 
}
public void setBlock(Player player, Location loc){
	blockloc.put(player, loc);
}
public void removePlayer(Player player){
	blockloc.remove(player);
}

public List<Player> getPlayerList(Location loc){
	return playerlist.get(loc);
}
	
public void addPlayerList(Location loc, Player player){
	List<Player> pl= new ArrayList<Player>();
	if (playerlist.get(loc)==null){
		pl.add(player);
		playerlist.put(loc, pl);
	}
	else{
		playerlist.get(loc).add(player);
	}
}
public void removePlayer(Location loc, Player player){
	List<Player> pl= new ArrayList<Player>();
	for (Player a: playerlist.get(loc)){
		if (a==player) continue;
		pl.add(a);
	}
	playerlist.put(loc, pl);
}
}
