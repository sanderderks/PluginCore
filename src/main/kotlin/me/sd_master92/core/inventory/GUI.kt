package me.sd_master92.core.inventory

import me.sd_master92.core.plugin.CustomPlugin
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Item
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
    val name: String,
    size: Int = 9,
    private val alwaysCancelEvent: Boolean = true,
    val back: Boolean = true,
    val save: Boolean = false
) : Listener
{
    private val inventory = Bukkit.createInventory(null, size, ChatColor.stripColor(name)!!)
    val items = mutableMapOf<Int,BaseItem>()
    var cancelCloseEvent = false
    var keepAlive = false

    val size get() = inventory.size
    val contents get() = inventory.contents

    abstract fun onClick(event: InventoryClickEvent, player: Player)
    abstract fun onClose(event: InventoryCloseEvent, player: Player)
    abstract fun onBack(event: InventoryClickEvent, player: Player)
    abstract fun onSave(event: InventoryClickEvent, player: Player)

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent)
    {
        if (isThisInventory(event))
        {
            if (alwaysCancelEvent)
            {
                event.isCancelled = true
            }
            if(event.currentItem != null)
            {
                onClick(event, event.whoClicked as Player)
                items[event.slot]?.onClick(event, event.whoClicked as Player)
            }
        }
    }

    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent)
    {
        if (isThisInventory(event) && !cancelCloseEvent)
        {
            if(!keepAlive)
            {
                HandlerList.unregisterAll(this)
            }
            onClose(event, event.player as Player)
        }
    }

    @EventHandler
    fun onDrag(event: InventoryDragEvent)
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

    fun addItem(item: BaseItem)
    {
        val slot = inventory.firstEmpty()
        inventory.setItem(slot, item)
        items[slot] = item
    }

    fun addItem(item: ItemStack, stack: Boolean = true)
    {
        if(stack)
        {
            inventory.addItem(item)
        } else
        {
            inventory.setItem(inventory.firstEmpty(), item)
        }
    }

    fun setItem(slot: Int, item: BaseItem)
    {
        inventory.setItem(slot, item)
        items[slot] = item
    }

    fun setItem(slot: Int, item: ItemStack)
    {
        inventory.setItem(slot, item)
    }

    fun clear()
    {
        inventory.clear()
        items.clear()
    }

    fun open(player: Player)
    {
        player.openInventory(inventory)
    }

    private fun init(plugin: CustomPlugin)
    {
        plugin.registerListener(this)
    }

    init
    {
        if (back)
        {
            setItem(inventory.size - if (save) 2 else 1, object : BaseItem(Material.BARRIER, ChatColor.RED.toString() + "Back") {
                override fun onClick(event: InventoryClickEvent, player: Player)
                {
                    onBack(event, player)
                }
            })
        }
        if(save)
        {
            setItem(inventory.size - 1, object : BaseItem(Material.WRITABLE_BOOK, ChatColor.GREEN.toString() + "Save") {
                override fun onClick(event: InventoryClickEvent, player: Player)
                {
                    onSave(event, player)
                }
            })
        }
        init(plugin)
    }
}