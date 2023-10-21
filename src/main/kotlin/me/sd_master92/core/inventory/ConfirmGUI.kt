package me.sd_master92.core.inventory

import me.sd_master92.core.plugin.CustomPlugin
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

abstract class ConfirmGUI(plugin: CustomPlugin, name: String, confirm: String = "Confirm", cancel: String = "Cancel") :
    GUI(plugin, null, name)
{
    abstract fun onConfirm(event: InventoryClickEvent, player: Player)
    abstract fun onCancel(event: InventoryClickEvent, player: Player)

    override fun newInstance(): GUI
    {
        return this
    }

    override fun onClick(event: InventoryClickEvent, player: Player)
    {
    }

    override fun onClose(event: InventoryCloseEvent, player: Player)
    {
    }

    override fun onBack(event: InventoryClickEvent, player: Player)
    {
    }

    override fun onSave(event: InventoryClickEvent, player: Player)
    {
    }

    init
    {
        setItem(2, object : BaseItem(Material.GREEN_WOOL, ChatColor.GREEN.toString() + confirm)
        {
            override fun onClick(event: InventoryClickEvent, player: Player)
            {
                cancelCloseEvent = true
                onConfirm(event, player)
            }
        })
        setItem(6, object : BaseItem(Material.RED_WOOL, ChatColor.RED.toString() + cancel)
        {
            override fun onClick(event: InventoryClickEvent, player: Player)
            {
                cancelCloseEvent = true
                onCancel(event, player)
            }
        })
    }
}