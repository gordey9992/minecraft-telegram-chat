package com.github.chatsync.managers;

import com.github.chatsync.ChatSync;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class TelegramManager {
    private final ChatSync plugin;
    private final String botToken;
    private final String chatId;
    private final int threadId;
    private boolean enabled;

    public TelegramManager(ChatSync plugin) {
        this.plugin = plugin;
        FileConfiguration config = plugin.getConfig();
        this.botToken = config.getString("telegram.bot-token", "8263486505:AAHs0FDTQJrNRoMd1loLaNASWrp4SfKEuRc");
        this.chatId = config.getString("telegram.chat-id", "-1002935953089");
        this.threadId = config.getInt("telegram.thread-id", 434);
        this.enabled = config.getBoolean("telegram.enabled", true);
    }

    public void initialize() {
        if (enabled) {
            plugin.getLogger().info("📱 Telegram менеджер инициализирован");
        }
    }

    public void sendToTelegram(String message) {
        if (!enabled) return;

        new Thread(() -> {
            try {
                String urlString = "https://api.telegram.org/bot" + botToken + "/sendMessage";
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String jsonPayload = String.format(
                    "{\"chat_id\": \"%s\", \"message_thread_id\": %d, \"text\": \"%s\", \"parse_mode\": \"Markdown\"}",
                    chatId, threadId, escapeJsonString(message)
                );

                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = conn.getResponseCode();
                if (responseCode != 200) {
                    plugin.getLogger().warning("❌ Ошибка Telegram: " + responseCode);
                }
                
                conn.disconnect();
            } catch (Exception e) {
                plugin.getLogger().warning("❌ Ошибка отправки в Telegram: " + e.getMessage());
            }
        }).start();
    }

    private String escapeJsonString(String value) {
        return value.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }

    private String escapeMarkdown(String text) {
        return text.replace("_", "\\_")
                  .replace("*", "\\*")
                  .replace("[", "\\[")
                  .replace("]", "\\]")
                  .replace("(", "\\(")
                  .replace(")", "\\)")
                  .replace("~", "\\~")
                  .replace("`", "\\`")
                  .replace(">", "\\>")
                  .replace("#", "\\#")
                  .replace("+", "\\+")
                  .replace("-", "\\-")
                  .replace("=", "\\=")
                  .replace("|", "\\|")
                  .replace("{", "\\{")
                  .replace("}", "\\}")
                  .replace(".", "\\.")
                  .replace("!", "\\!");
    }

    public void sendChatMessage(String playerName, String message, String prefix) {
        String format = plugin.getConfigManager().getMessage("formats.chat-to-telegram", "💬 **%s%s**: %s");
        String formattedMessage = String.format(
            format,
            prefix != null && !prefix.isEmpty() ? "[" + prefix + "] " : "",
            escapeMarkdown(playerName),
            escapeMarkdown(message)
        );
        sendToTelegram(formattedMessage);
    }

    public void sendJoinMessage(String playerName) {
        if (plugin.getConfig().getBoolean("chat-sync.events.player-join", true)) {
            String format = plugin.getConfigManager().getMessage("formats.player-join", "🟢 **%s** присоединился к серверу");
            String message = String.format(format, escapeMarkdown(playerName));
            sendToTelegram(message);
        }
    }

    public void sendLeaveMessage(String playerName) {
        if (plugin.getConfig().getBoolean("chat-sync.events.player-leave", true)) {
            String format = plugin.getConfigManager().getMessage("formats.player-leave", "🔴 **%s** покинул сервер");
            String message = String.format(format, escapeMarkdown(playerName));
            sendToTelegram(message);
        }
    }

    public void sendServerMessage(String message) {
        String format = plugin.getConfigManager().getMessage("formats.server-message", "⚡ **Сервер**: %s");
        sendToTelegram(String.format(format, escapeMarkdown(message)));
    }

    public void sendDeathMessage(String deathMessage) {
        if (plugin.getConfig().getBoolean("chat-sync.events.player-death", true)) {
            String format = plugin.getConfigManager().getMessage("formats.player-death", "💀 %s");
            sendToTelegram(String.format(format, escapeMarkdown(deathMessage)));
        }
    }

    public void sendAchievementMessage(String playerName, String achievement) {
        if (plugin.getConfig().getBoolean("chat-sync.events.player-achievement", true)) {
            String format = plugin.getConfigManager().getMessage("formats.player-achievement", "🏆 **%s** получил достижение: %s");
            String message = String.format(format, escapeMarkdown(playerName), escapeMarkdown(achievement));
            sendToTelegram(message);
        }
    }

    public boolean isEnabled() {
        return enabled;
    }
}
