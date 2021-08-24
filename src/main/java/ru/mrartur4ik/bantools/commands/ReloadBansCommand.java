package ru.mrartur4ik.bantools.commands;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ru.mrartur4ik.bantools.BanTools;

import java.util.Collections;

public class ReloadBansCommand extends SimpleCommand {

    private final BanTools plugin = BanTools.getInstance();

    public ReloadBansCommand() {
        super("reload-bans", "Перезагрузка списка банов", "/reload-bans", Collections.singletonList("rl-bans"), "bantools.reload-bans");
    }

    @Override
    public boolean exec(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        plugin.getBansConfig().reloadConfig();
        plugin.getConfig().reloadConfig();
        sender.sendMessage(Component.text("§eСписок банов перезагружен"));
        return true;
    }
}
