package com.github.chatsync.listeners;

import com.github.chatsync.ChatSync;
import com.github.chatsync.managers.ChatFormatter;
import com.github.chatsync.managers.TelegramManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import java.util.List;

public class ChatListener implements Listener {
    private final ChatSync plugin;
    private final TelegramManager telegramManager;
    
    public ChatListener(ChatSync plugin) {
        this.plugin = plugin;
        this.telegramManager = plugin.getTelegramManager();
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!telegramManager.isEnabled()) return;
        if (!plugin.getConfig().getBoolean("chat-sync.enabled", true)) return;
        
        Player player = event.getPlayer();
        String message = event.getMessage();
        
        // Проверяем запрещенные слова
        List<String> bannedWords = plugin.getConfig().getStringList("chat-sync.banned-words");
        if (ChatFormatter.containsBannedWords(message, bannedWords)) {
            plugin.getLogger().info("❌ Сообщение от " + player.getName() + " содержит запрещенные слова и не было отправлено в Telegram");
            return;
        }
        
        // Проверяем минимальную длину сообщения
        int minLength = plugin.getConfig().getInt("chat-sync.min-message-length", 2);
        if (message.trim().length() < minLength) {
            return;
        }
        
        // В методе onPlayerChat замените эту строку:
        String prefix = ChatFormatter.getPlayerPrefix(player); // Должно быть так
        String playerName = ChatFormatter.getPlayerDisplayName(player);
        
        // Отправляем сообщение в Telegram
        telegramManager.sendChatMessage(playerName, message, prefix);
        
        // Логируем в консоль
        plugin.getLogger().info("💬 " + playerName + ": " + message);
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!telegramManager.isEnabled()) return;
        
        Player player = event.getPlayer();
        String playerName = ChatFormatter.getPlayerDisplayName(player);
        
        telegramManager.sendJoinMessage(playerName);
        plugin.getLogger().info("🟢 " + playerName + " присоединился к серверу");
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!telegramManager.isEnabled()) return;
        
        Player player = event.getPlayer();
        String playerName = ChatFormatter.getPlayerDisplayName(player);
        
        telegramManager.sendLeaveMessage(playerName);
        plugin.getLogger().info("🔴 " + playerName + " покинул сервер");
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!telegramManager.isEnabled()) return;
        
        String deathMessage = ChatFormatter.cleanMinecraftFormatting(event.getDeathMessage());
        telegramManager.sendDeathMessage(deathMessage);
        plugin.getLogger().info("💀 " + deathMessage);
    }
    
    @EventHandler
    public void onPlayerAdvancement(PlayerAdvancementDoneEvent event) {
        if (!telegramManager.isEnabled()) return;
        
        Player player = event.getPlayer();
        String advancementKey = event.getAdvancement().getKey().getKey();
        
        // Игнорируем стандартные рецепты
        if (advancementKey.startsWith("recipes/")) {
            return;
        }
        
        String playerName = ChatFormatter.getPlayerDisplayName(player);
        String advancementName = getAdvancementName(advancementKey);
        
        telegramManager.sendAchievementMessage(playerName, advancementName);
        plugin.getLogger().info("🏆 " + playerName + " получил достижение: " + advancementName);
    }
    
    private String getAdvancementName(String key) {
        // Простой маппинг ключей достижений на русские названия
        switch (key) {
            case "story/root": return "Начало истории";
            case "story/mine_stone": return "Каменный век";
            case "story/iron_tools": return "Железные инструменты";
            case "story/obtain_armor": return "Броня";
            case "story/deflect_arrow": return "Защита";
            case "story/enchant_item": return "Зачарование";
            case "story/enter_the_end": return "Энд";
            case "story/follow_ender_eye": return "Глаз эндера";
            case "story/enter_the_nether": return "Незер";
            case "story/upgrade_tools": return "Улучшение инструментов";
            case "story/smelt_iron": return "Выплавка железа";
            case "story/cure_zombie_villager": return "Лечение зомби-жителя";
            case "story/shiny_gear": return "Блестящая экипировка";
            default: return key.replace("_", " ");
        }
    }
}
