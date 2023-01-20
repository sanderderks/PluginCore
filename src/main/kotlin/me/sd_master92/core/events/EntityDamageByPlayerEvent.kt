package me.sd_master92.core.events

import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.entity.EntityDamageByEntityEvent

class EntityDamageByPlayerEvent(
    event: EntityDamageByEntityEvent,
    val player: Player,
    val isIndirect: Boolean
) :
    EntityDamageByEntityEvent(
        event.damager,
        event.entity,
        event.cause,
        event.damage
    )
{
    override fun getHandlers(): HandlerList
    {
        return handlerList
    }

    companion object
    {
        private val handlerList: HandlerList = HandlerList()
    }
}