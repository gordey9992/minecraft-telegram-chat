package com.github.chatsync.managers;

import net.ess3.api.IUser;
import net.ess3.api.events.NickChangeEvent;
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
     * Получает префикс игрока из EssentialsX
     */
    public static String getPlayerPrefix(Player player) {
        try {
            // Пытаемся получить префикс через EssentialsX
            IUser user = (IUser) player; // EssentialsX API
            String prefix = user.getPrefix();
            return prefix != null ? cleanMinecraftFormatting(prefix) : "";
        } catch (Exception e) {
            // Если EssentialsX не доступен, возвращаем пустую строку
            return "";
        }
    }
    
    /**
     * Получает никнейм игрока с учетом EssentialsX
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
}
