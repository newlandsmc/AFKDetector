package me.synapz.afkdetector

import org.bukkit.plugin.java.JavaPlugin
import com.google.common.io.MoreFiles.listFiles
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerJoinEvent
import java.util.*
import kotlin.collections.HashMap

class AFKDetector : JavaPlugin(), Listener {

    val playerToSphereCenter = HashMap<UUID, Location>()
    val standingTime = HashMap<UUID, Long>()
    val messageTimers = HashMap<UUID, MessageTimer>()

    var radius = 4
    var message = "${ChatColor.GRAY}You need to move around to avoid being kicked."
    var flagTime = 300
    var messageDelay = 30
    var messageRepeats = 4
    var kickCommand = "kick ${ChatColor.RED}You have been kicked for being AFK."
    var chatWillCancel = true

    override fun onEnable() {
        loadSettings()

        Bukkit.getPluginManager().registerEvents(this, this)

        for (player in Bukkit.getOnlinePlayers()) {
            playerToSphereCenter.put(player.uniqueId, player.location)
            standingTime.put(player.uniqueId, System.currentTimeMillis())
        }

        AFKCheckTimer(this)
    }

    fun loadSettings() {
        if (!dataFolder.exists()) {
            dataFolder.mkdir()
        }

        if (dataFolder.listFiles().none { it.name.equals("config.yml") })
            saveResource("config.yml", false)

        radius = config.getInt("radius")
        message = config.getString("message")
        flagTime = config.getInt("flag-time")
        messageDelay = config.getInt("message-delay")
        messageRepeats = config.getInt("message-repeats")
        kickCommand = config.getString("kick-command")
        chatWillCancel = config.getBoolean("chat-cancels-timer")
    }

    @EventHandler
    fun playerJoin(join: PlayerJoinEvent) {
        val player = join.player

		playerToSphereCenter.put(player.uniqueId, player.location)
		standingTime.put(player.uniqueId, System.currentTimeMillis())
    }
    
	@EventHandler
    fun playerQuit(quit: PlayerQuitEvent) {
		val player = quit.player
		
		playerToSphereCenter.remove(player.uniqueId)
		standingTime.remove(player.uniqueId)
    }

    @EventHandler
    fun onTalkCancel(chat: AsyncPlayerChatEvent) {
        if (!chatWillCancel) {
            return
        }
        val player = chat.player
        
		playerToSphereCenter.remove(player.uniqueId)
		standingTime.remove(player.uniqueId)

		playerToSphereCenter.put(player.uniqueId, player.location)
		standingTime.put(player.uniqueId, System.currentTimeMillis())
        //val timer = messageTimers[player.uniqueId] ?: return

       	//timer.cancel()
    }
    
    @EventHandler
    fun oncommandCancel(command: PlayerCommandPreprocessEvent) {
        if (!chatWillCancel) {
            return
        }
        val player = command.player
        
		playerToSphereCenter.remove(player.uniqueId)
		standingTime.remove(player.uniqueId)

		playerToSphereCenter.put(player.uniqueId, player.location)
		standingTime.put(player.uniqueId, System.currentTimeMillis())

	}
}
