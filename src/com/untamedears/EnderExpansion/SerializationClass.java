package com.untamedears.EnderExpansion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class SerializationClass implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6157960981723010864L;
	
	private SerializeInventoryContents inv;
	
	public SerializationClass(){
	}
	public void packInventory(Inventory inv){
		this.inv = new SerializeInventoryContents(inv.getContents());
	}
	
	public void updateInventory(Inventory inv){
		inv.setContents(this.inv.unPackInv());
	}
	
	class SerializeInventoryContents implements Serializable{
		private Map<Integer, StoredEnchantments> enchants = new HashMap<Integer, StoredEnchantments>(); // Item pos, enchant 
		private Map<Integer, SerializeLore> lore = new HashMap<Integer, SerializeLore>(); //item pos, lore
		private Map<Integer, SerializeBook> books = new HashMap<Integer, SerializeBook>(); //item pos, book
		/**
		 * 
		 */
		
		private static final long serialVersionUID = 1045353639041695968L;
		
		private Map<Integer, Integer> amount = new HashMap<Integer, Integer>(); // place, amount
		private Map<Integer, String> material = new HashMap<Integer, String>(); // place, material
		private Map<Integer, Short> durability = new HashMap<Integer, Short>(); // place, durability
		private Map<Integer, SerializedBooksimplements> enchantedbook = new HashMap<Integer, SerializedBooksimplements>();
		
		private int size;
		
		public SerializeInventoryContents(ItemStack[] contents) {
			int x = 0;
			size = contents.length;
			for (ItemStack item: contents){
				if (item == null){
					x++;
					continue;
				}
				if (item.getType() == Material.WRITTEN_BOOK || item.getType() == Material.BOOK_AND_QUILL){
					BookMeta book = (BookMeta) item.getItemMeta();
					books.put(x, new SerializeBook(book));
				}
				if (item.getType() == Material.ENCHANTED_BOOK){
					EnchantmentStorageMeta esm = (EnchantmentStorageMeta) item.getItemMeta();
					enchantedbook.put(x, new SerializedBooksimplements(esm));
				}
				amount.put(x, item.getAmount());
				enchants.put(x, new StoredEnchantments(contents[x]));
				material.put(x, item.getType().toString());
				durability.put(x, item.getDurability());
				lore.put(x, new SerializeLore(item));
				x++;
			}
		}
		
		public ItemStack[] unPackInv(){
			ItemStack[] items = new ItemStack[size];
			for(int x = 0; x < size; x++){
				if (!material.containsKey(x)){
					continue;
				}
				items[x] = new ItemStack(Material.getMaterial(material.get(x)), amount.get(x));
				if (books.containsKey(x)){
					items[x] = books.get(x).unPack(items[x]);
				}
				if (enchantedbook.containsKey(x)){
					EnchantmentStorageMeta esm = (EnchantmentStorageMeta) items[x].getItemMeta();
					SerializedBooksimplements sbs = enchantedbook.get(x);
					Map<String, Integer> data = sbs.getData();
					for (String z: data.keySet()){
						esm.addStoredEnchant(Enchantment.getByName(z), data.get(z), true);
					}
					items[x].setItemMeta(esm);
				}
				items[x].setDurability(durability.get(x));
				Map<String, Integer> en = enchants.get(x).getEnchantments();
				items[x] = getEnchant(items[x], en);
				items[x] = lore.get(x).getLore(items[x]);
			}
			return items;
		}
		public ItemStack getEnchant(ItemStack item, Map<String, Integer> ench){
			for (String id: ench.keySet()){
				item.addEnchantment(Enchantment.getByName(id), ench.get(id));
			}
			return item;
		}
	}
	
	class SerializeBook implements Serializable{
		private String author;
		private String title;
		private String display;
		private List<String> pages;
		private List<String> lore;
		/**
		 * 
		 */
		private static final long serialVersionUID = 6931550901915595804L;

		public SerializeBook(BookMeta book){
			author = book.getAuthor();
			title = book.getTitle();
			display = book.getDisplayName();
			pages = book.getPages();
			lore = book.getLore();
		}
		public ItemStack unPack(ItemStack stack){
			BookMeta book = (BookMeta) stack.getItemMeta();
			book.setAuthor(author);
			book.setTitle(title);
			book.setDisplayName(display);
			book.setPages(pages);
			book.setLore(lore);
			stack.setItemMeta(book);
			return stack;
		}
	}
	
	class SerializeLore implements Serializable{
		List<String> lore = new ArrayList<String>();
		String display ="";
		/**
		 * 
		 */
		private static final long serialVersionUID = 4444354610614193435L;
		
		public SerializeLore (ItemStack stack){
			if (stack.getItemMeta() == null) return;
			if (stack.getItemMeta().getDisplayName() != null)
				display = stack.getItemMeta().getDisplayName();
			if (stack.getItemMeta().getLore() != null)
				lore.addAll(stack.getItemMeta().getLore());
		}
		
		public ItemStack getLore(ItemStack stack){
			ItemMeta meta = stack.getItemMeta();
			if (!display.equals(""))
				meta.setDisplayName(display);
			if (lore.size() > 0){
				meta.setLore(lore);
			}
			stack.setItemMeta(meta);
			return stack;
		}
	}
	
	class StoredEnchantments implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = -3862785762573072287L;
		private Map<String, Integer> enchantments = new HashMap<String, Integer>(); // enchant name, enchant level
		
		public StoredEnchantments(ItemStack stack){
			if (stack == null) return;
			Map<Enchantment, Integer> enchs = stack.getEnchantments();
			for (Enchantment ench: enchs.keySet()){
				enchantments.put(ench.getName(), enchs.get(ench));
			}
		}
		
		public Map<String, Integer> getEnchantments(){
			return enchantments;
		}
	}
	class SerializedBooksimplements implements Serializable{
		private Map<String, Integer> enchantments = new HashMap<String, Integer>(); // enchant name, enchant level
		/**
		 * 
		 */
		private static final long serialVersionUID = -7927364919320998830L;
		public SerializedBooksimplements(EnchantmentStorageMeta esm){
			Map<Enchantment, Integer> enc = esm.getStoredEnchants();
			for (Enchantment ench: enc.keySet()){
				enchantments.put(ench.getName(), enc.get(ench));
			}
		}
		
		public Map<String, Integer> getData(){
			return enchantments;
		}
	}
}
