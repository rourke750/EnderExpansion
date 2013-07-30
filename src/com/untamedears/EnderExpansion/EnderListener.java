package com.untamedears.EnderExpansion;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
	public void PlayerplaceEnderChest(BlockPlaceEvent event){
		if (event.getBlock().getType()==Material.ENDER_CHEST){
			Location loc= event.getBlock().getLocation();
			li.createInventory(loc);
		}
	}
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void Playerinteractevent(InventoryOpenEvent event){
		InventoryHolder holder = event.getInventory().getHolder();
		Player player= (Player) event.getPlayer();
		if (event.getInventory().getType()== InventoryType.ENDER_CHEST){
			event.setCancelled(true);
			Location loc;
			loc = ((Block)holder).getLocation();
			Inventory inv=li.getInventory(loc);
			li.setBlock(player, loc);
			player.openInventory(inv);
			player.updateInventory();
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
					((Player) pl).updateInventory();
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
					((Player) pl).updateInventory();
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void PlayercloseInventory(InventoryCloseEvent event){
		Player player= (Player) event.getPlayer();
		if (li.getBlock(player)!=null){
			li.removePlayer(player);
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void BlockBreakEvent(BlockBreakEvent event){
		Material mat=event.getBlock().getType();
		if (mat==Material.ENDER_CHEST){
			Location loc= event.getBlock().getLocation();
			li.deleteInventory(loc);
		}
	}
}
