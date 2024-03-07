package me.sd_master92.core.inventory

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

abstract class PaginationPreviousAction(private val currentPage: GUI, private val page: Int, previousText: String) :
    BaseItem(Material.FEATHER, previousText)
{
    abstract fun onPrevious(player: Player, newPage: Int)

    override fun onClick(event: InventoryClickEvent, player: Player)
    {
        event.isCancelled = true
        if (page > 0)
        {
            currentPage.cancelCloseEvent = true
            onPrevious(player, page - 1)
        }
    }
}