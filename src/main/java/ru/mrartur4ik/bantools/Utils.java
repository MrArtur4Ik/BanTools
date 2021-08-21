package ru.mrartur4ik.bantools;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Utils {

    public static @NotNull List<Player> getOnlinePlayersByAddress(String address) {
        List<Player> list = new ArrayList<>();
        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p.getAddress().getAddress().getHostAddress().equals(address)) {
                list.add(p);
            }
        }
        return list;
    }

    public static <K, V> @Nullable K getByValue(Map<K, V> map, V value) {
        for(Map.Entry<K, V> entry : map.entrySet()) {
            if(entry.getValue() == value) {
                return entry.getKey();
            }
        }
        return null;
    }
}
