package com.untamedears.EnderExpansion.DataBase;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;

import com.untamedears.EnderExpansion.Enderplugin;
import com.untamedears.EnderExpansion.SerializationClass;

public class InventoryManager {

	/*
	 * These values are for database connection
	 */
	private final String host;
    private final String dbname;
    private final String username;
    private final int port;
    private final String password;
    private DataBase db;
    
	public InventoryManager(FileConfiguration config_, Enderplugin plugin){
		host = config_.getString("sql.hostname");
		port = config_.getInt("sql.port");
		dbname = config_.getString("sql.dbname");
		username = config_.getString("sql.username");
		password = config_.getString("sql.password");
		db = new DataBase(host, port, dbname, username, password, plugin.getLogger());
		boolean connected = db.connect();
		if (connected){
			createTables();
			initializeStatements();
		}
	}
	
	/*
     * These methods are for inventory management
     */
	
	private PreparedStatement createChest;
	private PreparedStatement readChest;
	private PreparedStatement deleteChest;
	private PreparedStatement updateChest;
	
	public void initializeStatements(){
		createChest = db.prepareStatement("insert into inventory (world, x, y, z, obj_name, object_value)"
				+ "values (?,?,?,?,?,?)");
		readChest = db.prepareStatement("select object_value from inventory where world = ? and x = ? and y = ? and z = ?");
		deleteChest= db.prepareStatement("delete from inventory where world = ? and x = ? and y = ? and z = ?");
		updateChest = db.prepareStatement("update inventory set object_value = ? where "
				+ "world = ? and x = ? and y = ? and z = ?");
	}
	
	public void createTables(){
		db.execute("create table if not exists inventory("
				+ "world varchar(40) not null,"
				+ "x int not null,"
				+ "y int not null,"
				+ "z int not null,"
				+ "obj_name varchar(128),"
				+ "object_value BLOB,"
				+ "unique key xyz(world, x, y, z));");
	}
	
	public void reinitializeDatabase(){
		db.connect();
		initializeStatements();
	}
	
	public void createInventory(Location loc, Object inv) throws SQLException{
		createChest.setString(1, loc.getWorld().getName());
		createChest.setInt(2, loc.getBlockX());
		createChest.setInt(3, loc.getBlockY());
		createChest.setInt(4, loc.getBlockZ());
		createChest.setObject(5, inv.getClass().getName());
		createChest.setObject(6, inv);
		createChest.execute();
	}
	
	public void updateInventory(Location loc, Inventory inv) throws SQLException{
		SerializationClass obj = new SerializationClass();
		obj.packInventory(inv);
		updateChest.setObject(1, obj);
		updateChest.setString(2, loc.getWorld().getName());
		updateChest.setInt(3, loc.getBlockX());
		updateChest.setInt(4, loc.getBlockY());
		updateChest.setInt(5, loc.getBlockZ());
		updateChest.execute();
	}
	
	public void deleteInventory(Location loc) throws SQLException{
		deleteChest.setString(1, loc.getWorld().getName());
		deleteChest.setInt(2, loc.getBlockX());
		deleteChest.setInt(3, loc.getBlockY());
		deleteChest.setInt(4, loc.getBlockZ());
		deleteChest.execute();
	}
	
	public Inventory getInventory(Location loc) throws SQLException, IOException, ClassNotFoundException{
		readChest.setString(1, loc.getWorld().getName());
		readChest.setInt(2, loc.getBlockX());
		readChest.setInt(3, loc.getBlockY());
		readChest.setInt(4, loc.getBlockZ());
		ResultSet set = readChest.executeQuery();
		Inventory inv = Bukkit.createInventory(null, 27);
		SerializationClass obj = new SerializationClass();
		obj.packInventory(inv);
		if (!set.next()){
			createInventory(loc, obj);
			return inv;
		}
		byte[] buf = set.getBytes("object_value");
        ObjectInputStream objectIn = null;
        objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));
        Object object = objectIn.readObject();
        SerializationClass newObj = (SerializationClass) object;
        newObj.updateInventory(inv);
        return inv;
	}
}
