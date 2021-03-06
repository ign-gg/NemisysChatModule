package me.petterim1.nemisyschat;

import org.itxtech.nemisys.Client;
import org.itxtech.nemisys.Player;
import org.itxtech.nemisys.event.EventHandler;
import org.itxtech.nemisys.event.Listener;
import org.itxtech.nemisys.event.player.PlayerChatEvent;
import org.itxtech.nemisys.plugin.PluginBase;
import org.itxtech.nemisys.utils.Config;

public class Main extends PluginBase implements Listener {

    private static Config config;

    private String lastMessage;
    private String lastPlayer;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        config = getConfig();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onChat(PlayerChatEvent e) {
        Player p = e.getPlayer();

        if (config.getStringList("chat_disabled_from").contains(p.getClient().getDescription())) {
            return;
        }

        String name = p.getName();
        String message = e.getMessage();

        if (config.getBoolean("spam_filter")) {
            if (message.equals(lastMessage) && name.equals(lastPlayer)) return;
            lastMessage = message;
            lastPlayer = name;
        }

        if (config.getBoolean("log_messages")) {
            getServer().getLogger().info(config.getString("chat_format").replace("%server%", p.getClient().getDescription()).replace("%player%", name).replace("%message%", message).replaceAll("§", "\u00A7"));
        }

        for (Client c : getServer().getClients().values()) {
            if (config.getStringList("chat_disabled_to").contains(c.getDescription())) {
                continue;
            }

            if (!c.getDescription().equals(p.getClient().getDescription())) {
                c.getPlayers().forEach((u, pl) -> {
                    pl.sendMessage(config.getString("chat_format").replace("%server%", p.getClient().getDescription()).replace("%player%", name).replace("%message%", message).replaceAll("§", "\u00A7"));
                });
            }
        }
    }
}