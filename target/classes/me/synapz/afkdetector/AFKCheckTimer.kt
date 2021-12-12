package me.synapz.afkdetector

import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import kotlin.collections.ArrayList

class AFKCheckTimer(val plugin: AFKDetector): BukkitRunnable() {

    init {
        runTaskTimer(plugin, 0, 20)
    }

    override fun run() {

        val playerUUIDs = ArrayList(plugin.playerToSphereCenter.keys)

        for (uuid in playerUUIDs) {
            val player = Bukkit.getPlayer(uuid) ?: continue

            var pastLocation = plugin.playerToSphereCenter[uuid]
            if (pastLocation == null) {
                pastLocation = player.location
                plugin.playerToSphereCenter[uuid] = pastLocation
            }

            var standingTime = plugin.standingTime[uuid]
            if (standingTime == null) {
                standingTime = System.currentTimeMillis()
                plugin.standingTime[uuid] = standingTime
            }

            if (!player.location.world.name.equals(pastLocation!!.world.name)) {
                continue
            }

            if (player.location.distanceSquared(pastLocation) > plugin.radius * plugin.radius) {
                plugin.playerToSphereCenter[uuid] = player.location
                plugin.standingTime.put(player.uniqueId, System.currentTimeMillis())

                val currentTimer = plugin.messageTimers[player.uniqueId] ?: continue
                currentTimer.cancel()
                plugin.messageTimers.remove(player.uniqueId, currentTimer)
            } else {
                if (System.currentTimeMillis() - standingTime > plugin.flagTime * 1000) {
                    val currentTimer = plugin.messageTimers[player.uniqueId]

                    if (currentTimer == null) {
                        plugin.messageTimers[player.uniqueId] = MessageTimer(plugin, player.uniqueId)
                    }
                } else {
                    val currentTimer = plugin.messageTimers[player.uniqueId] ?: continue
                    currentTimer.cancel()
                    plugin.messageTimers.remove(player.uniqueId, currentTimer)
                }
            }
        }

    }

}