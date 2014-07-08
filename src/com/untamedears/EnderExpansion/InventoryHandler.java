package com.untamedears.EnderExpansion;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.untamedears.EnderExpansion.DataBase.InventoryManager;

public class InventoryHandler {

	private InventoryManager im;
	private FileConfiguration config;
	
	private Map<Location, Inventory> inventories = new HashMap<Location, Inventory>();
	
	public InventoryHandler(InventoryManager im, FileConfiguration config){
		this.im = im;
		this.config = config;
	}
	
	public Inventory getInventory(Location loc){
		if (inventories.get(loc) == null){
			Inventory inv = null;
			try {
				inv = im.getInventory(loc);
			} catch (ClassNotFoundException | SQLException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			inventories.put(loc, inv);
			return inv;
		}
		else return inventories.get(loc);
	}
	
	public void deleteInventory(Location loc){
		if (inventories.get(loc) != null){
			Inventory inv = inventories.get(loc);
			inventories.remove(loc); // remove from ram
			try {
				im.deleteInventory(loc);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for (ItemStack stack: inv.getContents()){
				if (stack == null)
					continue;
				loc.getWorld().dropItemNaturally(loc, stack);
			}
		}
	}
	
	public void saveAllInventories(){
		for (Location loc: inventories.keySet()){
			try {
				im.updateInventory(loc, inventories.get(loc));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
