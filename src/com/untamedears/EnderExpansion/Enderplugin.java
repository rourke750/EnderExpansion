package com.untamedears.EnderExpansion;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import com.untamedears.EnderExpansion.SaveManager.Info;


public class Enderplugin extends JavaPlugin{
	private EnderListener el;
	public BufferedWriter writer;
	private File file;
	private static SaveManager sm;
	private File inventoryDir_ = null;
	public void onEnable(){
		LoadInventories li= new LoadInventories();
		sm= new SaveManager(li);
		el= new EnderListener(li, sm, this);
		createInventoryDir();
		sm.setFile(file);
		enableListener();
		
	}
	public void onDisable(){
		sm.forceSave();
	}
	private void enableListener() {
		getServer().getPluginManager().registerEvents(el, this);
	}
	public void createInventoryDir() {
		    File dataFolder = this.getDataFolder();
		    if (!dataFolder.exists()) {
		      dataFolder.mkdir();  
		    }
		    inventoryDir_ = new File(dataFolder, "invdata");
		    file=inventoryDir_;
		    if (!inventoryDir_.exists()) {
		      inventoryDir_.mkdir();  
		    }
		  }
	
	public static Inventory getchestInventory(Location loc){
		return sm.getInfo(loc).inv;
	}
}
