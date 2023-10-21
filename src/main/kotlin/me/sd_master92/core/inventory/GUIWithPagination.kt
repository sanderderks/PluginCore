package me.sd_master92.core.inventory

import me.sd_master92.core.plugin.CustomPlugin
import org.bukkit.entity.Player

abstract class GUIWithPagination<T>(
    plugin: CustomPlugin,
    backPage: GUI?,
    items: List<T>,
    getItem: (context: GUIWithPagination<T>, item: T) -> BaseItem,
    private val page: Int = 0,
    name: String,
    nextText: String,
    previousText: String,
    otherButtonsSize: Int = 0
) :
    GUI(
        plugin,
        backPage,
        "$name | page ${page + 1}",
        calculateInventorySize(items.size, page, otherButtonsSize + if (backPage != null) 1 else 0)
    )
{
    abstract fun open(player: Player, page: Int)

    companion object
    {
        private const val INVENTORY_ROW_SIZE = 9
        private const val MAX_INVENTORY_SIZE = 54

        private fun maxItemsOnPage(usedSlotsWithoutPagination: Int): Int
        {
            return (MAX_INVENTORY_SIZE - usedSlotsWithoutPagination)
        }

        private fun precedingItems(maxItemsOnPage: Int, page: Int): Int
        {
            var precedingItems = 0
            if (page > 0)
            {
                precedingItems += maxItemsOnPage * page - 1 - (2 * (page - 1))
            }
            return precedingItems
        }

        private fun startIndex(usedSlotsWithoutPagination: Int, page: Int): Int
        {
            return if (page == 0) 0 else precedingItems(
                maxItemsOnPage(usedSlotsWithoutPagination),
                page
            )
        }

        private fun endIndex(inventorySize: Int, usedSlots: Int, startIndex: Int): Int
        {
            return startIndex + inventorySize - usedSlots
        }

        private fun hasNextPage(totalItemSize: Int, inventorySize: Int, endIndex: Int): Boolean
        {
            return inventorySize == MAX_INVENTORY_SIZE && endIndex < totalItemSize - 1
        }

        private fun hasPreviousPage(page: Int): Boolean
        {
            return page > 0
        }

        private fun calculateInventorySize(totalItemSize: Int, page: Int, otherButtonsSize: Int): Int
        {
            val startIndex = startIndex(otherButtonsSize, page)
            val endIndex = endIndex(MAX_INVENTORY_SIZE, otherButtonsSize + 2, startIndex)
            if (hasNextPage(totalItemSize, MAX_INVENTORY_SIZE, endIndex))
            {
                return MAX_INVENTORY_SIZE
            }
            val maxItemsOnPage = maxItemsOnPage(otherButtonsSize)
            val precedingItems = precedingItems(maxItemsOnPage, page)
            val itemSize = totalItemSize - precedingItems + otherButtonsSize + if (hasPreviousPage(page)) 1 else 0
            val inventorySize =
                if (itemSize % INVENTORY_ROW_SIZE == 0) itemSize else itemSize + (INVENTORY_ROW_SIZE - (itemSize % INVENTORY_ROW_SIZE))
            return inventorySize.coerceAtLeast(INVENTORY_ROW_SIZE)
        }
    }

    init
    {
        val usedSlotsWithoutPagination = otherButtonsSize + if (backPage != null) 1 else 0
        var usedSlots = 2 + usedSlotsWithoutPagination
        val hasPreviousPage = hasPreviousPage(page)
        if (!hasPreviousPage)
        {
            usedSlots--
        }
        val start = startIndex(usedSlotsWithoutPagination, page)
        var end = endIndex(size, usedSlots, start)
        val hasNextPage = hasNextPage(items.size, size, end)
        if (hasNextPage)
        {
            addItem(
                object : PaginationNextAction(this, page, nextText)
                {
                    override fun onNext(player: Player, newPage: Int)
                    {
                        cancelCloseEvent = true
                        open(player, newPage)
                    }
                }, true
            )
        } else
        {
            usedSlots--
            end = endIndex(size, usedSlots, start)
        }
        if (hasPreviousPage)
        {
            addItem(object : PaginationPreviousAction(this, page, previousText)
            {
                override fun onPrevious(player: Player, newPage: Int)
                {
                    cancelCloseEvent = true
                    open(player, newPage)
                }
            }, true)
        }
        val filteredItems = items.filterIndexed { i, _ -> i in start until end }
        for (item in filteredItems)
        {
            addItem(getItem(this, item))
        }
    }
}
