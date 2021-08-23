package ru.mrartur4ik.bantools;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTimeUtils;
import ru.mrartur4ik.bantools.commands.*;
import ru.mrartur4ik.bantools.config.Ban;
import ru.mrartur4ik.bantools.config.BansConfiguration;
import ru.mrartur4ik.bantools.config.Configuration;

import java.text.SimpleDateFormat;
import java.util.*;

public class BanTools extends JavaPlugin implements Listener, Runnable {

    private Configuration config;
    private BansConfiguration bansConfig;

    private static BanTools instance;

    public SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", new Locale("ru"));

    @Override
    public void onEnable() {
        instance = this;

        ConfigurationSerialization.registerClass(Ban.class);

        this.config = new Configuration();
        this.bansConfig = new BansConfiguration();

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, this, 0, 20);

        CommandMap commandMap = Bukkit.getCommandMap();

        List<Command> cmdList = new ArrayList<>();
        cmdList.add(new BanCommand());
        cmdList.add(new BanIPCommand());
        cmdList.add(new ReloadBansCommand());
        cmdList.add(new UnbanCommand());
        cmdList.add(new UnbanIPCommand());
        cmdList.add(new TempBanCommand());
        cmdList.add(new TempBanIPCommand());

        commandMap.registerAll(getName(), cmdList);

        getServer().getPluginManager().registerEvents(this, this);
    }

    public static BanTools getInstance() {
        return instance;
    }

    public BansConfiguration getBansConfig() {
        return bansConfig;
    }

    @Override
    public @NotNull Configuration getConfig() {
        return config;
    }

    public TextComponent kickMessage(Ban ban, boolean ipban) {
        String name = Bukkit.getOfflinePlayer(ban.getFrom()).getName();

        String message = ipban ? config.getColorizedString("kick.ip-kick-message")
                : config.getColorizedString("kick.kick-message");

        StringBuilder str = new StringBuilder(Objects.requireNonNull(message.replace("%player%", name != null ? name : "Console")));
        if (!ban.getReason().equals("")) {
            str.append("\n");
            str.append(config.getColorizedString("kick.reason").replace("%reason%", ban.getReason()));
        }
        if (ban.getTime() != -1) {
            str.append("\n");
            str.append(config.getColorizedString("kick.expire").replace("%date%", dateFormat.format(new Date(ban.getTime()))));
        }

        return Component.text(str.toString());
    }

    public Component broadcastMessage(UUID uuid, Ban ban) {
        OfflinePlayer banned = Bukkit.getOfflinePlayer(uuid);
        String bannedname = banned.getName();
        if(banned.isOnline()) {
            bannedname = LegacyComponentSerializer.legacySection().serializeOrNull(((Player) banned).displayName());
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(ban.getFrom());
        String name = "Console";
        if(player.getName() != null) {
            name = player.getName();
            if(player.isOnline()) {
                name = LegacyComponentSerializer.legacySection().serializeOrNull(((Player) player).displayName());
            }
        }

        StringBuilder str = new StringBuilder(config.getColorizedString("broadcast.ban-message").replace("%banned%", Objects.requireNonNull(bannedname)).replace("%player%", name));

        if (!ban.getReason().equals("")) {
            str.append(config.getColorizedString("broadcast.reason").replace("%reason%", ban.getReason()));
        }
        if (ban.getTime() != -1) {
            str.append(config.getColorizedString("broadcast.expire").replace("%date%", BanTools.getInstance().dateFormat.format(new Date(ban.getTime()))));
        }

        return Component.text(str.toString());
    }

    public Component broadcastMessage(String address, Ban ban) {
        String banned;
        List<OfflinePlayer> list = bansConfig.getPlayersByAddress(address);
        if(!list.isEmpty()) {
            StringBuilder bannedpls = new StringBuilder();
            for(int i = 0; i < list.size(); i++) {
                OfflinePlayer bannedpl = list.get(i);
                String name = bannedpl.getName();
                if(bannedpl.isOnline()) {
                    name = LegacyComponentSerializer.legacySection().serializeOrNull(((Player) bannedpl).displayName());
                }
                bannedpls.append(name).append(i < list.size() - 1 ? "": ", ");
            }
            banned = bannedpls.toString();
        } else {
            banned = address.replace('-', '.');
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(ban.getFrom());
        String name = "Console";
        if(player.getName() != null) {
            name = player.getName();
            if(player.isOnline()) {
                name = LegacyComponentSerializer.legacySection().serializeOrNull(((Player) player).displayName());
            }
        }

        StringBuilder str = new StringBuilder(config.getColorizedString("broadcast.ban-ip-message").replace("%banned%", banned).replace("%player%", name));

        if (!ban.getReason().equals("")) {
            str.append(config.getColorizedString("broadcast.reason").replace("%reason%", ban.getReason()));
        }
        if (ban.getTime() != -1) {
            str.append(config.getColorizedString("broadcast.expire").replace("%date%", BanTools.getInstance().dateFormat.format(new Date(ban.getTime()))));
        }

        return Component.text(str.toString());
    }

    public Component unbanMessage(UUID uuid, Ban ban) {
        OfflinePlayer unbanned = Bukkit.getOfflinePlayer(uuid);
        String unbannedname = unbanned.getName();
        if(unbanned.isOnline()) {
            unbannedname = LegacyComponentSerializer.legacySection().serializeOrNull(((Player) unbanned).displayName());
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(ban.getFrom());
        String name = "Console";
        if(player.getName() != null) {
            name = player.getName();
            if(player.isOnline()) {
                name = LegacyComponentSerializer.legacySection().serializeOrNull(((Player) player).displayName());
            }
        }

        return Component.text(config.getColorizedString("broadcast.unban-message").replace("%unbanned%", Objects.requireNonNull(unbannedname)).replace("%player%", name));
    }

    public Component unbanMessage(String address, Ban ban) {
        String banned;
        List<OfflinePlayer> list = bansConfig.getPlayersByAddress(address);
        if(!list.isEmpty()) {
            StringBuilder bannedpls = new StringBuilder();
            for(int i = 0; i < list.size(); i++) {
                OfflinePlayer bannedpl = list.get(i);
                String name = bannedpl.getName();
                if(bannedpl.isOnline()) {
                    name = LegacyComponentSerializer.legacySection().serializeOrNull(((Player) bannedpl).displayName());
                }
                bannedpls.append(name).append(i < list.size() - 1 ? "": ", ");
            }
            banned = bannedpls.toString();
        } else {
            banned = address.replace('-', '.');
        }

        OfflinePlayer player = Bukkit.getOfflinePlayer(ban.getFrom());
        String name = "Console";
        if(player.getName() != null) {
            name = player.getName();
            if(player.isOnline()) {
                name = LegacyComponentSerializer.legacySection().serializeOrNull(((Player) player).displayName());
            }
        }

        return Component.text(config.getColorizedString("broadcast.unban-message").replace("%banned%", banned).replace("%player%", name));
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Map<String, Ban> ipbans = bansConfig.getIPBans();
        Map<UUID, Ban> bans = bansConfig.getBans();

        UUID uuid = event.getPlayer().getUniqueId();
        String ip = event.getAddress().getHostAddress().replace(".", "-");

        bansConfig.addIP(uuid, event.getAddress());
        bansConfig.saveConfig();

        if (ipbans.containsKey(ip)) {
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, kickMessage(ipbans.get(ip), true));
            return;
        }

        if(bans.containsKey(uuid)) {
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, kickMessage(bans.get(uuid), false));
        }
    }

    @Override
    public void run() {
        Map<UUID, Ban> bans = bansConfig.getBans();
        for(Ban ban : bans.values()) {
            if(ban.getTime() <= System.currentTimeMillis()) {
                bansConfig.set("bans." + Utils.getByValue(bans, ban), null);
                bansConfig.saveConfig();
            }
        }

        Map<String, Ban> ipbans = bansConfig.getIPBans();
        for(Ban ban : ipbans.values()) {
            if(ban.getTime() <= System.currentTimeMillis()) {
                bansConfig.set("ipbans." + Utils.getByValue(ipbans, ban), null);
                bansConfig.saveConfig();
            }
        }
    }
}