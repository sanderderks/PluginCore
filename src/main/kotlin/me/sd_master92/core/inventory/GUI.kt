package me.sd_master92.core.inventory

import me.sd_master92.core.lastEmpty
import me.sd_master92.core.plugin.CustomPlugin
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryEvent
import org.bukkit.inventory.ItemStack

abstract class GUI @JvmOverloads constructor(
    plugin: CustomPlugin,
    var backPage: GUI?,
    val title: String,
    size: (context: GUI) -> Int = { 9 },
    private val alwaysCancelEvent: Boolean = true,
    val hasSaveButton: Boolean = false
) : Listener
{
    val clickableItems = mutableMapOf<Int, BaseItem>()
    var cancelCloseEvent = false
    var keepAlive = false
    val initSize = if (hasSaveButton) 1 else 0 + if (backPage != null) 1 else 0
    private val inventory =
        Bukkit.createInventory(null, size(this), ChatColor.stripColor(title)!!)

    val contents: Array<ItemStack?> get() = inventory.contents
    val size get() = inventory.size

    val clickableSize get() = clickableItems.size
    val nonClickableSize get() = contents.filterNotNull().size - clickableSize
    val nonClickableSizeWithNull get() = contents.size - clickableSize

    abstract fun newInstance(): GUI

    /**
     * Called when any non-null item is clicked
     */
    abstract fun onClick(event: InventoryClickEvent, player: Player)

    /**
     * Called when this inventory is closed
     */
    abstract fun onClose(event: InventoryCloseEvent, player: Player)

    /**
     * Click and close event are always cancelled
     * and the previous page is automatically opened if available
     * Else this inventory is force closed
     */
    abstract fun onBack(event: InventoryClickEvent, player: Player)

    /**
     * Click and close event are always cancelled
     * and the previous page is automatically opened if available
     * Else this inventory is force closed
     */
    abstract fun onSave(event: InventoryClickEvent, player: Player)

    @EventHandler
    open fun onInventoryClick(event: InventoryClickEvent)
    {
        if (isThisInventory(event))
        {
            if (alwaysCancelEvent)
            {
                event.isCancelled = true
            }
            if (event.currentItem != null)
            {
                onClick(event, event.whoClicked as Player)
                clickableItems[event.slot]?.onClick(event, event.whoClicked as Player)
            }
        } else if (event.view.title == title && event.isShiftClick && alwaysCancelEvent)
        {
            event.isCancelled = true
        }
    }

    @EventHandler
    open fun onInventoryClose(event: InventoryCloseEvent)
    {
        if (isThisInventory(event) && !cancelCloseEvent)
        {
            if (!keepAlive)
            {
                HandlerList.unregisterAll(this)
            }
            onClose(event, event.player as Player)
        }
    }

    @EventHandler
    open fun onDrag(event: InventoryDragEvent)
    {
        if (isThisInventory(event) && alwaysCancelEvent)
        {
            event.isCancelled = true
        }
    }

    fun isThisInventory(event: InventoryEvent): Boolean
    {
        return if (event is InventoryClickEvent)
        {
            event.clickedInventory === inventory
        } else event.inventory === inventory
    }

    fun addItem(item: BaseItem, end: Boolean = false)
    {
        val slot = if (end) inventory.lastEmpty() else inventory.firstEmpty()
        if (slot != null)
        {
            inventory.setItem(slot, item)
            clickableItems[slot] = item
        }
    }

    fun addItem(item: ItemStack, stack: Boolean = true, end: Boolean = false)
    {
        if (stack)
        {
            inventory.addItem(item)
        } else
        {
            val slot = if (end) inventory.lastEmpty() else inventory.firstEmpty()
            if (slot != null)
            {
                inventory.setItem(slot, item)
            }
        }
    }

    fun setItem(slot: Int, item: BaseItem)
    {
        inventory.setItem(slot, item)
        clickableItems[slot] = item
    }

    fun setItem(slot: Int, item: ItemStack)
    {
        inventory.setItem(slot, item)
    }

    fun clear()
    {
        inventory.clear()
        clickableItems.clear()
    }

    fun open(player: Player)
    {
        cancelCloseEvent = false
        player.openInventory(inventory)
    }

    private fun init(plugin: CustomPlugin)
    {
        if (backPage != null)
        {
            setItem(inventory.size - if (hasSaveButton) 2 else 1, BackButton(this))
        }
        if (hasSaveButton)
        {
            setItem(inventory.size - 1, SaveButton(this))
        }
        plugin.registerListener(this)
    }

    init
    {
        init(plugin)
    }
}