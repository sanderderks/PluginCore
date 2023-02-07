package me.sd_master92.core

import me.sd_master92.core.plugin.CustomPlugin
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import java.sql.PreparedStatement
import java.util.*

fun String.appendWhenTrue(value: Boolean, append: String): String
{
    if (value)
    {
        return this + append
    }
    return this
}

fun Boolean.reverseWhenTrue(value: Boolean): Boolean
{
    if (value)
    {
        return !this
    }
    return this
}

fun PreparedStatement.setValue(i: Int, value: Any): PreparedStatement
{
    when (value)
    {
        is String -> this.setString(i, value)
        is Int    -> this.setInt(i, value)
        is Double -> this.setDouble(i, value)
        is Long   -> this.setLong(i, value)
        else      -> this.setString(i, value.toString())
    }
    return this
}

fun CustomPlugin.infoLog(message: String)
{
    server.consoleSender.sendMessage("[$pluginName] $message")
}

fun CustomPlugin.errorLog(message: String, e: Exception? = null)
{
    server.consoleSender.sendMessage(ChatColor.YELLOW.toString() + "[$pluginName] " + ChatColor.RESET + message)
    e?.let { println(it.toString()) }
}

fun String.getUuidByName(): UUID?
{
    return Bukkit.getOfflinePlayers().find { it.name == this }?.uniqueId
}