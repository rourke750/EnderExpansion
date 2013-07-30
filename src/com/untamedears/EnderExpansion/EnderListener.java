package com.untamedears.EnderExpansion;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.EnderChest;
import org.bukkit.event.block.BlockBreakEvent;

public class EnderListener implements Listener{
	private LoadInventories li;
public EnderListener(LoadInventories lin){
	li=lin;
}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void PlayerplaceEnderChest(BlockPlaceEvent event){  // On Enderchest placement it creates an inventory
		if (event.getBlock().getType()==Material.ENDER_CHEST){ // for that block.
			Location loc= event.getBlock().getLocation();      // would be nice if bukkit made sense.
			li.createInventory(loc);
		}
	}
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void Playerinteractevent(InventoryOpenEvent event){  
		Player player= (Player) event.getPlayer();
		if (event.getInventory().getType()== InventoryType.ENDER_CHEST){
			event.setCancelled(true);
			Block block = player.getTargetBlock(null, 5);
			Location loc= block.getLocation();
			Inventory inv=li.getInventory(loc);
			if (inv==null){
				player.sendMessage(ChatColor.RED+"Please replace your Ender Chest."); // for preexisting ender chests.
				return;
			}
			li.setBlock(player, loc); // Allows me to find the block in certain methods that dont allow me.
			player.openInventory(inv);
			player.updateInventory();  // refreshes player inventory.
			event.setCancelled(true); // cancels the opening of the standard enderchests so my version can open.
		}
	}
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void Playerinteractmoveevent(InventoryDragEvent event){
		if (event.getInventory().getType()==InventoryType.ENDER_CHEST){
			Inventory inv=event.getInventory();
			Player player= (Player) event.getWhoClicked();
			Location loc= li.getBlock(player);
			li.addInventory(loc, inv);
			Inventory newinv= li.getInventory(loc);
			player.openInventory(newinv);
			List<HumanEntity> viewers= event.getInventory().getViewers();
			for (HumanEntity pl:viewers){
				if (pl instanceof Player){
					((Player) pl).updateInventory(); //updates everyones inventory that is looking in the enderchest.
				}
			}
		}
	}
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void PlayerClickEvent(InventoryClickEvent event){
		if (event.getInventory().getType()==InventoryType.ENDER_CHEST){
			Inventory inv=event.getInventory();
			Player player= (Player) event.getWhoClicked();
			Location loc= li.getBlock(player);
			li.addInventory(loc, inv);
			Inventory newinv= li.getInventory(loc);
			player.openInventory(newinv);
			List<HumanEntity> viewers= event.getInventory().getViewers();
			for (HumanEntity pl:viewers){
				if (pl instanceof Player){
					((Player) pl).updateInventory(); //updates everyones inventory that is looking in the enderchest.
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void PlayercloseInventory(InventoryCloseEvent event){
		Player player= (Player) event.getPlayer();
		if (li.getBlock(player)!=null){
			li.removePlayer(player); // After they close the inventory it removes them from the list.
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void BlockBreakEvent(BlockBreakEvent event){
		Material mat=event.getBlock().getType();
		if (mat==Material.ENDER_CHEST){
			Location loc= event.getBlock().getLocation();
			if (li.getInventory(loc)==null){
				return; // for preexisting enderchests.
			}
			li.deleteInventory(loc); // Removes the inventory and drops contents onto the floor.
		}
	}
}
