package ru.mrartur4ik.bantools.config;

import org.apache.commons.io.FileUtils;
import ru.mrartur4ik.bantools.BanTools;

import java.io.File;
import java.io.IOException;

public class Configuration extends SimpleConfiguration {

    private static final File file = new File(BanTools.getInstance().getDataFolder() + File.separator + "config.yml");

    public Configuration() {
        super(file);

        if(!file.exists()) {
            options().copyDefaults(true);
            saveConfig();
        }

        if(getInt("version") != 1) {
            try {
                FileUtils.delete(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            reloadConfig();
            saveDefaultConfig();
        }
    }
}
