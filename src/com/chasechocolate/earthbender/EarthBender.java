package com.chasechocolate.earthbender;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.chasechocolate.earthbender.listeners.PlayerInteractListener;

public class EarthBender extends JavaPlugin {
	private static EarthBender instance;
	
	public void log(String msg){
		this.getLogger().info(msg);
	}
	
	@Override
	public void onEnable(){
		instance = this;
		
		PluginManager pm = this.getServer().getPluginManager();
		
		pm.registerEvents(new PlayerInteractListener(), this);
		
		log("Enabled!");
	}
	
	@Override
	public void onDisable(){
		log("Disabled!");
	}
	
	public static EarthBender getInstance(){
		return instance;
	}
}