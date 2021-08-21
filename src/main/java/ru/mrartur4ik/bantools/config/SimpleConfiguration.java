package ru.mrartur4ik.bantools.config;

import com.google.common.base.Charsets;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import ru.mrartur4ik.bantools.BanTools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

public class SimpleConfiguration extends YamlConfiguration {

    private final File file;

    public SimpleConfiguration(File file) {
        this.file = file;
        reloadConfig();
    }

    public void reloadConfig() {
        try {
            if(!file.exists()) {
                if(!saveDefaultConfig()) {
                    file.createNewFile();
                    setDefaults();
                    saveConfig();
                }
            }

            load(file);

            final InputStream defConfigStream = BanTools.getInstance().getResource(file.getName());

            if (defConfigStream != null) {
                setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, Charsets.UTF_8)));
            }
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        try {
            save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean saveDefaultConfig() {
        if(BanTools.getInstance().getResource(file.getName()) != null) {
            BanTools.getInstance().saveResource(file.getName(), true);
            return true;
        }
        return false;
    }

    public String getColorizedString(String path) {
        return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(getString(path)));
    }

    public Ban getBan(String path) {
        return getSerializable(path, Ban.class);
    }

    public boolean isBan(String path) {
        return getBan(path) != null;
    }

    public void setDefaults() {}
}
