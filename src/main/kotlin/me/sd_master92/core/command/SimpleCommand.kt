package me.sd_master92.core.command

import com.github.shynixn.mccoroutine.bukkit.SuspendingCommandExecutor
import com.github.shynixn.mccoroutine.bukkit.setSuspendingExecutor
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin


abstract class SimpleCommand @JvmOverloads constructor(
    private val plugin: JavaPlugin, private val name: String, private val allowZeroArgs: Boolean = true,
    vararg subCommands: SimpleSubCommand
) : SuspendingCommandExecutor, TabCompleter
{
    private var subCommands: Map<String, SimpleSubCommand>
    private var mustBePlayer = false
    private var usage: String = ChatColor.RED.toString()
    private var noPermMsg: String
    private var notPlayerMsg: String

    fun register()
    {
        val command = plugin.getCommand(name)
        if (command != null)
        {
            command.setSuspendingExecutor(this)
            usage += command.usage
        }
    }

    abstract suspend fun onCommand(sender: CommandSender, args: Array<out String>)
    abstract suspend fun onCommand(player: Player, args: Array<out String>)

    override suspend fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean
    {
        if (hasPermission(sender, command.permission) && validateArguments(sender, label, args))
        {
            if (!mustBePlayer)
            {
                onCommand(sender, args)
            } else if (isPlayer(sender))
            {
                onCommand(sender as Player, args)
            }
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        cmd: Command,
        p2: String,
        args: Array<String>
    ): MutableList<String>?
    {
        if (cmd.name == name)
        {
            return this.subCommands.values
                    .filter { subCommand ->
                        subCommand.permission?.let { sender.hasPermission(it) } ?: true
                    }
                    .filter { subCommand -> !subCommand.mustBePlayer() || sender is Player }
                    .map { subCommand -> subCommand.name }
                    .toMutableList()
                    .ifEmpty { null }
        }
        return null
    }

    fun withUsage(usage: String): SimpleCommand
    {
        this.usage = usage
        return this
    }

    fun withNoPermMessage(noPermMsg: String): SimpleCommand
    {
        this.noPermMsg = noPermMsg
        return this
    }

    fun withPlayer(notPlayerMsg: String): SimpleCommand
    {
        this.notPlayerMsg = notPlayerMsg
        return withPlayer()
    }

    fun withPlayer(): SimpleCommand
    {
        mustBePlayer = true
        return this
    }

    private fun isPlayer(sender: CommandSender): Boolean
    {
        if (sender is Player)
        {
            return true
        }
        sender.sendMessage(notPlayerMsg)
        return false
    }

    private fun hasPermission(sender: CommandSender, permission: String?): Boolean
    {
        if (permission == null || sender.hasPermission(permission))
        {
            return true
        }
        sender.sendMessage(noPermMsg)
        return false
    }

    private suspend fun validateArguments(sender: CommandSender, label: String, args: Array<out String>): Boolean
    {
        if (args.isEmpty() && allowZeroArgs)
        {
            return true
        }
        if (args.isNotEmpty())
        {
            val subCommand = subCommands[args[0].lowercase()]
            if (subCommand != null)
            {
                if (hasPermission(sender, subCommand.permission))
                {
                    if ((args.size - 1) >= subCommand.minArgs)
                    {
                        if (!subCommand.mustBePlayer())
                        {
                            subCommand.onCommand(sender, args)
                        } else if (isPlayer(sender))
                        {
                            subCommand.onCommand(sender as Player, args)
                        }
                        return false
                    } else if (subCommand.usage != null)
                    {
                        sendMessage(sender, subCommand.usage!!, label)
                    }
                }
                return false
            }
            return true
        }
        sendMessage(sender, usage, label)
        return false
    }

    private fun sendMessage(sender: CommandSender, message: String, label: String)
    {
        sender.sendMessage(message.replace(COMMAND_LABEL, label))
    }

    companion object
    {
        const val COMMAND_LABEL = "<command>"
        val NO_PERMISSION = ChatColor.RED.toString() + "You do not have permission to perform this command."
        val MUST_BE_PLAYER = ChatColor.RED.toString() + "You must be a player to perform this command."
    }

    init
    {
        noPermMsg = NO_PERMISSION
        notPlayerMsg = MUST_BE_PLAYER
        this.subCommands = subCommands.associateBy { it.name }
    }
}