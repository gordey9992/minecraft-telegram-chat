package com.github.chatsync.managers;

import org.bukkit.entity.Player;

import java.util.List;
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
     * Получает префикс игрока через LuckPerms API
     */
    public static String getPlayerPrefix(Player player) {
        try {
            // Способ 1: LuckPerms API (самый надежный)
            String luckPermsPrefix = getLuckPermsPrefix(player);
            if (!luckPermsPrefix.isEmpty()) {
                return luckPermsPrefix;
            }
            
            // Способ 2: Из display name (резервный метод)
            String displayNamePrefix = getPrefixFromDisplayName(player);
            if (!displayNamePrefix.isEmpty()) {
                return displayNamePrefix;
            }
            
            // Способ 3: Из метаданных
            String metaPrefix = getPrefixFromMetadata(player);
            if (!metaPrefix.isEmpty()) {
                return metaPrefix;
            }
            
        } catch (Exception e) {
            // Игнорируем ошибки и возвращаем пустую строку
        }
        
        return "";
    }
    
    /**
     * Получает префикс через LuckPerms API
     */
    private static String getLuckPermsPrefix(Player player) {
        try {
            // Проверяем, установлен ли LuckPerms
            net.luckperms.api.LuckPerms luckPerms = getLuckPerms();
            if (luckPerms == null) {
                return "";
            }
            
            // Получаем пользователя
            net.luckperms.api.model.user.User user = luckPerms.getUserManager().getUser(player.getUniqueId());
            if (user == null) {
                return "";
            }
            
            // Получаем префикс из контекста
            net.luckperms.api.query.QueryOptions queryOptions = luckPerms.getContextManager().getQueryOptions(user).orElse(null);
            if (queryOptions == null) {
                return "";
            }
            
            // Получаем метаданные
            net.luckperms.api.cacheddata.CachedMetaData metaData = user.getCachedData().getMetaData(queryOptions);
            String prefix = metaData.getPrefix();
            
            if (prefix != null && !prefix.trim().isEmpty()) {
                return cleanMinecraftFormatting(prefix);
            }
            
        } catch (Exception e) {
            // LuckPerms не доступен или произошла ошибка
        }
        
        return "";
    }
    
    /**
     * Получает экземпляр LuckPerms
     */
    private static net.luckperms.api.LuckPerms getLuckPerms() {
        try {
            return org.bukkit.Bukkit.getServicesManager().getRegistration(net.luckperms.api.LuckPerms.class).getProvider();
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * Извлекает префикс из display name
     */
    private static String getPrefixFromDisplayName(Player player) {
        try {
            String displayName = player.getDisplayName();
            String playerName = player.getName();
            
            if (!displayName.equals(playerName)) {
                String cleanDisplayName = cleanMinecraftFormatting(displayName);
                String cleanPlayerName = cleanMinecraftFormatting(playerName);
                
                if (cleanDisplayName.contains(cleanPlayerName)) {
                    int nameIndex = cleanDisplayName.indexOf(cleanPlayerName);
                    if (nameIndex > 0) {
                        String potentialPrefix = cleanDisplayName.substring(0, nameIndex).trim();
                        // Убираем возможные скобки и пробелы
                        potentialPrefix = potentialPrefix.replaceAll("[\\[\\]()]", "").trim();
                        if (!potentialPrefix.isEmpty() && potentialPrefix.length() <= 15) {
                            return potentialPrefix;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Игнорируем ошибки
        }
        
        return "";
    }
    
    /**
     * Получает префикс из метаданных
     */
    private static String getPrefixFromMetadata(Player player) {
        try {
            // Проверяем различные ключи метаданных, которые могут содержать префикс
            String[] metaKeys = {"prefix", "chatprefix", "tag", "chattag", "essentials_prefix", "luckperms_prefix"};
            
            for (String key : metaKeys) {
                for (org.bukkit.metadata.MetadataValue meta : player.getMetadata(key)) {
                    if (meta != null && meta.value() instanceof String) {
                        String prefix = (String) meta.value();
                        String cleanPrefix = cleanMinecraftFormatting(prefix).trim();
                        if (!cleanPrefix.isEmpty()) {
                            return cleanPrefix;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Игнорируем ошибки метаданных
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
    public static boolean containsBannedWords(String message, List<String> bannedWords) {
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
