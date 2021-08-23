package ru.mrartur4ik.bantools.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;
import org.jetbrains.annotations.NotNull;
import ru.mrartur4ik.bantools.BanTools;
import ru.mrartur4ik.bantools.config.Ban;
import ru.mrartur4ik.bantools.config.BansConfiguration;
import ru.mrartur4ik.bantools.config.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class BanCommand extends SimpleCommand {

    private static final BanTools plugin = BanTools.getInstance();

    private final BansConfiguration bansConfig = plugin.getBansConfig();
    private final Configuration config = plugin.getConfig();

    public BanCommand() {
        super("ban", "Забанить игрока навсегда", "/ban <никнейм> [причина]", "bantools.ban");
    }

    @Override
    public boolean exec(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        if(args.length > 0) {
            String nickname = args[0];
            OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(nickname);
            if(target == null) {
                sender.sendMessage(config.getColorizedString("info.player-has-not-played-before"));
                return true;
            }
            if(!bansConfig.getBans().containsKey(target.getUniqueId())) {
                String reason = "";
                UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
                if(args.length > 1) {
                    StringBuilder str = new StringBuilder();
                    for(int i = 1; i < args.length; i++) {
                        str.append(args[i]).append(i < args.length - 1 ? "" : " ");
                    }
                    reason = ChatColor.translateAlternateColorCodes('&', str.toString());
                }
                if(sender instanceof Player) {
                    uuid = ((Player) sender).getUniqueId();
                }
                Ban ban = new Ban(reason, uuid);
                bansConfig.ban(target.getUniqueId(), ban);
                bansConfig.saveConfig();

                Bukkit.broadcast(plugin.broadcastMessage(target.getUniqueId(), ban));

                if(target.isOnline()) {
                    ((Player) target).kick(plugin.kickMessage(ban, false), PlayerKickEvent.Cause.BANNED);
                }
            } else {
                sender.sendMessage(config.getColorizedString("info.player-already-in-ban"));
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
                if(!bansConfig.getBans().containsKey(p.getUniqueId())) {
                    list.add(p.getName());
                }
            }
            return list;
        }
        return Collections.emptyList();
    }
}
