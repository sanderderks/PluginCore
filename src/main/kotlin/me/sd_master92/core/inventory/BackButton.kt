package me.sd_master92.core.inventory

import me.sd_master92.core.plugin.CustomPlugin
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class BackButton(private val currentPage: GUI) : BaseItem(Material.BARRIER, CustomPlugin.BACK_TEXT) {
    override fun onClick(event: InventoryClickEvent, player: Player)
    {
        event.isCancelled = true
        currentPage.cancelCloseEvent = true
        currentPage.onBack(event, player)
        if(currentPage.backPage != null)
        {
            currentPage.backPage!!.open(player)
        } else
        {
            player.closeInventory()
        }
    }
}