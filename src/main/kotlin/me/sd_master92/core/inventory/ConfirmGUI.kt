package me.sd_master92.core.inventory

import me.sd_master92.core.plugin.CustomPlugin
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.ItemStack

abstract class ConfirmGUI(plugin: CustomPlugin, name: String, confirm: String = "Confirm", cancel: String = "Cancel") : GUI(plugin, name, 9, false, true)
{
    abstract fun onConfirm(event: InventoryClickEvent, player: Player)
    abstract fun onCancel(event: InventoryClickEvent, player: Player)

    override fun onClick(event: InventoryClickEvent, player: Player, item: ItemStack)
    {
        when (item.type)
        {
            Material.GREEN_WOOL ->
            {
                cancelCloseEvent()
                onConfirm(event, player)
            }
            Material.RED_WOOL   ->
            {
                cancelCloseEvent()
                onCancel(event, player)
            }
            else                ->
            {
            }
        }
    }

    init
    {
        inventory.setItem(2, BaseItem(Material.GREEN_WOOL, ChatColor.GREEN.toString() + confirm))
        inventory.setItem(6, BaseItem(Material.RED_WOOL, ChatColor.RED.toString() + cancel))
    }
}