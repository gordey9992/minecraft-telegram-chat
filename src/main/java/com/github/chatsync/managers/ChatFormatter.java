package com.github.chatsync.managers;

import org.bukkit.entity.Player;
import java.util.List;
import java.util.regex.Pattern;

public class ChatFormatter {
    private static final Pattern COLOR_PATTERN = Pattern.compile("§[0-9a-fk-or]");
    
    public static String cleanMinecraftFormatting(String text) {
        if (text == null) return "";
        return COLOR_PATTERN.matcher(text).replaceAll("").trim();
    }
    
    public static String getPlayerPrefix(Player player) {
        return ""; // Упрощенно - без префиксов
    }
    
    public static String getPlayerDisplayName(Player player) {
        return cleanMinecraftFormatting(player.getName());
    }
    
    public static boolean containsBannedWords(String message, List<String> bannedWords) {
        if (bannedWords == null || bannedWords.isEmpty()) return false;
        String cleanMessage = message.toLowerCase();
        for (String word : bannedWords) {
            if (cleanMessage.contains(word.toLowerCase())) return true;
        }
        return false;
    }
}
