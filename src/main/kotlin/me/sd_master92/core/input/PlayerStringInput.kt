package me.sd_master92.core.input

import me.sd_master92.core.plugin.CustomPlugin
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable

abstract class PlayerStringInput(
    private val plugin: CustomPlugin,
    private val player: Player,
    private val allowCancel: Boolean = true
) : Listener
{
    abstract fun onInputReceived(input: String)
    abstract fun onCancel()

    @EventHandler
    fun onPlayerChat(event: AsyncPlayerChatEvent)
    {
        if (event.player == player)
        {
            event.isCancelled = true
            object : BukkitRunnable()
            {
                override fun run()
                {
                    processInput(event.message)
                }
            }.runTask(plugin)
        }
    }

    @EventHandler
    fun onCommandProcess(event: PlayerCommandPreprocessEvent)
    {
        if (event.player == player)
        {
            event.isCancelled = true
            processInput(event.message)
        }
    }

    @EventHandler
    fun onPlayerLeave(event: PlayerQuitEvent)
    {
        if (event.player == player)
        {
            cancel()
        }
    }

    fun cancel()
    {
        HandlerList.unregisterAll(this)
    }

    private fun processInput(input: String)
    {
        if (allowCancel && input == "cancel")
        {
            cancel()
            onCancel()
        } else
        {
            onInputReceived(input)
        }
    }

    private fun initialize()
    {
        plugin.registerListener(this)
    }

    init
    {
        initialize()
    }
}