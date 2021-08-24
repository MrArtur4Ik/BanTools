package ru.mrartur4ik.bantools.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.mrartur4ik.bantools.BanTools;
import ru.mrartur4ik.bantools.config.Ban;
import ru.mrartur4ik.bantools.config.BansConfiguration;
import ru.mrartur4ik.bantools.config.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class UnbanCommand extends SimpleCommand {

    private static final BanTools plugin = BanTools.getInstance();

    private final BansConfiguration bansConfig = plugin.getBansConfig();
    private final Configuration config = plugin.getConfig();

    public UnbanCommand() {
        super("unban", "Разбанить игрока", "/unban <никнейм>", Collections.singletonList("pardon"), "bantools.unban");
    }

    @Override
    public boolean exec(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if(args.length > 0) {
            String nickname = args[0];
            OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(nickname);
            if(target == null) {
                sender.sendMessage(config.getColorizedString("info.player-has-not-played-before"));
                return true;
            }
            if(bansConfig.getBans().containsKey(target.getUniqueId())) {
                UUID uuid = UUID.fromString("00000000-0000-0000-0000-000000000000");
                if(sender instanceof Player) {
                    uuid = ((Player) sender).getUniqueId();
                }
                Ban ban = bansConfig.getBans().get(target.getUniqueId());
                ban.setFrom(uuid);

                bansConfig.unban(target.getUniqueId());
                bansConfig.saveConfig();

                Bukkit.broadcast(plugin.unbanMessage(target.getUniqueId(), ban));
            } else {
                sender.sendMessage(config.getColorizedString("info.player-has-not-banned"));
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
                if(bansConfig.getBans().containsKey(p.getUniqueId())) {
                    list.add(p.getName());
                }
            }
            return list;
        }
        return Collections.emptyList();
    }
}
