package com.chasechocolate.earthbender.utils;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.chasechocolate.earthbender.EarthBender;

public class BendingUtils {
	public static HashMap<String, Boolean> carryingBlock = new HashMap<String, Boolean>();
	public static HashMap<String, ItemStack> playerBlocks = new HashMap<String, ItemStack>();
	public static HashMap<String, Double> chargeUps = new HashMap<String, Double>();
	
	public static void pickupBlock(final Player player, Block block){
		ItemStack item = new ItemStack(block.getType(), 1, (short) block.getData());
		ItemMeta meta = item.getItemMeta();
		
		meta.setDisplayName(ChatColor.RED + "Throwable " + StringUtils.capitalize(item.getType().toString().toLowerCase().replaceAll("_", " ")));
		item.setItemMeta(meta);
		player.setItemInHand(item);
		block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());
		block.setType(Material.AIR);
		carryingBlock.put(player.getName(), true);
		playerBlocks.put(player.getName(), item);
		chargeUps.put(player.getName(), 0.0D);
		
		new BukkitRunnable(){
			@Override
			public void run(){
				if(carryingBlock.containsKey(player.getName()) && carryingBlock.get(player.getName())){
					player.removePotionEffect(PotionEffectType.SLOW);
					player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 1));
				} else {
					this.cancel();
				}
			}
		}.runTaskTimer(EarthBender.getInstance(), 0L, 40L);
		
		new BukkitRunnable(){
			@Override
			public void run(){
				if(carryingBlock.containsKey(player.getName()) && carryingBlock.get(player.getName())){
					double chargeUp = chargeUps.get(player.getName());
					
					if(chargeUp >= 0.9999999D){ //Sometimes the numbers get to 0.9999999999 
						player.sendMessage(ChatColor.GREEN + "Fully charged up!");
						this.cancel();
					} else {
						player.sendMessage(ChatColor.GREEN + "" + Math.round((chargeUp * 100)) + "% charged up.");
						chargeUps.put(player.getName(), chargeUp + 0.10D);
					}
				} else {
					this.cancel();
				}
			}
		}.runTaskTimer(EarthBender.getInstance(), 0L, 20L);
	}
	
	public static void throwBlock(final Player player){
		if(carryingBlock.containsKey(player.getName()) && carryingBlock.get(player.getName())){
			final ItemStack item = playerBlocks.get(player.getName());
			final Material type = item.getType();
			final Location loc = player.getLocation();
			Vector velocity = loc.getDirection().multiply((chargeUps.get(player.getName()) == 0.0D ? 1 : 1 + chargeUps.get(player.getName())));
			final FallingBlock fb = loc.getWorld().spawnFallingBlock(loc.add(0.0D, 1.0D, 0.0D), type, item.getData().getData());
			
			fb.setDropItem(true);
			fb.setVelocity(velocity);
			player.setItemInHand(null);
			player.removePotionEffect(PotionEffectType.SLOW);
			carryingBlock.put(player.getName(), false);
			playerBlocks.remove(player.getName());
			
			new BukkitRunnable(){
				@Override
				public void run(){
					if(!(fb.isOnGround())){
						for(Entity nearby : fb.getNearbyEntities(1.25D, 1.25D, 1.25D)){
							if(nearby instanceof Player){
								Player nearbyPlayer = (Player) nearby;
								double distance = nearbyPlayer.getLocation().distance(loc);
								double damage = 0.0D;
								
								if(type == Material.DIRT){
									if(distance <= 2.0D){
										damage = 1.0D;
									} else {
										damage = 2.0D;
									}
								} else if(type == Material.STONE){
									if(distance <= 2.0D){
										damage = 2.0D;
									} else if(distance <= 5.0D){
										damage = 1.0D;
									} else {
										damage = 6.0D;
									}
								}
								
								ItemStack newItem = new ItemStack(item.getType(), item.getAmount(), (short) item.getData().getData());
								
								if(!(nearbyPlayer.equals(player))){
									nearbyPlayer.damage(damage);
								}
								
								fb.remove();
								nearbyPlayer.getWorld().dropItemNaturally(nearbyPlayer.getLocation(), newItem);
								chargeUps.remove(player.getName());
								this.cancel();
							}
						}
					} else {
						this.cancel();
					}
				}
			}.runTaskTimer(EarthBender.getInstance(), 4L, 1L);
		}
	}
}