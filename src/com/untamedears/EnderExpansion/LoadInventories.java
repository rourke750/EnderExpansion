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
	private Map<Location,Inventory> inventory= new HashMap<Location,Inventory>();
	private Map<Player, Location> blockloc= new HashMap<Player, Location>();
	
public void addInventory(Location loc, Inventory inv){
	inventory.put(loc, inv);
}

public Inventory getInventory(Location loc){
	return inventory.get(loc);
}
@SuppressWarnings("deprecation")
public void deleteInventory(Location loc){
	World world=loc.getWorld();
		Inventory inv= inventory.get(loc);
		for (int i=0;i<36;i++){
			if (inv.getItem(i)==null){
				continue;
			}
		world.dropItemNaturally(loc, inv.getItem(i));
		}
		Inventory in=inventory.get(loc);
		List<HumanEntity> player=in.getViewers();
		for (HumanEntity one: player){
			if (one instanceof Player){
				((Player) one).closeInventory();
				((Player) one).updateInventory();
			}
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
	Inventory inv=Bukkit.createInventory(null, 36);
	inventory.put(loc, inv);
}
public Set<Location> getListLocation(){
	return inventory.keySet();
}
}
