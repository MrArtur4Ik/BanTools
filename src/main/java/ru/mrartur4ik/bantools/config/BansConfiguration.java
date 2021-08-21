package ru.mrartur4ik.bantools.config;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import ru.mrartur4ik.bantools.BanTools;

import java.io.File;
import java.net.InetAddress;
import java.util.*;

public class BansConfiguration extends SimpleConfiguration {

    private static final File file = new File(BanTools.getInstance().getDataFolder() + File.separator + "ban.yml");

    public BansConfiguration() {
        super(file);

        if(getInt("version") != 1) {
            BanTools.getInstance().getLogger().info("Converting config to UUIDs...");
            ConfigurationSection bans = getConfigurationSection("bans");
            if(bans != null) {
                Map<OfflinePlayer, Ban> map = new HashMap<>();

                for(String nick : bans.getKeys(false)) {
                    map.put(Bukkit.getOfflinePlayer(nick), bans.getSerializable(nick, Ban.class));
                }

                for(OfflinePlayer p : map.keySet()) {
                    bans.set(p.getUniqueId().toString(), map.get(p));
                }

                set("bans", bans);
            }

            ConfigurationSection ipbans = getConfigurationSection("ip-bans");
            if(ipbans != null) {
                for(String ip : Objects.requireNonNull(ipbans).getKeys(true)) {
                    ipbans.set(ip, ipbans.getSerializable(ip, Ban.class));
                }

                set("ip-bans", ipbans);
            }

            set("version", 1);
            saveConfig();
        }
    }

    @Override
    public void setDefaults() {
        set("version", 1);
    }

    public void addIP(UUID uuid, @NotNull InetAddress address) {
        List<String> list = getStringList("ips." + uuid);
        String addr = address.getHostAddress().replace('.', '-');
        if(!list.contains(addr)) {
            list.add(addr);
        }
        set("ips." + uuid, list);
    }

    public List<String> getIPs(UUID uuid) {
        return getStringList("ips." + uuid);
    }

    public void ban(@NotNull UUID uuid, Ban ban) {
        set("bans." + uuid, ban);
    }

    public void banIP(@NotNull String address, Ban ban) {
        set("ipbans." + address, ban);
    }

    public void unban(@NotNull UUID uuid) {
        set("bans." + uuid, null);
    }

    public void unbanIP(@NotNull String address) {
        set("ipbans." + address, null);
    }

    public Map<UUID, Ban> getBans() {
        Map<UUID, Ban> bans = new HashMap<>();
        ConfigurationSection section = getConfigurationSection("bans");
        if(section != null) {
            for(String s : section.getKeys(false)) {
                bans.put(UUID.fromString(s), section.getSerializable(s, Ban.class));
            }
        }
        return bans;
    }

    public Map<String, Ban> getIPBans() {
        Map<String, Ban> bans = new HashMap<>();
        ConfigurationSection section = getConfigurationSection("ip-bans");
        if(section != null) {
            for(String s : section.getKeys(false)) {
                bans.put(s, section.getSerializable(s, Ban.class));
            }
        }
        return bans;
    }

    public List<OfflinePlayer> getPlayersByAddress(String address) {
        List<OfflinePlayer> list = new ArrayList<>();
        ConfigurationSection section = getConfigurationSection("ips");
        if(section != null) {
            List<String> keys = new ArrayList<>(section.getKeys(false));
            for(String key : keys) {
                List<String> ips = section.getStringList(key);
                if(ips.contains(address)) {
                    list.add(Bukkit.getOfflinePlayer(UUID.fromString(key)));
                }
            }
        }
        return list;
    }
}
