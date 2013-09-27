package com.untamedears.EnderExpansion;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.server.v1_6_R3.NBTBase;
import net.minecraft.server.v1_6_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_6_R3.NBTTagCompound;
import net.minecraft.server.v1_6_R3.NBTBase;
import net.minecraft.server.v1_6_R3.NBTTagList;
import net.minecraft.server.v1_6_R3.IInventory;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftInventory;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftInventoryCustom;
import org.bukkit.craftbukkit.v1_6_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SaveManager {
	private LoadInventories li;
	public SaveManager(LoadInventories load){
		li=load;
	}
	  private Map<Location, Info> ptoinfo_ = new HashMap<Location, Info>();
	  private Map<IInventory, Info> invtoinfo_ = new HashMap<IInventory, Info>();
	  private File inventoryDir_ = null;
	  
	public class Info {
	    public Info(Location lo, Inventory i) {
	      loc = lo;
	      inv = i;
	      iinv = ((CraftInventory)i).getInventory();;
	    }
	    public Location loc;
	    public Inventory inv;
	    public IInventory iinv;
	  }

	  public Inventory loadInventory(Location loc) {
	    File inventorySave = new File(inventoryDir_, String.format("inv_%s.dat", loc.toString()));
	    if (!inventorySave.exists()) {
	      return null;
	    }
	    NBTTagCompound nbtInventory = null;
	    try {
	      FileInputStream fis = new FileInputStream(inventorySave);
	      nbtInventory = NBTCompressedStreamTools.a(fis);
	      fis.close();
	    } catch (Exception e) {
	      System.out.println("loadInventory Exception: " + e.toString());
	      return null;
	    }
	    
	    if (!nbtInventory.getString("Location").equals(loc.toString())) {
	      return null;
	    }
	    Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST);
	    NBTTagList nbtList = nbtInventory.getList("Items");
	    for (int i = 0; i < nbtList.size(); ++i) {
	      NBTTagCompound itemTag = (NBTTagCompound)nbtList.get(i);
	      int slot = itemTag.getInt("Slot");
	      net.minecraft.server.v1_6_R3.ItemStack nmsis =
	    		  net.minecraft.server.v1_6_R3.ItemStack.createStack(itemTag);
	      if (nmsis == null) {
	        continue;
	      }
	      inv.setItem(slot, CraftItemStack.asBukkitCopy(nmsis));
	    }
	    return inv;
	  }
	  
	public void saveInventory(Location loc, Info info) {
	    Inventory inv = info.inv;
	    if (!(inv instanceof CraftInventoryCustom)) {
	      return;
	    }
	    File inventorySave = new File(inventoryDir_, String.format("inv_%s.dat", loc));
	    if (inventorySave.exists()) {
	      inventorySave.delete();
	    }
	    CraftInventoryCustom cinv = (CraftInventoryCustom)inv;
	    NBTTagCompound nbtInventory = new NBTTagCompound("Inventory");
	    NBTTagList nbtList = new NBTTagList("Items");
	    nbtInventory.set("Items", nbtList);
	    nbtInventory.setString("Location", loc.toString());
	    ItemStack[] items = cinv.getContents();
	    for (int slot = 0; slot < items.length; ++slot) {
	      ItemStack item = items[slot];
	      if (item == null) {
	        continue;
	      }
	      net.minecraft.server.v1_6_R3.ItemStack nmsis = CraftItemStack.asNMSCopy(item);
	      NBTTagCompound serializedItem = new NBTTagCompound();
	      nmsis.save(serializedItem);
	      serializedItem.setInt("Slot", slot);
	      nbtList.add(serializedItem);
	    }
	    try {
	      FileOutputStream fos = new FileOutputStream(inventorySave);
	      NBTCompressedStreamTools.a(nbtInventory, fos);
	      fos.close();
	    } catch (Exception e) {
	      System.out.println("saveInventory Exception: " + e.toString());
	      return;
	    }
	  }

	  public Info getInfo(Location loc) {
	    Info info = ptoinfo_.get(loc);
	    boolean newinfo = false;
	    if (info == null) {
	      Inventory inv = loadInventory(loc);
	      if (inv != null) {
	        newinfo = true;
	        info = new Info(loc, inv);
	      }
	    }
	    if (info == null) {
	      Inventory inv = Bukkit.createInventory(null, InventoryType.CHEST);
	      if (inv != null) {
	        newinfo = true;
	        info = new Info(loc, inv);
	      }
	    }
	    if (newinfo) {
	      ptoinfo_.put(loc, info);
	      invtoinfo_.put(info.iinv, info);
	    }
	    return info;
	  }
	  public void setFile(File file){
		  inventoryDir_=file;
	  }
	  public void deleteInventory(Location loc){
		  File inventorySave = new File(inventoryDir_, String.format("inv_%s.dat", loc));
		    if (inventorySave.exists()) {
		      inventorySave.delete();
		      ptoinfo_.remove(loc);
		    }
		    
	  }
	  public void forceSave(){
		  for (Info in:ptoinfo_.values()){
			  saveInventory(in.loc, in);
		  }
	  }

	  public Boolean isInventory(Location loc){
		  if (ptoinfo_.get(loc)==null) return false;
		  return true;
	  }
}
