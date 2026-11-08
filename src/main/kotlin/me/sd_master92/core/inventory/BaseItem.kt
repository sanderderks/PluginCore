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
        val meta = itemMeta ?: return
        meta.setDisplayName(name)
        meta.addItemFlags(*ItemFlag.entries.toTypedArray())
        itemMeta = meta
    }

    fun setLore(lore: String)
    {
        val meta = itemMeta ?: return
        meta.lore = listOf(*lore.split(";".toRegex()).toTypedArray())
        meta.addItemFlags(*ItemFlag.entries.toTypedArray())
        itemMeta = meta
    }

    fun addLore(lore: String)
    {
        val meta = itemMeta ?: return
        val newLore = if(meta.hasLore()) meta.lore!!.toMutableList() else mutableListOf()
        newLore.addAll(listOf(*lore.split(";".toRegex()).toTypedArray()))
        meta.lore = newLore
        meta.addItemFlags(*ItemFlag.entries.toTypedArray())
        itemMeta = meta
    }

    fun setEnchanted()
    {
        val meta = itemMeta ?: return
        meta.addEnchant(Enchantment.LUCK, 1, true)
        meta.addItemFlags(*ItemFlag.entries.toTypedArray())
        itemMeta = meta
    }

    init
    {
        val meta = itemMeta
        if(meta != null)
        {
            name?.let { meta.setDisplayName(it) }
            lore?.let { meta.lore = it.split(";".toRegex()) }
            if(enchanted) { meta.addEnchant(Enchantment.LUCK, 1, true) }
            
            meta.addItemFlags(*ItemFlag.entries.toTypedArray())
            itemMeta = meta
        }
    }
}