package com.github.chatsync;

import com.github.chatsync.managers.ConfigManager;
import com.github.chatsync.managers.TelegramManager;
import com.github.chatsync.listeners.ChatListener;
import com.github.chatsync.commands.ChatSyncCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatSync extends JavaPlugin {
    private static ChatSync instance;
    private ConfigManager configManager;
    private TelegramManager telegramManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Красивое приветствие в консоль
        printWelcomeMessage();
        
        // Инициализация менеджеров
        this.configManager = new ConfigManager(this);
        this.telegramManager = new TelegramManager(this);
        
        // Загрузка конфигурации
        configManager.loadConfigs();
        telegramManager.initialize();
        
        // Регистрация слушателей и команд
        Bukkit.getPluginManager().registerEvents(new ChatListener(this), this);
        getCommand("chatsync").setExecutor(new ChatSyncCommand(this));
        
        getLogger().info("✅ Синхронизация чата с Telegram успешно активирована!");
        telegramManager.sendToTelegram("💬 **ChatSync активирован!** Синхронизация чата Minecraft ↔ Telegram запущена.");
    }
    
    @Override
    public void onDisable() {
        telegramManager.sendToTelegram("💬 **ChatSync деактивирован.** Синхронизация чата остановлена.");
        getLogger().info("💬 ChatSync деактивирован.");
    }
    
    private void printWelcomeMessage() {
        System.out.println("\n");
        System.out.println("╔══════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                                                  ║");
        System.out.println("║    🎮  💬  M I N E C R A F T   -   T E L E G R A M   C H A T   S Y N C         ║");
        System.out.println("║                     С И Н Х Р О Н И З А Т О Р    Ч А Т А                        ║");
        System.out.println("║                                                                                  ║");
        System.out.println("║  🔄  Синхронизация глобального чата Minecraft ↔ Telegram в реальном времени     ║");
        System.out.println("║  📱  Поддержка EssentialsXChat & EssentialsX                                    ║");
        System.out.println("║  ⚙️   Полностью настраиваемый через config.yml и messages.yml                   ║");
        System.out.println("║  🎯  Русская локализация и подробная документация                               ║");
        System.out.println("║                                                                                  ║");
        System.out.println("║  👨‍💻 Авторы: gordey9992 & DeepSeek                                               ║");
        System.out.println("║  🌐 GitHub: https://github.com/gordey9992                                       ║");
        System.out.println("║  🚀 Версия: 1.0.0 | Сборка: " + getDescription().getVersion() + "                          ║");
        System.out.println("║                                                                                  ║");
        System.out.println("╚══════════════════════════════════════════════════════════════════════════════════╝");
        System.out.println("\n");
    }
    
    public static ChatSync getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() { return configManager; }
    public TelegramManager getTelegramManager() { return telegramManager; }
    
    public void reloadConfiguration() {
        configManager.loadConfigs();
        telegramManager.initialize();
        getLogger().info("✅ Конфигурация ChatSync перезагружена!");
    }
}
