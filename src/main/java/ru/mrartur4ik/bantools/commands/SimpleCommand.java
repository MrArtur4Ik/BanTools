package ru.mrartur4ik.bantools.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public abstract class SimpleCommand extends Command {

    public SimpleCommand(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    public SimpleCommand(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull List<String> aliases, @NotNull String permission) {
        this(name, description, usageMessage, aliases);
        setPermission(permission);
    }

    public SimpleCommand(@NotNull String name, @NotNull String description, @NotNull String usageMessage) {
        this(name, description, usageMessage, Collections.emptyList());
    }

    public SimpleCommand(@NotNull String name, @NotNull String description, @NotNull String usageMessage, @NotNull String permission) {
        this(name, description, usageMessage);
        setPermission(permission);
    }

    @Override
    public final boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if(testPermission(sender)) {
            boolean exec = exec(sender, commandLabel, args);
            if(!exec) sender.sendMessage(getUsage());
            return exec;
        }
        return true;
    }

    @Override
    public final @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
        if(testPermissionSilent(sender)) {
            return complete(sender, alias, args);
        }
        return Collections.emptyList();
    }

    public abstract boolean exec(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args);

    public @NotNull List<String> complete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        return Collections.emptyList();
    }
}
