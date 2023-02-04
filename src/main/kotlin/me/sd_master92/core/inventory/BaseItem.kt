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

    init
    {
        val meta = itemMeta
        if (meta != null)
        {
            if (name != null)
            {
                meta.setDisplayName(name)
            }
            if (lore != null)
            {
                meta.lore = null
                meta.lore = listOf(*lore.split(";".toRegex()).toTypedArray())
            }
            if (enchanted)
            {
                meta.addEnchant(Enchantment.LUCK, 1, true)
            }
            meta.addItemFlags(*ItemFlag.values())
            itemMeta = meta
        }
    }
}