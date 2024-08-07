package me.sd_master92.core

import me.sd_master92.core.plugin.CustomPlugin
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.sql.PreparedStatement
import java.sql.Types
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

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

fun String.translateAlternateColorCodes(): String
{
    var message = this
    val pattern = Pattern.compile("&#[a-fA-F0-9]{6}")
    var matcher: Matcher = pattern.matcher(message)

    while (matcher.find())
    {
        val colorCode = message.substring(matcher.start(), matcher.end())
        val color = colorCode.substring(1)
        try
        {
            message = message.replace(colorCode, "" + net.md_5.bungee.api.ChatColor.of(color))
        } catch (_: Exception)
        {
        }
        matcher = pattern.matcher(message)
    }

    return ChatColor.translateAlternateColorCodes('&', message)
}

fun Array<ItemStack?>.withAir(): Array<ItemStack?>
{
    return this.map { it ?: ItemStack(Material.AIR) }.toTypedArray()
}

fun PreparedStatement.setValue(i: Int, value: Any?): PreparedStatement
{
    if (value == null)
    {
        this.setNull(i, Types.NULL)
    } else
    {
        when (value)
        {
            is String -> this.setString(i, value)
            is Int    -> this.setInt(i, value)
            is Double -> this.setDouble(i, value)
            is Long   -> this.setLong(i, value)
            else      -> this.setString(i, value.toString())
        }
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
    e?.printStackTrace()
}

fun String.getUuidByName(): UUID?
{
    return Bukkit.getOfflinePlayers().find { it.name == this }?.uniqueId
}

fun Inventory.firstEmpty(skip: Int? = null): Int
{
    return if (skip != null)
    {
        withIndex().find { (index, item) -> index >= skip && item == null }?.index ?: 0
    } else
    {
        firstEmpty()
    }
}

fun Inventory.lastEmpty(): Int?
{
    for (i in size - 1 downTo 0)
    {
        if (getItem(i) == null)
        {
            return i
        }
    }
    return null
}