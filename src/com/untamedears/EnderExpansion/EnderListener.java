package com.untamedears.EnderExpansion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;



import org.bukkit.block.Block;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.EnderChest;
import org.bukkit.event.block.BlockBreakEvent;

import com.untamedears.EnderExpansion.SaveManager.Info;
import com.untamedears.citadel.Citadel;
import com.untamedears.citadel.access.AccessDelegate;
import com.untamedears.citadel.entity.Faction;
import com.untamedears.citadel.entity.IReinforcement;
import com.untamedears.citadel.entity.PlayerReinforcement;

public class EnderListener implements Listener{
	private LoadInventories li;
	private SaveManager sm;
	private Enderplugin plugin;
public EnderListener(LoadInventories lin, SaveManager save, Enderplugin ep){
	li=lin;
	sm=save;
	plugin=ep;
}
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void Playerinteractevent(InventoryOpenEvent event){
		Player player= (Player) event.getPlayer();
		if (event.getInventory().getType()== InventoryType.ENDER_CHEST){
			event.setCancelled(true);
			Block block = player.getTargetBlock(null, 5);
			final Location loc= block.getLocation();
			Location check = block.getLocation().clone();
			Inventory inv=sm.getInfo(loc).inv;
			check.setY(check.getY()+1);
			int id;
			id= check.getBlock().getTypeId();
			boolean open;
			open = false;
			switch (id){
			case 53:
			case 67:
			case 108:
			case 109:
			case 114:
			case 128:
			case 134:
			case 135:
			case 136:
			case 156:
			case 0:
			case 130:
			case 126:
			case 44:
				open = true;
				break;
			}
			final Info info=sm.getInfo(loc);
			
			if (Bukkit.getPluginManager().isPluginEnabled("citadel")){
			IReinforcement rein = AccessDelegate.getDelegate(block).getReinforcement();
			if (!(rein instanceof PlayerReinforcement)) return;
			PlayerReinforcement prein = (PlayerReinforcement)rein;
			boolean hasAccess = prein.isAccessible(player);
			if (hasAccess != true) return;
			}
			
			if (open == true){
			li.setBlock(player, loc); // Allows me to find the block in certain methods that dont allow me.
			player.openInventory(inv);
			player.updateInventory();  // refreshes player inventory.
			li.addPlayerList(loc, player);
			Bukkit.getScheduler().runTask(plugin, new Runnable(){
				@Override
				public void run(){
				sm.saveInventory(loc, info);
				}
			});
			}
		}
	}
//	@SuppressWarnings("deprecation")
//	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
//	public void Playerinteractmoveevent(InventoryDragEvent event){
//		Player player = (Player) event.getWhoClicked();
//		final Location loc=li.getBlock(player);
//		if (loc==null) return;
//		final Info info=sm.getInfo(loc);
//		if (info!=null){
//			List<HumanEntity> viewers= event.getInventory().getViewers();
//			Bukkit.getScheduler().runTask(plugin, new Runnable(){
//				@Override
//				public void run(){
//				sm.saveInventory(loc, info);
//				}
//			});
//			for (HumanEntity pl:viewers){
//					if ((Player) pl==event.getWhoClicked()) continue;
//					((Player) pl).updateInventory(); //updates everyones inventory that is looking in the enderchest.
//				
//			}
//		}
//		else{
//			return;
//		}
//	}
//	@SuppressWarnings("deprecation")
//	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
//	public void PlayerClickEvent(InventoryClickEvent event){
//		Player player = (Player) event.getWhoClicked();
//		final Location loc=li.getBlock(player);
//		if (loc==null) return;
//		final Info info=sm.getInfo(loc);
//		if (info!=null){
//			List<HumanEntity> viewers= event.getInventory().getViewers();
//			Bukkit.getScheduler().runTask(plugin, new Runnable(){
//				@Override
//				public void run(){
//				sm.saveInventory(loc, info);
//				}
//			});
//			for (HumanEntity pl:viewers){
//				if (pl instanceof Player){
//					if ((Player) pl==event.getWhoClicked()) continue;
//					((Player) pl).updateInventory(); //updates everyones inventory that is looking in the enderchest.
//				}
//			}
//		}
//		else{
//			return;
//		}
	
//	}


	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void PlayercloseInventory(InventoryCloseEvent event){
		Player player= (Player) event.getPlayer();
		final Location loc=li.getBlock(player);
		if (loc!=null){
			if (event.getViewers()==null){
			player.getWorld().playSound(player.getLocation(), Sound.CHEST_CLOSE, 1.0F, 1.0F); 
			player.sendBlockChange(loc, Material.ENDER_CHEST, (byte) 0);
			}
			final Info info=sm.getInfo(loc);
			li.removePlayer(player); // After they close the inventory it removes them from the list.
			Bukkit.getScheduler().runTask(plugin, new Runnable(){
				@Override
				public void run(){
				sm.saveInventory(loc, info);
				}
			});
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void BlockBreakEvent(BlockBreakEvent event){
		Material mat=event.getBlock().getType();
		if (mat==Material.ENDER_CHEST){
			final Location loc= event.getBlock().getLocation();
			
			if (sm.isInventory(loc)==false){
				return; // for preexisting enderchests.
			}
			Inventory inv=sm.getInfo(loc).inv;
			for (int i=0;i<27;i++){
				if (inv.getItem(i)==null){
				continue; // if an item spot is empty is skips trying to drop nothing and throwing an error.
				}
				loc.getWorld().dropItemNaturally(loc, inv.getItem(i)); //drops items to where the chest was.
				}
				List<Player> player= li.getPlayerList(loc);
				if (player==null){
					player = new ArrayList<Player>();
					player.add(event.getPlayer());
				}
				for (Player one: player){
				li.removePlayer(loc, one);
				one.closeInventory();
				one.updateInventory(); // Closes the inventory of everyone looking at the chest.
				
				}
				
				Bukkit.getScheduler().runTask(plugin, new Runnable(){
					@Override
					public void run(){
					sm.deleteInventory(loc);// Removes the inventory and drops contents onto the floor.
					}
				});
		}
	}

}