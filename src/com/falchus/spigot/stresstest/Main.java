package com.falchus.spigot.stresstest;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.falchus.lib.minecraft.spigot.FalchusLibMinecraftSpigot;
import com.falchus.lib.minecraft.spigot.task.SpigotTask;
import com.falchus.lib.minecraft.spigot.utils.ServerUtils;
import com.falchus.spigot.stresstest.listeners.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Main extends JavaPlugin {

	@Getter static Main instance;
	FalchusLibMinecraftSpigot lib;
	
	final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
	final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss aa");
	
	int previousOnline;
	int i;
	@Setter boolean profiling;
	@Setter long startTime;
	
	DamageListener damageListener;
	JoinQuitListener joinQuitListener;
	
	public static final String server = "Falchus";
	public static final String serverLowerCase = server.toLowerCase();
	public static final String serverFull = server + ".com";
	public static final String colorcode = "§f§l";
	public static final String website = serverFull.toLowerCase();
	
	@Override
	public void onEnable() {
		instance = this;
		lib = FalchusLibMinecraftSpigot.getInstance();

		Bukkit.getScheduler().runTask(this, () -> {
			damageListener = new DamageListener();
			joinQuitListener = new JoinQuitListener();
			
			SpigotTask.runTimer(() -> {
				if (profiling) {
					if (i % 8 == 0) {
						int online = Bukkit.getOnlinePlayers().size();
						if (previousOnline == online) {
							Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spark profiler --stop");
							profiling = false;
						}
						previousOnline = online;
					}
					i++;
				}
			}, 1, TimeUnit.SECONDS);
		});
	}
	
	public String getTimeElapsed() {
		if (startTime == 0) {
			return getRemaining(0, false);
		}
		return getRemaining(System.currentTimeMillis() - startTime, false);
	}
	
	public String getRemaining(long duration, boolean ms) {
		if (ms && duration < TimeUnit.MINUTES.toMillis(1)) {
			return ThreadLocal.withInitial(() -> new DecimalFormat("0.0")).get().format(duration * 0.001) + 's';
		} else {
			return (duration <= 0 ? "00:00" : DurationFormatUtils.formatDuration(duration, (duration >= TimeUnit.HOURS.toMillis(1) ? "HH:" : "") + "mm:ss"));
		}
	}
	
	public String getTPSColored() {
        double tps = ServerUtils.getRecentTps()[0];
        String color = (tps > 18 ? "§a" : tps > 16 ? "§e" : "§c");
        String asterisk = (tps > 20 ? "*" : "");
        return color + asterisk + Math.min(Math.round(tps * 100.0) / 100.0, 20.0);
	}
}
