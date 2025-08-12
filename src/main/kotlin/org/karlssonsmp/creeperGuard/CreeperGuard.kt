package org.karlssonsmp.creeperGuard

import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bstats.bukkit.Metrics

class CreeperGuard : JavaPlugin(), Listener {

    private var protectionEnabled = true

    private val prefix = "§8§lᴄʀᴇᴇᴘᴇʀɢᴜᴀʀᴅ §7» §r"

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
        if (!protectionEnabled) return

        if (event.entity.type == EntityType.CREEPER) {
            event.blockList().clear()
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (!sender.hasPermission("creeperguard.admin")) {
            sender.sendMessage("${prefix}§cYou don't have permission to do that.")
            return true
        }

        when (args.getOrNull(0)?.lowercase()) {
            "on" -> {
                protectionEnabled = true
                saveState()
                sender.sendMessage("${prefix}§aCreeper protection ON")
            }
            "off" -> {
                protectionEnabled = false
                saveState()
                sender.sendMessage("${prefix}§cCreeper protection OFF")
            }
            else -> {
                sender.sendMessage("${prefix}§7Usage: /$label [on | off]")
            }
        }
        return true
    }
}