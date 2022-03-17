package me.sd_master92.core.inventory

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

open class BaseItem(mat: Material, name: String? = null, lore: String? = null, enchanted: Boolean = false) : ItemStack(mat)
{
    constructor(mat: Material, enchanted: Boolean) : this(mat, null, null, enchanted)

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