package me.sd_master92.core.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

abstract class SimpleSubCommand(val name: String)
{
    var minArgs = 0
        private set
    var usage: String? = null
        private set
    var permission: String? = null
        private set
    private var mustBePlayer = false

    abstract fun onCommand(sender: CommandSender, args: Array<String>)
    abstract fun onCommand(player: Player, args: Array<String>)

    fun withArguments(minArgs: Int, usage: String): SimpleSubCommand
    {
        this.minArgs = minArgs
        this.usage = usage
        return this
    }

    fun withPermission(permission: String): SimpleSubCommand
    {
        this.permission = permission
        return this
    }

    fun withPlayer(): SimpleSubCommand
    {
        mustBePlayer = true
        return this
    }

    fun mustBePlayer(): Boolean
    {
        return mustBePlayer
    }
}