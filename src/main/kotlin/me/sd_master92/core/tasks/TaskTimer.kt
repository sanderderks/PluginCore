package me.sd_master92.core.tasks

import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scheduler.BukkitTask

class TaskTimer private constructor(
    private val plugin: Plugin,
    private val delay: Long = 0,
    private val time: Long = -1,
    var count: Int = -1,
    private val task: (TaskTimer) -> Unit
)
{
    private var bukkitTask: BukkitTask? = null
    private var next: TaskTimer? = null
    private val self = this

    fun run(): TaskTimer
    {
        if (time < 0)
        {
            bukkitTask = object : BukkitRunnable()
            {
                override fun run()
                {
                    task(self)
                    self.cancel()
                }
            }.runTaskLater(plugin, delay)
        } else
        {
            bukkitTask = object : BukkitRunnable()
            {
                override fun run()
                {
                    task(self)
                    if (count == 0)
                    {
                        self.cancel()
                    }
                    count--
                }
            }.runTaskTimer(plugin, delay, time)
        }
        return this
    }

    fun then(task: TaskTimer): TaskTimer
    {
        next = task
        return task
    }

    fun cancel()
    {
        bukkitTask?.cancel()
        next?.run()
    }

    companion object
    {
        fun delay(plugin: Plugin, delay: Long = 1, task: (TaskTimer) -> Unit): TaskTimer
        {
            return TaskTimer(plugin, delay, task = task)
        }

        fun repeat(
            plugin: Plugin,
            time: Long = 1,
            delay: Long = 0,
            count: Int = -1,
            task: (TaskTimer) -> Unit
        ): TaskTimer
        {
            return TaskTimer(plugin, delay, time, count, task)
        }
    }
}
