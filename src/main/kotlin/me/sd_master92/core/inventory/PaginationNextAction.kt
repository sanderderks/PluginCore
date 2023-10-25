package me.sd_master92.core.inventory

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

abstract class PaginationNextAction(
    private val currentPage: GUI,
    private val page: Int,
    nextText: String
) : BaseItem(Material.FEATHER, nextText)
{
    abstract fun onNext(player: Player, newPage: Int)

    override fun onClick(event: InventoryClickEvent, player: Player)
    {
        event.isCancelled = true
        currentPage.cancelCloseEvent = true
        onNext(player, page + 1)
    }
}