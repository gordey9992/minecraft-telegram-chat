package com.github.chatsync.commands;

import com.github.chatsync.ChatSync;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ChatSyncCommand implements CommandExecutor {
    private final ChatSync plugin;
    
    public ChatSyncCommand(ChatSync plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "reload":
            case "перезагрузить":
                if (!sender.hasPermission("chatsync.reload")) {
                    sender.sendMessage(ChatColor.RED + "❌ У вас нет разрешения для этой команды.");
                    return true;
                }
                plugin.reloadConfiguration();
                sender.sendMessage(ChatColor.GREEN + "✅ Конфигурация ChatSync перезагружена!");
                break;
                
            case "status":
            case "статус":
                if (!sender.hasPermission("chatsync.status")) {
                    sender.sendMessage(ChatColor.RED + "❌ У вас нет разрешения для этой команды.");
                    return true;
                }
                sendStatus(sender);
                break;
                
            case "help":
            case "помощь":
                sendHelp(sender);
                break;
                
            default:
                sender.sendMessage(ChatColor.RED + "❌ Неизвестная команда. Используйте " + ChatColor.YELLOW + "/chatsync помощь");
                break;
        }
        return true;
    }
    
    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "╔══════════════════════════════════════════════╗");
        sender.sendMessage(ChatColor.GOLD + "║             " + ChatColor.AQUA + "ChatSync - Помощь" + ChatColor.GOLD + "                ║");
        sender.sendMessage(ChatColor.GOLD + "╠══════════════════════════════════════════════╣");
        sender.sendMessage(ChatColor.YELLOW + "║ /chatsync перезагрузить " + ChatColor.WHITE + "- Перезагрузить конфиг  ║");
        sender.sendMessage(ChatColor.YELLOW + "║ /chatsync статус " + ChatColor.WHITE + "- Статус плагина           ║");
        sender.sendMessage(ChatColor.YELLOW + "║ /chatsync помощь " + ChatColor.WHITE + "- Показать это сообщение  ║");
        sender.sendMessage(ChatColor.GOLD + "╚══════════════════════════════════════════════╝");
    }
    
    private void sendStatus(CommandSender sender) {
        boolean telegramEnabled = plugin.getConfig().getBoolean("telegram.enabled", true);
        boolean chatSyncEnabled = plugin.getConfig().getBoolean("chat-sync.enabled", true);
        
        sender.sendMessage(ChatColor.GOLD + "╔══════════════════════════════════════════════╗");
        sender.sendMessage(ChatColor.GOLD + "║             " + ChatColor.AQUA + "ChatSync - Статус" + ChatColor.GOLD + "                ║");
        sender.sendMessage(ChatColor.GOLD + "╠══════════════════════════════════════════════╣");
        sender.sendMessage(ChatColor.YELLOW + "║ Telegram: " + (telegramEnabled ? ChatColor.GREEN + "✅ Включен" : ChatColor.RED + "❌ Выключен") + ChatColor.GOLD + "           ║");
        sender.sendMessage(ChatColor.YELLOW + "║ Синхронизация: " + (chatSyncEnabled ? ChatColor.GREEN + "✅ Вкл" : ChatColor.RED + "❌ Выкл") + ChatColor.GOLD + "              ║");
        sender.sendMessage(ChatColor.YELLOW + "║ Авторы: " + ChatColor.WHITE + "gordey9992 & DeepSeek" + ChatColor.GOLD + "          ║");
        sender.sendMessage(ChatColor.GOLD + "╚══════════════════════════════════════════════╝");
    }
}
