package com.github.chatsync.managers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class ChatFormatter {
    private static final Pattern COLOR_PATTERN = Pattern.compile("§[0-9a-fk-or]");
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("§x(§[0-9a-f]){6}");
    
    /**
     * Очищает текст от цветовых кодов Minecraft
     */
    public static String cleanMinecraftFormatting(String text) {
        if (text == null) return "";
        
        // Удаляем HEX цвета
        String cleaned = HEX_COLOR_PATTERN.matcher(text).replaceAll("");
        // Удаляем обычные цвета
        cleaned = COLOR_PATTERN.matcher(cleaned).replaceAll("");
        
        return cleaned.trim();
    }
    
    /**
     * Получает префикс игрока из EssentialsX или Vault
     */
    public static String getPlayerPrefix(Player player) {
        try {
            // Способ 1: Через Vault API (если установлен)
            if (hasVault()) {
                net.milkbowl.vault.chat.Chat chat = getVaultChat();
                if (chat != null) {
                    String prefix = chat.getPlayerPrefix(player);
                    if (prefix != null && !prefix.trim().isEmpty()) {
                        return cleanMinecraftFormatting(prefix);
                    }
                }
            }
            
            // Способ 2: Через EssentialsX напрямую
            if (hasEssentials()) {
                // EssentialsX хранит префиксы в метаданных или конфигурации
                // Попробуем получить из display name
                String displayName = player.getDisplayName();
                if (!displayName.equals(player.getName())) {
                    // Если есть кастомный display name, может содержать префикс
                    return extractPrefixFromDisplayName(displayName, player.getName());
                }
            }
            
            // Способ 3: Из разрешений (LuckPerms и т.д.)
            return getPrefixFromPermissions(player);
            
        } catch (Exception e) {
            // Если ничего не работает, возвращаем пустую строку
            return "";
        }
    }
    
    /**
     * Проверяет наличие Vault
     */
    private static boolean hasVault() {
        try {
            Class.forName("net.milkbowl.vault.chat.Chat");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    /**
     * Получает Vault Chat provider
     */
    private static net.milkbowl.vault.chat.Chat getVaultChat() {
        try {
            org.bukkit.plugin.RegisteredServiceProvider<net.milkbowl.vault.chat.Chat> rsp = 
                org.bukkit.Bukkit.getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
            return rsp != null ? rsp.getProvider() : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Проверяет наличие EssentialsX
     */
    private static boolean hasEssentials() {
        try {
            Class.forName("com.earth2me.essentials.Essentials");
            return org.bukkit.Bukkit.getPluginManager().getPlugin("Essentials") != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
    
    /**
     * Извлекает префикс из display name
     */
    private static String extractPrefixFromDisplayName(String displayName, String playerName) {
        String cleanDisplayName = cleanMinecraftFormatting(displayName);
        String cleanPlayerName = cleanMinecraftFormatting(playerName);
        
        if (cleanDisplayName.contains(cleanPlayerName)) {
            int nameIndex = cleanDisplayName.indexOf(cleanPlayerName);
            if (nameIndex > 0) {
                String potentialPrefix = cleanDisplayName.substring(0, nameIndex).trim();
                // Убираем возможные скобки и пробелы
                potentialPrefix = potentialPrefix.replaceAll("[\\[\\]()]", "").trim();
                if (!potentialPrefix.isEmpty()) {
                    return potentialPrefix;
                }
            }
        }
        
        return "";
    }
    
    /**
     * Получает префикс из системы разрешений
     */
    private static String getPrefixFromPermissions(Player player) {
        try {
            // Проверяем метаданные игрока
            for (org.bukkit.metadata.MetadataValue meta : player.getMetadata("prefix")) {
                if (meta != null && meta.value() instanceof String) {
                    String prefix = (String) meta.value();
                    if (!prefix.trim().isEmpty()) {
                        return cleanMinecraftFormatting(prefix);
                    }
                }
            }
            
            // Проверяем метаданные от плагинов
            for (org.bukkit.metadata.MetadataValue meta : player.getMetadata("chatprefix")) {
                if (meta != null && meta.value() instanceof String) {
                    String prefix = (String) meta.value();
                    if (!prefix.trim().isEmpty()) {
                        return cleanMinecraftFormatting(prefix);
                    }
                }
            }
            
        } catch (Exception e) {
            // Игнорируем ошибки
        }
        
        return "";
    }
    
    /**
     * Получает никнейм игрока
     */
    public static String getPlayerDisplayName(Player player) {
        try {
            String displayName = player.getDisplayName();
            return cleanMinecraftFormatting(displayName);
        } catch (Exception e) {
            return cleanMinecraftFormatting(player.getName());
        }
    }
    
    /**
     * Форматирует сообщение для Telegram
     */
    public static String formatForTelegram(String playerName, String message, String prefix) {
        String cleanName = cleanMinecraftFormatting(playerName);
        String cleanMessage = cleanMinecraftFormatting(message);
        String cleanPrefix = cleanMinecraftFormatting(prefix);
        
        return String.format("%s%s: %s", 
            cleanPrefix.isEmpty() ? "" : "[" + cleanPrefix + "] ",
            cleanName, 
            cleanMessage
        );
    }
    
    /**
     * Проверяет, содержит ли сообщение запрещенные слова
     */
    public static boolean containsBannedWords(String message, java.util.List<String> bannedWords) {
        if (bannedWords == null || bannedWords.isEmpty()) return false;
        
        String cleanMessage = message.toLowerCase();
        for (String word : bannedWords) {
            if (cleanMessage.contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Упрощенный метод для получения префикса (без зависимостей)
     */
    public static String getSimplePlayerPrefix(Player player) {
        // Простая реализация без внешних зависимостей
        // Можно расширить позже
        return "";
    }
}
