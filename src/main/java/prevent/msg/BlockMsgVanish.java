package prevent.msg;

import de.myzelyam.api.vanish.VanishAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public class BlockMsgVanish extends JavaPlugin implements Listener {

    // PM command aliases we want to intercept
    private final List<String> pmAliases = Arrays.asList("/msg", "/w", "/tell", "/whisper");

    @Override
    public void onEnable() {
        getLogger().info("BlockMsgVanish has been enabled!");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("BlockMsgVanish has been disabled!");
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage(); // e.g. "/msg target hello world"
        String lowerMessage = message.toLowerCase();

        // Only proceed if the message starts with one of our PM aliases, followed by a space.
        boolean isPM = false;
        for (String alias : pmAliases) {
            if (lowerMessage.startsWith(alias + " ")) {
                isPM = true;
                break;
            }
        }
        if (!isPM) {
            return; // Not a PM command, do nothing.
        }

        // Split the message into at most three parts:
        // parts[0]: command, parts[1]: target, parts[2]: message (which may contain spaces)
        String[] parts = message.split(" ", 3);

        // If there aren't at least 3 parts (command, target, message), let Essentials handle the usage.
        if (parts.length < 3) {
            return;
        }

        String targetName = parts[1];
        Player target = Bukkit.getPlayerExact(targetName);

        // If the target is online but vanished, cancel the command.
        if (target != null && VanishAPI.isInvisible(target)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cNo player was found");
        }
        // Otherwise, do nothing—allow the PM command to be processed normally.
    }
}

