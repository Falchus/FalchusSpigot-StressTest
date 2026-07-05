package com.falchus.spigot.stresstest.listeners;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.falchus.lib.minecraft.spigot.player.elements.PlayerElement;
import com.falchus.lib.minecraft.spigot.player.elements.impl.Actionbar;
import com.falchus.lib.minecraft.spigot.player.elements.impl.Scoreboard;
import com.falchus.lib.minecraft.spigot.utils.PlayerUtils;
import com.falchus.lib.minecraft.spigot.utils.builder.ItemBuilder;
import com.falchus.spigot.stresstest.Main;

public class JoinQuitListener implements Listener {

    private static final Main plugin = Main.getInstance();
    
    public JoinQuitListener() {
		Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String name = player.getName();
    	
		if (player.isOp()) {
			PlayerElement.get(Actionbar.class, player).sendUpdating(1000,
				() -> {
					int chunks = 0;
					int entities = 0;
					for (World world : Bukkit.getWorlds()) {
						chunks += world.getLoadedChunks().length;
						entities += world.getEntities().size();
					}
					return "§7TPS: §f" + plugin.getTPSColored() + " §8× §7Players: §f" + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + " §8× §7Chunks: §f" + chunks + " §8× §7Entities: §f" + entities;
				}
			);
			PlayerElement.get(Scoreboard.class, player).sendUpdating(1000,
				() -> Main.colorcode + Main.website + "/spigot",
				() -> {
					int averagePing = 0;
					int total = 0;
					for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
						if (onlinePlayer.isOp()) continue;
						averagePing += PlayerUtils.getPing(onlinePlayer);
						total++;
					}
					
					Date date = new Date();
					
					List<String> lines = new ArrayList<>();
					lines.add("");
					lines.add("§6» §7Time elapsed");
					lines.add(" §8┃ §6" + plugin.getTimeElapsed());
					lines.add("");
					lines.add("§a» §7Date");
					lines.add(" §8┃ §a" + plugin.getDateFormat().format(date));
					lines.add("");
					lines.add("§e» §7Time");
					lines.add(" §8┃ §e" + plugin.getTimeFormat().format(date));
					lines.add("");
					lines.add("§b» §7Average bot ping");
					lines.add(" §8┃ §b" + (total > 0 ? (averagePing / total) : 0) + "ms");
					lines.add("");
					return lines;
				}
			);
			
			player.getInventory().addItem(new ItemBuilder(Material.BLAZE_ROD).withInteractListener(p -> {
				p.performCommand("tps");
			}).build());
			player.getInventory().addItem(new ItemBuilder(Material.STICK).withInteractListener(p -> {
				if (Bukkit.getName().equals("FalchusSpigot")) {
					p.performCommand("falchusspigot:tps");	
				} else {
					p.performCommand("spigot:tps");
				}
			}).build());
		}
		
		if (name.equals("rowin_0")) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spark profiler --thread *");
			plugin.setProfiling(true);
			plugin.setStartTime(System.currentTimeMillis());
		}
		event.setJoinMessage(null);
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
    	event.setQuitMessage(null);
    }
}