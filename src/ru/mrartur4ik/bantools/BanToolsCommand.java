package ru.mrartur4ik.bantools;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class BanToolsCommand implements CommandExecutor {
	private final BanTools plugin = (BanTools) JavaPlugin.getPlugin(BanTools.class);
	public void sendHelp(CommandSender sender){
		sender.sendMessage("§eКоманды BanTools:");
		sender.sendMessage("§f/bantools reload§f - Перезагрузка конфига.");
	}
	@Override
	public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
		if(args.length > 0){
			String subcmd = args[0];
			if(subcmd.equals("reload")){
				plugin.reloadConfig();
				plugin.reloadBansConfig();
				sender.sendMessage("§aКонфиг перезагружен!");
			}else{
				sendHelp(sender);
			}
		}else{
			sendHelp(sender);
		}
		return true;
	}
}
