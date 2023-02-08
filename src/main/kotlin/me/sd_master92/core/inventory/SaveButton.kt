package me.sd_master92.core.inventory

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class SaveButton(private val currentPage: GUI, private val backPage: GUI? = null) : BaseItem(Material.WRITABLE_BOOK, ChatColor.GREEN.toString() + "Save") {
    override fun onClick(event: InventoryClickEvent, player: Player)
    {
        event.isCancelled = true
        currentPage.cancelCloseEvent = true
        currentPage.onSave(event, player)
        if(backPage != null)
        {
            backPage.open(player)
        } else
        {
            player.closeInventory()
        }
    }
}