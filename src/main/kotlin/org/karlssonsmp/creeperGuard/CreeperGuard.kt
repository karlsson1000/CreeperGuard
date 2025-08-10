package org.karlssonsmp.creeperGuard

import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bstats.bukkit.Metrics

class CreeperGuard : JavaPlugin(), Listener {

    private var protectionEnabled = true
    private val mm = MiniMessage.miniMessage()

    override fun onEnable() {
        saveDefaultConfig()
        protectionEnabled = config.getBoolean("creeperguard-enabled", true)
        server.pluginManager.registerEvents(this, this)
        getCommand("creeperguard")?.setExecutor(this)

        val pluginId = 26867
        Metrics(this, pluginId)

        logger.info("CreeperGuard is ready to protect your builds from creeper damage!")
    }

    private fun saveState() {
        config.set("creeperguard-enabled", protectionEnabled)
        saveConfig()
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onEntityExplode(event: EntityExplodeEvent) {
        if (protectionEnabled && event.entity.type == EntityType.CREEPER) {
            event.blockList().clear()
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!sender.hasPermission("creeperguard.admin")) {
            sender.sendMessage(mm.deserialize("<red>No permission!"))
            return true
        }

        when (args.getOrNull(0)?.lowercase()) {
            "on" -> {
                protectionEnabled = true
                saveState()
                sender.sendMessage(mm.deserialize("<bold><dark_gray>ᴄʀᴇᴇᴘᴇʀɢᴜᴀʀᴅ</dark_gray></bold> <gray>»</gray> <green>Creeper protection ON</green>"))
            }
            "off" -> {
                protectionEnabled = false
                saveState()
                sender.sendMessage(mm.deserialize("<bold><dark_gray>ᴄʀᴇᴇᴘᴇʀɢᴜᴀʀᴅ</dark_gray></bold> <gray>»</gray> <red>Creeper protection OFF</red>"))
            }
            else -> {
                sender.sendMessage(mm.deserialize("<bold><dark_gray>ᴄʀᴇᴇᴘᴇʀɢᴜᴀʀᴅ</dark_gray></bold> <gray>»</gray> <green>Usage: /$label [on | off]</green>"))
            }
        }
        return true
    }
}