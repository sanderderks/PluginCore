package me.sd_master92.core.inventory

import me.sd_master92.core.file.CustomFile
import me.sd_master92.core.reverseWhenTrue
import org.bukkit.ChatColor
import org.bukkit.Material

abstract class StatusItem(mat: Material, name: String, value: Boolean) : BaseItem(
    mat,
    ChatColor.LIGHT_PURPLE.toString() + name,
    ChatColor.GRAY.toString() + "Status: " + if (value) ChatColor.GREEN.toString() + "ON" else ChatColor.RED.toString() + "OFF"
)
{
    constructor(mat: Material, name: String, config: CustomFile, path: String, reverse: Boolean = false) : this(
        mat,
        name,
        config.getBoolean(path).reverseWhenTrue(reverse)
    )
}