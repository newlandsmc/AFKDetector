package me.synapz.afkdetector

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class MessageTimer(val plugin: AFKDetector, val uuid: UUID): BukkitRunnable() {

    var repeats: Int = plugin.messageRepeats

    init {
        runTaskTimer(plugin, 0, plugin.messageDelay * 20.toLong())
    }

    override fun run() {
        val player = Bukkit.getPlayer(uuid)

        if (player == null || player.hasPermission("afkdetector.bypass")) {
            cancel()
            return
        }

        player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.message))
        repeats -= 1

        if (repeats <= 0) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), plugin.kickCommand.replace("%player%", player.name))
            cancel()
        }
    }

}