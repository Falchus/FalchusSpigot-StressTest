package com.falchus.spigot.stresstest.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import com.falchus.spigot.stresstest.Main;

public class DamageListener implements Listener {

    private static final Main plugin = Main.getInstance();
    
    public DamageListener() {
		Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
		if (!(event.getEntity() instanceof Player player)) return;
		String name = player.getName();
		if (name.contains("rowin")) {
			event.setCancelled(true);	
		}
    }
}