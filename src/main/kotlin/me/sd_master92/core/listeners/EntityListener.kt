package me.sd_master92.core.listeners

import me.sd_master92.core.events.EntityDamageByPlayerEvent
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class EntityListener : Listener
{
    @EventHandler
    fun onEntityDamage(event: EntityDamageByEntityEvent)
    {
        if (event.damager is Player)
        {
            Bukkit.getPluginManager().callEvent(EntityDamageByPlayerEvent(event, event.damager as Player, false))
        } else if (event.damager is Projectile && (event.damager as Projectile).shooter is Player)
        {
            Bukkit.getPluginManager()
                .callEvent(EntityDamageByPlayerEvent(event, (event.damager as Projectile).shooter as Player, true))
        }
    }
}