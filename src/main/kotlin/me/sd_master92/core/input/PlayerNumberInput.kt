package me.sd_master92.core.input

import me.sd_master92.core.plugin.CustomPlugin
import org.bukkit.ChatColor
import org.bukkit.entity.Player

abstract class PlayerNumberInput(
    plugin: CustomPlugin,
    private val player: Player,
    private val min: Int = 0,
    private val max: Int = 1000000,
    allowCancel: Boolean = true
) : PlayerStringInput(plugin, player, allowCancel)
{
    abstract fun onNumberReceived(input: Int)

    override fun onInputReceived(input: String)
    {
        try
        {
            val number = input.toInt()
            if (number < min || number > max)
            {
                player.sendMessage(ChatColor.RED.toString() + "Enter a number between $min and $max")
            } else
            {
                onNumberReceived(number)
            }
        } catch (e: Exception)
        {
            player.sendMessage(ChatColor.RED.toString() + "Enter a number")
        }
    }
}