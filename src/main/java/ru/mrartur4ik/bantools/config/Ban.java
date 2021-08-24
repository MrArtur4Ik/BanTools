package ru.mrartur4ik.bantools.config;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Ban implements ConfigurationSerializable {

    private final String reason;
    private UUID from;
    private final long time;

    /**
     * Constructor for deserialization
     * @param args Map to deserialize
     */
    public Ban(Map<String, Object> args) {
        if(args.containsKey("reason")) {
            reason = ChatColor.translateAlternateColorCodes('&', (String) args.get("reason"));
        } else {
            reason = "";
        }
        String from = (String) args.get("from");
        if(from.matches("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}")) {
            this.from = UUID.fromString(from);
        } else {
            this.from = Bukkit.getOfflinePlayer(from).getUniqueId();
        }
        this.time = (Long) args.get("time");
    }

    public Ban(String reason, UUID from, long time) {
        this.reason = reason;
        this.from = from;
        this.time = time;
    }

    public Ban(String reason, UUID from) {
        this(reason, from, -1);
    }

    public Ban(UUID from) {
        this("", from);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        Map<String, Object> args = new HashMap<>();

        args.put("reason", reason);
        args.put("from", from.toString());
        args.put("time", time);

        return args;
    }

    public final String getReason() {
        return reason;
    }

    public UUID getFrom() {
        return from;
    }

    public void setFrom(UUID uuid) {
        this.from = uuid;
    }

    public final long getTime() {
        return time;
    }
}
