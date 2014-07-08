package com.untamedears.EnderExpansion;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.untamedears.EnderExpansion.DataBase.InventoryManager;

public class Enderplugin extends JavaPlugin{
	
	private InventoryManager im;
	private InventoryHandler ih;
	
	public void onEnable(){
		ConfigManager config = new ConfigManager();
		config.initconfig(getConfig()); // Initializes the config
		saveConfig();
		
		im = new InventoryManager(getConfig(), this); // Initializes the class dealing with the database
		
		ih = new InventoryHandler(im, getConfig());
		
		registerListeners(); // take a guess
		saveAll(); // guess another time, peasant
	}
	
	public void onDisable(){
		ih.saveAllInventories();
	}
	
	public void registerListeners(){
		this.getServer().getPluginManager().registerEvents(new EnderListener(ih), this);
	}
	
	public void saveAll(){
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable(){

			@Override
			public void run() {
				ih.saveAllInventories();
			}
			
		},getConfig().getInt("save"), getConfig().getInt("save"));
	}
}
