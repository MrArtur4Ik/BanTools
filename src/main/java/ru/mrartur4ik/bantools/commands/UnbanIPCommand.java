package ru.mrartur4ik.bantools.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
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

public class UnbanIPCommand extends Command {

    private static final BanTools plugin = BanTools.getInstance();
    private final BansConfiguration bansConfig = plugin.getBansConfig();
    private final Configuration config = plugin.getConfig();

    public UnbanIPCommand() {
        super("unban-ip", "Разбанить ip игрока", "/ban-ip <никнейм/ip> [причина]", Collections.emptyList());
        setPermission("bantools.unban-ip");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
        if(args.length > 0) {
            String ip = args[0].replace('.', '-');
            if(bansConfig.getIPBans().containsKey(ip)) {
                UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
                if(sender instanceof Player) {
                    uuid = ((Player) sender).getUniqueId();
                }
                Ban ban = bansConfig.getIPBans().get(ip);
                ban.setFrom(uuid);

                if(Pattern.matches("([0-9]{1,3}[.]){3}[0-9]{1,3}", ip)) {
                    bansConfig.unbanIP(ip);
                    bansConfig.saveConfig();

                    Bukkit.broadcast(plugin.unbanMessage(ip, ban));
                } else {
                    sender.sendMessage(config.getColorizedString("info.incorrect-ip"));
                }
            } else {
                sender.sendMessage(config.getColorizedString("info.player-has-not-banned"));
            }
            return true;
        }
        return false;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if(args.length == 1) {
            List<String> list = new ArrayList<>();
            for(String s : bansConfig.getIPBans().keySet()) {
                list.add(s.replace('-', '.'));
            }
            return list;
        }
        return Collections.emptyList();
    }
}
