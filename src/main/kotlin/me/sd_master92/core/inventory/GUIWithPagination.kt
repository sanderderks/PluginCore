package me.sd_master92.core.inventory

import me.sd_master92.core.plugin.CustomPlugin
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class GUIWithPagination<T>(
    plugin: CustomPlugin,
    backPage: GUI?,
    items: List<T>,
    getKey: (item: T) -> Int?,
    itemMapper: (context: GUIWithPagination<T>, item: T, key: Int) -> ItemStack,
    private val page: Int = 0,
    title: String,
    nextText: String,
    previousText: String,
    actions: ((context: GUI) -> List<BaseItem>) = { emptyList() },
    differentStartIndex: Int? = null
) :
    GUI(
        plugin,
        backPage,
        "$title | page ${page + 1}",
        { calculateInventorySize(items.size, page, actions(it).size + it.initSize, differentStartIndex) }
    )
{
    abstract fun newInstance(page: Int): GUI
    abstract fun onPaginate(player: Player, page: Int)

    companion object
    {
        private const val INVENTORY_ROW_SIZE = 9
        private const val MAX_INVENTORY_SIZE = 54

        private fun maxItemsOnPage(usedSlotsWithoutPagination: Int): Int
        {
            return (MAX_INVENTORY_SIZE - usedSlotsWithoutPagination)
        }

        private fun precedingItems(maxItemsOnPage: Int, page: Int, differentStartIndex: Int?): Int
        {
            var precedingItems = 0
            if (page > 0)
            {
                precedingItems += maxItemsOnPage * page - (differentStartIndex ?: 0) - 1 - (2 * (page - 1))
            }
            return precedingItems
        }

        private fun calculateStartIndex(usedSlotsWithoutPagination: Int, page: Int, differentStartIndex: Int?): Int
        {
            return if (page == 0) differentStartIndex ?: 0 else precedingItems(
                maxItemsOnPage(usedSlotsWithoutPagination),
                page,
                differentStartIndex ?: 0
            )
        }

        private fun calculateEndIndex(
            inventorySize: Int,
            usedSlots: Int,
            startIndex: Int,
            page: Int,
            differentStartIndex: Int?
        ): Int
        {
            return startIndex + inventorySize - usedSlots - (if (page == 0) differentStartIndex ?: 0 else 0)
        }

        private fun hasNextPage(
            totalItemSize: Int,
            inventorySize: Int,
            endIndex: Int,
            differentStartIndex: Int?
        ): Boolean
        {
            return inventorySize == MAX_INVENTORY_SIZE && endIndex - (differentStartIndex ?: 0) < totalItemSize - 1
        }

        private fun hasPreviousPage(page: Int): Boolean
        {
            return page > 0
        }

        private fun calculateInventorySize(
            totalItemSize: Int,
            page: Int,
            otherButtonsSize: Int,
            differentStartIndex: Int? = null
        ): Int
        {
            val startIndex = calculateStartIndex(otherButtonsSize, page, differentStartIndex)
            val endIndex = calculateEndIndex(
                MAX_INVENTORY_SIZE,
                otherButtonsSize + 2,
                startIndex,
                page,
                differentStartIndex
            )
            if (hasNextPage(totalItemSize, MAX_INVENTORY_SIZE, endIndex, differentStartIndex))
            {
                return MAX_INVENTORY_SIZE
            }
            val maxItemsOnPage = maxItemsOnPage(otherButtonsSize)
            val precedingItems = precedingItems(maxItemsOnPage, page, differentStartIndex)
            val itemSize =
                totalItemSize - precedingItems + otherButtonsSize + if (hasPreviousPage(page)) 1 else 0
            var inventorySize =
                if (itemSize % INVENTORY_ROW_SIZE == 0) itemSize else itemSize + (INVENTORY_ROW_SIZE - (itemSize % INVENTORY_ROW_SIZE))
            if (page == 0 && differentStartIndex != null)
            {
                inventorySize += differentStartIndex
            }
            return inventorySize.coerceAtLeast(INVENTORY_ROW_SIZE).coerceAtMost(MAX_INVENTORY_SIZE)
        }
    }

    init
    {
        for (action in actions(this))
        {
            addItem(action, true)
        }
        val usedSlotsWithoutPagination = clickableSize
        var usedSlots = 2 + clickableSize
        var startIndex = calculateStartIndex(usedSlotsWithoutPagination, page, differentStartIndex)
        val hasPreviousPage = hasPreviousPage(page)
        if (!hasPreviousPage)
        {
            usedSlots--
        }
        var endIndex = calculateEndIndex(size, usedSlots, startIndex, page, differentStartIndex)
        val hasNextPage = hasNextPage(items.size, size, endIndex, differentStartIndex)
        if (hasNextPage)
        {
            addItem(
                object : PaginationNextAction(this, page, nextText)
                {
                    override fun onNext(player: Player, newPage: Int)
                    {
                        cancelCloseEvent = true
                        onPaginate(player, newPage)
                        newInstance(newPage).open(player)
                    }
                }, true
            )
        } else
        {
            usedSlots--
            endIndex = calculateEndIndex(size, usedSlots, startIndex, page, differentStartIndex)
        }
        if (hasPreviousPage)
        {
            addItem(object : PaginationPreviousAction(this, page, previousText)
            {
                override fun onPrevious(player: Player, newPage: Int)
                {
                    cancelCloseEvent = true
                    onPaginate(player, newPage)
                    newInstance(newPage).open(player)
                }
            }, true)
        }

        if (page == 0)
        {
            startIndex -= differentStartIndex ?: 0
            endIndex -= differentStartIndex ?: 0
        }

        val filteredItems = items
                .mapNotNull { item -> getKey(item)?.let { Pair(item, it) } }
                .sortedBy { (_, key) -> key }
                .filterIndexed { i, _ -> i in startIndex until endIndex }

        val skip = if (page == 0) differentStartIndex else null

        for ((item, key) in filteredItems)
        {
            val mappedItem = itemMapper(this, item, key)
            if (mappedItem is BaseItem)
            {
                addItem(mappedItem, skip = skip)
            } else
            {
                addItem(mappedItem, skip = skip)
            }
        }
    }
}
