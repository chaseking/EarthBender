package com.chasechocolate.earthbender.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.chasechocolate.earthbender.utils.BendingUtils;

public class PlayerInteractListener implements Listener {
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		Action action = event.getAction();
		ItemStack hand = player.getItemInHand();
		
		if(action == Action.LEFT_CLICK_BLOCK){
			Block block = event.getClickedBlock();
			
			if(block.getType() == Material.DIRT || block.getType() == Material.STONE){
				if(BendingUtils.carryingBlock.containsKey(player.getName()) && BendingUtils.carryingBlock.get(player.getName())){
					player.sendMessage(ChatColor.RED + "You are already carrying a block!");
				} else {
					if(hand.getType() == Material.AIR){
						BendingUtils.pickupBlock(player, block);
					}
				}
			}
			
		} else if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK){
			if(BendingUtils.carryingBlock.containsKey(player.getName()) && BendingUtils.carryingBlock.get(player.getName())){
				if(hand.equals(BendingUtils.playerBlocks.get(player.getName()))){
					if(action == Action.RIGHT_CLICK_BLOCK){
						event.setCancelled(true);
					}
					
					BendingUtils.throwBlock(player);
				}
			}
		}
	}
}