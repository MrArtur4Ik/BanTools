package ru.mrartur4ik.bantools;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.Set;

public class EventListener implements Listener {
	private final BanTools plugin = (BanTools) JavaPlugin.getPlugin(BanTools.class);
	@EventHandler
	public void onLogin(PlayerLoginEvent event){
		FileConfiguration config = plugin.getConfig();
		FileConfiguration bansConfig = plugin.getBansConfig();
		Set<String> bans = Collections.emptySet();
		Set<String> ip_bans = Collections.emptySet();
		Player player = event.getPlayer();
		try {
			bans = bansConfig.getConfigurationSection("bans").getKeys(false);
		}catch(NullPointerException ignored){ }
		try {
			ip_bans = bansConfig.getConfigurationSection("ip-bans").getKeys(false);
		}catch(NullPointerException ignored){ }
		String playerIP = event.getAddress().getHostAddress().replace(".", "-");
		if(ip_bans.contains(playerIP)){
			boolean hasReason = bansConfig.contains("ip-bans." + playerIP + ".reason");
			event.disallow(PlayerLoginEvent.Result.KICK_BANNED, Utils.color(config.getString("messages.ip-join-kick-message")
					+ " " + (hasReason ? config.getString("messages.reason") + "\n" + bansConfig.getString("ip-bans." + playerIP + ".reason") : "")));
			return;
		}
		if(Utils.containsIgnoreCase(Utils.stringSetToList(bans), player.getName()) != null){
			boolean hasReason = bansConfig.contains("bans." + player.getName() + ".reason");
			event.disallow(PlayerLoginEvent.Result.KICK_BANNED, Utils.color(config.getString("messages.join-kick-message")
					+ " " + (hasReason ? config.getString("messages.reason") + "\n" + bansConfig.getString("bans." + player.getName() + ".reason") : "")));
		}
	}
}
