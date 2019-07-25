package me.sebaarkadia.practice.util;

import java.io.*;
import org.bukkit.configuration.file.*;

import me.sebaarkadia.practice.Practice;

import org.bukkit.*;

public class FileConfig
{
    public String fileName;
    public File configFile;
    private FileConfiguration config;
    
    public FileConfig(final String fileName) {
        this.fileName = fileName;
        this.configFile = new File(Practice.getInstance().getDataFolder(), fileName);
        if (!this.configFile.exists()) {
            this.configFile.getParentFile().mkdirs();
            if (Practice.getInstance().getResource(fileName) == null) {
                try {
                    this.configFile.createNewFile();
                }
                catch (IOException e) {
                    Practice.getInstance().getLogger().severe("Failed to create new file " + fileName);
                }
            }
            else {
                Practice.getInstance().saveResource(fileName, false);
            }
        }
        this.config = (FileConfiguration)YamlConfiguration.loadConfiguration(this.configFile);
    }
    
    public FileConfig(final File file, final String fileName) {
        this.configFile = new File(file, fileName);
        if (!this.configFile.exists()) {
            this.configFile.getParentFile().mkdirs();
            if (Practice.getInstance().getResource(fileName) == null) {
                try {
                    this.configFile.createNewFile();
                }
                catch (IOException e) {
                    Practice.getInstance().getLogger().severe("Failed to create new file " + fileName);
                }
            }
            else {
                Practice.getInstance().saveResource(fileName, false);
            }
        }
        this.config = (FileConfiguration)YamlConfiguration.loadConfiguration(this.configFile);
    }
    
    public FileConfiguration getConfig() {
        return this.config;
    }
    
    public void save() {
        try {
            this.getConfig().save(this.configFile);
        }
        catch (IOException e) {
            Bukkit.getLogger().severe("Could not save config file " + this.configFile.toString());
            e.printStackTrace();
        }
    }
}
