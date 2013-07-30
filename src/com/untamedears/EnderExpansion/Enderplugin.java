package com.untamedears.EnderExpansion;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;


public class Enderplugin extends JavaPlugin{
	private EnderListener el;
	public BufferedWriter writer;
	public void onEnable(){
		LoadInventories li= new LoadInventories();
		el= new EnderListener(li);
		String dir = this.getDataFolder() + File.separator + "Inventories" + File.separator;
		new File(dir).mkdirs();
		try {
			File existing = new File(dir + "StoredBeacons.txt");
			if (existing.exists()) {
				Logger.getLogger(Enderplugin.class.getName()).log(Level.INFO, "Existing file", "");
				FileWriter fw = new FileWriter(existing.getAbsoluteFile(), true);
				writer = new BufferedWriter(fw);
			}
			else {
				Logger.getLogger(Enderplugin.class.getName()).log(Level.INFO, "Making a new file", "");
				PrintWriter fstream = new PrintWriter(dir + "StoredBeacons.txt");
				writer = new BufferedWriter(fstream);
			}

		}
		catch (IOException e) {
			e.printStackTrace();
		}
		enableListener();
	}
	public void onDisable(){
		
	}
	private void enableListener() {
		getServer().getPluginManager().registerEvents(el, this);
	}
}
