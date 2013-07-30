package com.untamedears.EnderExpansion;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;


public class Enderplugin extends JavaPlugin{
	private EnderListener el;
	public BufferedWriter writer;
	public File file;
	private SaveManager sm;
	public void onEnable(){
		LoadInventories li= new LoadInventories();
		SaveManager sm= new SaveManager(li);
		el= new EnderListener(li);
		String dir = this.getDataFolder() + File.separator + "Inventories" + File.separator; // creates the directory
		new File(dir).mkdirs();
		try {
			File existing = new File(dir + "Storage.txt");
			// Check for file
			if (existing.exists()) {
				Logger.getLogger(Enderplugin.class.getName()).log(Level.INFO, "Existing file", "");
				FileWriter fw = new FileWriter(existing.getAbsoluteFile(), true);
				writer = new BufferedWriter(fw);
				file=existing;
			}
			// If file doesnt exist it creates it.
			else {
				Logger.getLogger(Enderplugin.class.getName()).log(Level.INFO, "Making a new file", "");
				PrintWriter fstream = new PrintWriter(dir + "Storage.txt");
				writer = new BufferedWriter(fstream);
				file=existing;
			}

		}
		catch (IOException e) {
			e.printStackTrace();
		}
		final SaveManager smm=sm;
		try {
			smm.load(file);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				try {
					smm.save(file); // Saves enderchests to file every 200 ticks.
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, 0, 200);
		enableListener();
	}
	public void onDisable(){
		try {
			sm.save(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void enableListener() {
		getServer().getPluginManager().registerEvents(el, this);
	}
}
