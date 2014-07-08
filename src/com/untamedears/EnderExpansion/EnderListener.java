package com.untamedears.EnderExpansion;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import com.untamedears.citadel.Citadel;
import com.untamedears.citadel.ReinforcementManager;
import com.untamedears.citadel.entity.Faction;
import com.untamedears.citadel.entity.IReinforcement;
import com.untamedears.citadel.entity.PlayerReinforcement;

public class EnderListener implements Listener{

	private InventoryHandler ih;
	private ReinforcementManager rm;
	
	public EnderListener(InventoryHandler ih){
		this.ih = ih;
		rm = Citadel.getReinforcementManager();
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void inventoryOpenEvent(InventoryOpenEvent  event){
		if (event.getInventory().getType() != InventoryType.ENDER_CHEST) return; // you're not my real mom
		Location loc = event.getPlayer().getTargetBlock(null, 6).getLocation();
		event.setCancelled(true);
		Player player = (Player) event.getPlayer();
		IReinforcement rein = rm.getReinforcement(loc);
		Faction group = null;
		if (rein instanceof PlayerReinforcement) {
			group = ((PlayerReinforcement) rein).getOwner();
		}
		Inventory inv = ih.getInventory(loc);
		if (group == null || group.isFounder(player.getName()) || group.isModerator(player.getName()) || group.isMember(player.getName()))
			player.openInventory(inv); // opens the inventory for the player
	}
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void blockBreakEvent(BlockBreakEvent event){
		if (event.getBlock().getType() != Material.ENDER_CHEST) return;
		Location loc = event.getBlock().getLocation();
		ih.deleteInventory(loc);
	}
}
