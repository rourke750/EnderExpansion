package com.untamedears.EnderExpansion;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;



import net.minecraft.server.v1_6_R2.TileEntity;
import net.minecraft.server.v1_6_R2.TileEntityEnderChest;

import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_6_R2.CraftWorld;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
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

import com.untamedears.EnderExpansion.SaveManager.Info;

public class EnderListener implements Listener{
	private LoadInventories li;
	private SaveManager sm;
	private Enderplugin plugin;
public EnderListener(LoadInventories lin, SaveManager save, Enderplugin ep){
	li=lin;
	sm=save;
	plugin=ep;
}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void InteractEvent(PlayerInteractEvent event){
		if (event.getClickedBlock() instanceof EnderChest){
			event.setCancelled(true);
			
		}
	}
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void Playerinteractevent(InventoryOpenEvent event){
		Player player= (Player) event.getPlayer();
		if (event.getInventory().getType()== InventoryType.ENDER_CHEST){
			event.setCancelled(true); // cancels the opening of the standard enderchests so my version can open.
			
			Block block = player.getTargetBlock(null, 5);
			Location loc = block.getLocation();
			Location height=loc.clone();
			height.setY(height.getY()+1);
			Material check= height.getBlock().getType();
			if (!check.isTransparent()){
				return;
			}
			TileEntity tileEntity = ((CraftWorld) loc.getWorld()).getHandle().getTileEntity(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			player.getWorld().playSound(player.getLocation(), Sound.CHEST_OPEN, 1.0F, 1.0F); 
			if (tileEntity instanceof TileEntityEnderChest) {
	            ((TileEntityEnderChest) tileEntity).a(); 
	        }
			Inventory inv=sm.getInfo(loc).inv;
			li.setBlock(player, loc); // Allows me to find the block in certain methods that dont allow me.
			player.openInventory(inv);
			player.updateInventory();  // refreshes player inventory.
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void PlayercloseInventory(InventoryCloseEvent event){
		Player player= (Player) event.getPlayer();
		Location loc=li.getBlock(player);
		if (loc!=null){
			if (event.getViewers()==null){
			TileEntity tileEntity = ((CraftWorld) loc.getWorld()).getHandle().getTileEntity(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
			player.getWorld().playSound(player.getLocation(), Sound.CHEST_CLOSE, 1.0F, 1.0F); 
			if (tileEntity instanceof TileEntityEnderChest) {
	            ((TileEntityEnderChest) tileEntity).b(); // .close()
	        }
			}
			Info info=sm.getInfo(loc);
			li.removePlayer(player); // After they close the inventory it removes them from the list.
			sm.saveInventory(info.loc, info);
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void BlockBreakEvent(BlockBreakEvent event){
		Material mat=event.getBlock().getType();
		if (mat==Material.ENDER_CHEST){
			Location loc= event.getBlock().getLocation();
			if (sm.getInfo(loc)==null){
				return; // for preexisting enderchests.
			}
			Inventory inv=sm.getInfo(loc).inv;
			for (int i=0;i<27;i++){
				if (inv.getItem(i)==null){
				continue; // if an item spot is empty is skips trying to drop nothing and throwing an error.
				}
				loc.getWorld().dropItemNaturally(loc, inv.getItem(i)); //drops items to where the chest was.
				}
				List<HumanEntity> player=inv.getViewers();
				for (HumanEntity one: player){
				if (one instanceof Player){
				((Player) one).closeInventory();
				((Player) one).updateInventory(); // Closes the inventory of everyone looking at the chest.
				}
				}
			 sm.deleteInventory(loc);// Removes the inventory and drops contents onto the floor.
		}
	}
	
}
