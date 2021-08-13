package ru.mrartur4ik.bantools;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;

public class Commands implements CommandExecutor {
	private final BanTools plugin = JavaPlugin.getPlugin(BanTools.class);
	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		FileConfiguration config = plugin.getConfig();
		FileConfiguration bansConfig = plugin.getBansConfig();
		if(command.getName().equalsIgnoreCase("ban")){
			if(args.length > 0){
				String nickname = args[0];
				Set<String> bans = Collections.emptySet();
				try {
					bans = bansConfig.getConfigurationSection("bans").getKeys(false);
				}catch(NullPointerException ignored){ }
				String nicknamePath = Utils.containsIgnoreCase(Utils.stringSetToList(bans), nickname);
				if(nicknamePath != null){
					sender.sendMessage(Utils.color(config.getString("messages.already-ban-message")));
				}else{
					StringBuilder stringBuilder = new StringBuilder();
					if(args.length > 1){
						for(int i = 1; i < args.length; i++){
							stringBuilder.append(i == 1 ? "" : " ").append(args[i]);
						}
					}
					String reason = stringBuilder.toString();
					bansConfig.set("bans." + nickname + ".time", -1);
					bansConfig.set("bans." + nickname + ".from", sender.getName());
					if(!reason.equals("")){
						bansConfig.set("bans." + nickname + ".reason", reason);
					}
					Player player = Bukkit.getPlayer(nickname);
					String displayName = nickname;
					String senderDisplayName = ((sender instanceof Player) ? ((Player) sender).getDisplayName() : sender.getName());
					String broadcastMessage = Utils.color(config.getString("messages.ban-message").replace("%player%", senderDisplayName).replace("%banPlayer%", displayName)
							+ (!reason.equals("") ? " " + config.getString("messages.reason") + "§f" + reason : ""));
					String kickMessage = Utils.color(config.getString("messages.ban-message").replace("%player%", senderDisplayName).replace("%banPlayer%", displayName)
							+ (!reason.equals("") ? " " + config.getString("messages.reason") + "\n§f" + reason : ""));
					if(player != null){
						displayName = player.getDisplayName();
						player.kickPlayer(kickMessage);
					}
					Bukkit.broadcastMessage(broadcastMessage);
					plugin.saveBansConfig();
				}
			}else{
				return false;
			}
		}
		if(command.getName().equalsIgnoreCase("unban")){
			if(args.length > 0){
				String nickname = args[0];
				Set<String> bans = Collections.emptySet();
				try {
					bans = bansConfig.getConfigurationSection("bans").getKeys(false);
				}catch(NullPointerException ignored){ }
				String nicknamePath = Utils.containsIgnoreCase(Utils.stringSetToList(bans), nickname);
				if(nicknamePath != null){
					Player player = Bukkit.getPlayer(nickname);
					String displayName = nickname;
					String senderDisplayName = ((sender instanceof Player) ? ((Player) sender).getDisplayName() : sender.getName());
					if(player != null){
						displayName = player.getDisplayName();
					}
					bansConfig.set("bans." + nickname, null);
					Bukkit.broadcastMessage(Utils.color(config.getString("messages.unban-message").replace("%banPlayer%", displayName).replace("%player%", senderDisplayName)));
					plugin.saveBansConfig();
				}else{
					sender.sendMessage(Utils.color(config.getString("messages.noban-message")));
				}
			}else{
				return false;
			}
		}
		if(command.getName().equalsIgnoreCase("ban-ip")){
			if(args.length > 0){
				String ip = args[0];
				String nickname = ip;
				boolean ipMatches = Pattern.matches("([0-9]{1,3}[\\.]){3}[0-9]{1,3}", ip);
				if(!ipMatches){
					Player player = Bukkit.getPlayer(ip);
					if(player == null){
						sender.sendMessage(Utils.color(config.getString("messages.noplayer-message")));
						return true;
					}else{
						ip = player.getAddress().getAddress().getHostAddress();
					}
				}
				Set<String> bans = Collections.emptySet();
				try {
					bans = bansConfig.getConfigurationSection("ip-bans").getKeys(false);
				}catch(NullPointerException ignored){ }
				String ipPath = ip.replace(".", "-");
				if(bans.contains(ipPath)){
					sender.sendMessage(Utils.color(config.getString("messages.already-ban-message")));
				}else{
					StringBuilder stringBuilder = new StringBuilder();
					if(args.length > 1){
						for(int i = 1; i < args.length; i++){
							stringBuilder.append(i == 1 ? "" : " ").append(args[i]);
						}
					}
					String reason = stringBuilder.toString();
					Player player = null;
					String displayName = ip;
					if(!ipMatches){
						player = Bukkit.getPlayer(nickname);
						displayName = player.getDisplayName();
					}
					bansConfig.set("ip-bans." + ipPath + ".time", -1);
					bansConfig.set("ip-bans." + ipPath + ".from", sender.getName());
					if(!reason.equals("")){
						bansConfig.set("ip-bans." + ipPath + ".reason", reason);
					}
					String senderDisplayName = ((sender instanceof Player) ? ((Player) sender).getDisplayName() : sender.getName());
					String broadcastMessage = Utils.color(config.getString("messages.ban-ip-message").replace("%player%", senderDisplayName).replace("%banPlayer%", displayName)
							+ (!reason.equals("") ? " " + config.getString("messages.reason") + "§f" + reason : ""));
					String kickMessage = Utils.color(config.getString("messages.ban-ip-message").replace("%player%", senderDisplayName).replace("%banPlayer%", displayName)
							+ (!reason.equals("") ? " " + config.getString("messages.reason") + "\n§f" + reason : ""));
					if(player != null){
						player.kickPlayer(kickMessage);
					}
					Bukkit.broadcastMessage(broadcastMessage);
					plugin.saveBansConfig();
				}
			}else{
				return false;
			}
		}
		if(command.getName().equalsIgnoreCase("unban-ip")){
			if(args.length > 0){
				String ip = args[0];
				boolean ipMatches = Pattern.matches("([0-9]{1,3}[\\.]){3}[0-9]{1,3}", ip);
				Set<String> bans = Collections.emptySet();
				try {
					bans = bansConfig.getConfigurationSection("ip-bans").getKeys(false);
				}catch(NullPointerException ignored){ }
				String displayName = ip;
				if(!ipMatches){
					Player player = Bukkit.getPlayer(ip);
					if(player != null){
						displayName = player.getDisplayName();
					}
					boolean nicknameHasIP = false;
					for(String ban : bans){
						try {
							String nick = bansConfig.getString("ip-bans." + ban + ".from");
							if(nick.equalsIgnoreCase(ip)){
								ip = ban;
								nicknameHasIP = true;
								break;
							}
						}catch(NullPointerException ignored){ }
					}
					if(!nicknameHasIP){
						sender.sendMessage(Utils.color(config.getString("messages.unban-ip-noplayer-message")));
						return true;
					}
				}
				String ipPath = ip.replace(".", "-");
				if(bans.contains(ipPath)){
					bansConfig.set("ip-bans." + ipPath, null);
					String senderDisplayName = ((sender instanceof Player) ? ((Player) sender).getDisplayName() : sender.getName());
					String broadcastMessage = Utils.color(config.getString("messages.unban-message").replace("%player%", senderDisplayName).replace("%banPlayer%", displayName));
					Bukkit.broadcastMessage(broadcastMessage);
					plugin.saveBansConfig();
				}else{
					sender.sendMessage(Utils.color(config.getString("messages.noban-message")));
				}
			}else{
				return false;
			}
		}
		return true;
	}
}
