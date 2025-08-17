package org.karlssonsmp.creeperGuard

import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bstats.bukkit.Metrics

class CreeperGuard : JavaPlugin(), Listener, TabCompleter {

    private var protectionEnabled = true
    private val prefix = "§8§lᴄʀᴇᴇᴘᴇʀɢᴜᴀʀᴅ §7» §r"

    override fun onEnable() {
        saveDefaultConfig()
        protectionEnabled = config.getBoolean("creeperguard-enabled", true)
        val metricsEnabled = config.getBoolean("metrics", true)

        server.pluginManager.registerEvents(this, this)
        getCommand("creeperguard")?.setExecutor(this)
        getCommand("creeperguard")?.tabCompleter = this

        if (metricsEnabled) {
            val pluginId = 26867
            Metrics(this, pluginId)
        }

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
                if (protectionEnabled) {
                    sender.sendMessage("${prefix}§aCreeper protection is already ON")
                } else {
                    protectionEnabled = true
                    saveState()
                    sender.sendMessage("${prefix}§aCreeper protection ON")
                }
            }
            "off" -> {
                if (!protectionEnabled) {
                    sender.sendMessage("${prefix}§cCreeper protection is already OFF")
                } else {
                    protectionEnabled = false
                    saveState()
                    sender.sendMessage("${prefix}§cCreeper protection OFF")
                }
            }
            else -> {
                sender.sendMessage("${prefix}§7Usage: /$label [on | off]")
            }
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String> {
        if (!sender.hasPermission("creeperguard.admin")) {
            return emptyList()
        }

        return when (args.size) {
            1 -> {
                val options = listOf("on", "off")
                options.filter { it.startsWith(args[0].lowercase()) }
            }
            else -> emptyList()
        }
    }
}