package ru.mrartur4ik.bantools.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ru.mrartur4ik.bantools.BanTools;

import java.util.Collections;
import java.util.List;

public class ReloadBansCommand extends Command {

    private final BanTools plugin = BanTools.getInstance();

    public ReloadBansCommand() {
        super("reload-bans", "Перезагрузка списка банов", "/reload-bans", Collections.singletonList("rl-bans"));
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        plugin.getBansConfig().reloadConfig();
        plugin.getConfig().reloadConfig();
        sender.sendMessage(Component.text("§eСписок банов перезагружен"));
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        return Collections.emptyList();
    }
}
