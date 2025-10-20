package com.github.chatsync.managers;

import com.github.chatsync.ChatSync;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class ConfigManager {
    private final ChatSync plugin;
    private FileConfiguration messagesConfig;
    private File messagesFile;

    public ConfigManager(ChatSync plugin) {
        this.plugin = plugin;
    }

    public void loadConfigs() {
        // Сохраняем конфиги по умолчанию
        plugin.saveDefaultConfig();
        saveDefaultConfig("messages.yml");
        
        // Загружаем конфиги
        messagesConfig = loadConfig("messages.yml");
        
        plugin.getLogger().info("✅ Конфигурации загружены");
    }

    private void saveDefaultConfig(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            try (InputStream in = plugin.getResource(fileName)) {
                if (in != null) {
                    Files.copy(in, file.toPath());
                }
            } catch (IOException e) {
                plugin.getLogger().severe("❌ Не удалось сохранить " + fileName + ": " + e.getMessage());
            }
        }
    }

    private FileConfiguration loadConfig(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            saveDefaultConfig(fileName);
        }
        return YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getMessagesConfig() {
        return messagesConfig;
    }

    public String getMessage(String path, String def) {
        if (messagesConfig != null && messagesConfig.contains(path)) {
            return messagesConfig.getString(path, def).replace('&', '§');
        }
        return def.replace('&', '§');
    }

    public void saveMessagesConfig() {
        try {
            if (messagesConfig != null && messagesFile != null) {
                messagesConfig.save(messagesFile);
            }
        } catch (IOException e) {
            plugin.getLogger().severe("❌ Не удалось сохранить messages.yml: " + e.getMessage());
        }
    }
}
