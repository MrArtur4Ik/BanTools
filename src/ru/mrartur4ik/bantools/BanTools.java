package ru.mrartur4ik.bantools;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class BanTools extends JavaPlugin {
	private File bansFile = new File(getDataFolder() + File.separator + "bans.yml");
	private FileConfiguration bansConfig = YamlConfiguration.loadConfiguration(bansFile);
	public void onEnable(){
		File configFile = new File(getDataFolder() + File.separator + "config.yml");
		if(!configFile.exists()) {
			getConfig().options().copyDefaults(true);
			getLogger().info("Created configuration file.");
			saveConfig();
		}
		if(!bansFile.exists()){
			try {
				bansFile.createNewFile();
				saveResource("bans.yml", true);
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
		Commands commands = new Commands();
		getCommand("ban").setExecutor(commands);
		getCommand("ban-ip").setExecutor(commands);
		getCommand("unban").setExecutor(commands);
		getCommand("unban-ip").setExecutor(commands);
		getCommand("bantools").setExecutor(new BanToolsCommand());
		getServer().getPluginManager().registerEvents(new EventListener(), this);
	}
	public void onDisable(){

	}
	public FileConfiguration getBansConfig(){
		return bansConfig;
	}
	public void saveBansConfig(){
		try {
			bansConfig.save(bansFile);
		}catch(IOException exception){
			exception.printStackTrace();
		}
	}
	public void reloadBansConfig(){
		bansConfig = YamlConfiguration.loadConfiguration(bansFile);
	}
}
