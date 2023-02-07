package me.sd_master92.core.inventory

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

abstract class BaseItem(mat: Material, name: String? = null, lore: String? = null, enchanted: Boolean = false) : ItemStack(mat)
{
    constructor(mat: Material, enchanted: Boolean) : this(mat, null, null, enchanted)

    abstract fun onClick(event: InventoryClickEvent, player: Player)

    fun setName(name: String)
    {
        val meta = itemMeta
        meta?.setDisplayName(name)
        itemMeta = meta
    }

    fun setLore(lore: String)
    {
        val meta = itemMeta
        if(meta != null)
        {
            meta.lore = null
            meta.lore = listOf(*lore.split(";".toRegex()).toTypedArray())
        }
        itemMeta = meta
    }

    fun addLore(lore: String)
    {
        val meta = itemMeta
        if(meta != null)
        {
            val newLore = if(meta.hasLore()) meta.lore!! else mutableListOf()
            newLore.addAll(listOf(*lore.split(";".toRegex()).toTypedArray()))
            meta.lore = newLore
        }
        itemMeta = meta
    }

    fun setEnchanted()
    {
        val meta = itemMeta
        meta?.addEnchant(Enchantment.LUCK, 1, true)
        itemMeta = meta
    }

    init
    {
        name?.let { setName(name) }
        lore?.let { setLore(lore) }
        if(enchanted) { setEnchanted() }

        val meta = itemMeta
        meta?.addItemFlags(*ItemFlag.values())
        itemMeta = meta
    }
}