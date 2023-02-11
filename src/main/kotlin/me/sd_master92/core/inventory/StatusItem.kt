package me.sd_master92.core.inventory

import me.sd_master92.core.file.CustomFile
import me.sd_master92.core.plugin.CustomPlugin
import me.sd_master92.core.reverseWhenTrue
import org.bukkit.ChatColor
import org.bukkit.Material

abstract class StatusItem(mat: Material, name: String, val value: Boolean) : BaseItem(
    mat,
    ChatColor.LIGHT_PURPLE.toString() + name,
    CustomPlugin.STATUS_TEXT + " " + if (value) CustomPlugin.ON_TEXT else CustomPlugin.OFF_TEXT
)
{
    constructor(mat: Material, name: String, config: CustomFile, path: String, reverse: Boolean = false) : this(
        mat,
        name,
        config.getBoolean(path).reverseWhenTrue(reverse)
    )
}