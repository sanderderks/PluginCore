package me.sd_master92.core.inventory

import me.sd_master92.core.plugin.CustomPlugin
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class SaveButton(private val currentPage: GUI) : BaseItem(Material.WRITABLE_BOOK, CustomPlugin.SAVE_TEXT)
{
    override fun onClick(event: InventoryClickEvent, player: Player)
    {
        event.isCancelled = true
        currentPage.cancelCloseEvent = true
        currentPage.onSave(event, player)
        if (currentPage.backPage != null)
        {
            currentPage.backPage!!.newInstance().open(player)
        } else
        {
            player.closeInventory()
        }
    }
}