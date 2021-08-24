package ru.mrartur4ik.bantools.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;
import org.jetbrains.annotations.NotNull;
import ru.mrartur4ik.bantools.BanTools;
import ru.mrartur4ik.bantools.Utils;
import ru.mrartur4ik.bantools.config.Ban;
import ru.mrartur4ik.bantools.config.BansConfiguration;
import ru.mrartur4ik.bantools.config.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class TempBanIPCommand extends SimpleCommand {

    private static final BanTools plugin = BanTools.getInstance();
    private final BansConfiguration bansConfig = plugin.getBansConfig();
    private final Configuration config = plugin.getConfig();

    public TempBanIPCommand() {
        super("tempban-ip", "Забанить игрока по IP временно", "/tempban-ip <никнейм/ip> <время> [причина]", "bantools.tempban-ip");
    }

    @Override
    public boolean exec(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        if(args.length > 1) {
            String ip = args[0];
            if(!bansConfig.getIPBans().containsKey(ip)) {
                String reason = "";
                UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
                long time = System.currentTimeMillis();
                try {
                    time = time + Utils.parseTime(args[1]);
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(config.getColorizedString("info.incorrect-time"));
                    return true;
                }
                if(args.length > 2) {
                    StringBuilder str = new StringBuilder();
                    for(int i = 2; i < args.length; i++) {
                        str.append(args[i]).append(i < args.length - 1 ? "" : " ");
                    }
                    reason = ChatColor.translateAlternateColorCodes('&', str.toString());
                }
                if(sender instanceof Player) {
                    uuid = ((Player) sender).getUniqueId();
                }
                Ban ban = new Ban(reason, uuid, time);

                if(Pattern.matches("([0-9]{1,3}[.]){3}[0-9]{1,3}", ip)) {
                    bansConfig.banIP(ip.replace('.', '-'), ban);

                    Bukkit.broadcast(plugin.broadcastMessage(ip.replace('.', '-'), ban));

                    for(Player p : Utils.getOnlinePlayersByAddress(ip)) {
                        p.kick(plugin.kickMessage(ban, true), PlayerKickEvent.Cause.BANNED);
                    }
                } else {
                    OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(ip);

                    if(target == null) {
                        sender.sendMessage(config.getColorizedString("info.player-has-not-played-before"));
                        return true;
                    }

                    for(String s : bansConfig.getIPs(target.getUniqueId())) {
                        if(bansConfig.getIPBans().containsKey(s)) {
                            sender.sendMessage(config.getColorizedString("info.player-already-in-ban"));
                            break;
                        }
                        bansConfig.banIP(s, ban);
                        Bukkit.broadcast(plugin.broadcastMessage(s, ban));
                    }

                    if(target.isOnline()) {
                        Player p = (Player) target;
                        String address = p.getAddress().getAddress().getHostAddress().replace('.', '-');
                        if(!bansConfig.getIPBans().containsKey(address)){
                            bansConfig.banIP(address, ban);

                            Bukkit.broadcast(plugin.broadcastMessage(address, ban));
                        }

                        p.kick(plugin.kickMessage(ban, true), PlayerKickEvent.Cause.BANNED);
                    }
                }
                bansConfig.saveConfig();
            } else {
                sender.sendMessage(config.getColorizedString("info.already-in-ban"));
            }
            return true;
        }
        return false;
    }

    @Override
    public @NotNull List<String> complete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if(args.length == 1) {
            List<String> list = new ArrayList<>();
            for(@NotNull OfflinePlayer p : Bukkit.getOfflinePlayers()) {
                for(String ip : bansConfig.getIPs(p.getUniqueId())) {
                    if(!bansConfig.getIPBans().containsKey(ip)) {
                        list.add(p.getName());
                        break;
                    }
                }
            }
            return list;
        }
        return Collections.emptyList();
    }
}
