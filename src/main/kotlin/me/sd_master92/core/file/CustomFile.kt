package me.sd_master92.core.file

import me.sd_master92.core.plugin.CustomPlugin
import me.sd_master92.core.translateAlternateColorCodes
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import java.io.File

/**
 * create a new CustomFile
 *
 * @param folder folder of this CustomFile
 * @param name   name of this CustomFile
 * @param plugin main plugin class
 */
open class CustomFile(folder: File, name: String, plugin: CustomPlugin) : YamlConfiguration()
{
    private var file: File? = null

    /**
     * create a new CustomFile
     *
     * @param name   name of this CustomFile
     * @param plugin main plugin class
     */
    constructor(name: String, plugin: CustomPlugin) : this(plugin.dataFolder, name, plugin)

    /**
     * save config
     *
     * @return successful or not
     */
    fun saveConfig(): Boolean
    {
        return try
        {
            save(file!!)
            true
        } catch (e: Exception)
        {
            false
        }
    }

    /**
     * reload config
     *
     * @return successful or not
     */
    fun reloadConfig(): Boolean
    {
        return try
        {
            load(file!!)
            true
        } catch (e: Exception)
        {
            false
        }
    }

    /**
     * delete this file
     *
     * @return successful or not
     */
    open fun delete(): Boolean
    {
        return file!!.delete()
    }

    /**
     * delete a path from config
     *
     * @param path config path
     * @return successful or not
     */
    fun delete(path: String): Boolean
    {
        set(path.lowercase(), null)
        return saveConfig()
    }

    /**
     * get a timestamp
     *
     * @param path config path
     * @return number
     */
    fun getTimeStamp(path: String): Long
    {
        return getLong(path.lowercase())
    }

    /**
     * save a timestamp
     *
     * @param path config path
     * @return successful or not
     */
    fun setTimeStamp(path: String): Boolean
    {
        set(path.lowercase(), System.currentTimeMillis())
        return saveConfig()
    }

    /**
     * get a number
     *
     * @param path config path
     * @return number
     */
    fun getNumber(path: String): Int
    {
        return getInt(path.lowercase())
    }

    /**
     * save a number
     *
     * @param path   config path
     * @param number number to save
     * @return successful or not
     */
    fun setNumber(path: String, number: Int): Boolean
    {
        set(path.lowercase(), number)
        return saveConfig()
    }

    @JvmOverloads
    fun addNumber(path: String, add: Int = 1): Boolean
    {
        set(path.lowercase(), getInt(path.lowercase()) + add)
        return saveConfig()
    }

    /**
     * get a location
     *
     * @param path config path
     * @return location or null
     */
    override fun getLocation(path: String): Location?
    {
        return getObject("locations." + path.lowercase(), Location::class.java, null)
    }

    /**
     * save a location
     *
     * @param path config path
     * @param loc  location to save
     * @return successful or not
     */
    fun setLocation(path: String, loc: Location): Boolean
    {
        set("locations." + path.lowercase(), loc)
        return saveConfig()
    }

    /**
     * delete a location
     *
     * @param path config path
     * @return successful or not
     */
    fun deleteLocation(path: String): Boolean
    {
        return delete("locations." + path.lowercase())
    }

    /**
     * get a list of locations (e.g. a player's homes)
     *
     * @param path config path
     * @return empty or filled map of name -> locations
     */
    fun getLocations(path: String): Map<String, Location>
    {
        val locations = HashMap<String, Location>()
        val section = getConfigurationSection("locations." + path.lowercase())
        if (section != null)
        {
            for (key in section.getKeys(false))
            {
                val loc = getLocation(path.lowercase() + "." + key)
                if (loc != null)
                {
                    locations[key] = loc
                }
            }
        }
        return locations
    }

    /**
     * get items
     *
     * @param path config path
     * @return empty or filled array of items
     */
    fun getItems(path: String): Array<ItemStack>
    {
        val list = getList("items." + path.lowercase())
        if (list != null && list.isNotEmpty())
        {
            if (list[0] is ItemStack)
            {
                val items = list as List<ItemStack>
                return items.toTypedArray()
            }
        }
        return arrayOf()
    }

    /**
     * get items
     *
     * @param path config path
     * @return empty or filled array of items
     */
    fun getItemsWithPagination(path: String, page: Int, size: Int): Array<ItemStack>
    {
        val start = page * size
        return getItems(path).filterIndexed { i, _ -> i in start until (start + size) }.toTypedArray()
    }

    /**
     * save items
     *
     * @param path  config path
     * @param items items to save
     * @return successful or not
     */
    fun setItems(path: String, items: Array<ItemStack?>): Boolean
    {
        set("items." + path.lowercase(), items.filterNotNull())
        return saveConfig()
    }

    /**
     * save items with pagination
     *
     * @param path  config path
     * @param items items to save
     * @return successful or not
     */
    fun setItemsWithPagination(path: String, items: Array<ItemStack?>, page: Int, size: Int): Boolean
    {
        val originalItems = getItems(path).toMutableList()
        if (originalItems.size <= page * size)
        {
            originalItems.addAll(items.filterNotNull())
        } else
        {
            val start = page * size
            val end = minOf(start + size, originalItems.size)
            originalItems.subList(start, end).clear()
            originalItems.addAll(start, items.filterNotNull())
        }
        return setItems(path, originalItems.toTypedArray())
    }


    /**
     * delete items
     *
     * @param path config path
     * @return successful or not
     */
    fun deleteItems(path: String): Boolean
    {
        return delete("items." + path.lowercase())
    }

    /**
     * get a message
     *
     * @param path         config path
     * @param placeholders placeholders to replace in the message
     * @return empty or filled string
     */
    @JvmOverloads
    fun getMessage(path: String, placeholders: Map<String, String> = HashMap()): String
    {
        var message = getString(path.lowercase())
        if (message != null)
        {
            message = message.translateAlternateColorCodes()
            for (placeholder in placeholders.keys)
            {
                message = message!!.replace(placeholder, placeholders[placeholder]!!)
            }
            return message!!
        }
        return ""
    }

    /**
     * get a list of messages
     *
     * @param path         config path
     * @param placeholders placeholders to replace in the messages
     * @param replaceFirst replace only the first occurrence of a placeholder
     * @return empty or filled list of messages
     */
    @JvmOverloads
    fun getMessages(
        path: String,
        placeholders: Map<String, String> = HashMap(),
        replaceFirst: Boolean = false
    ): List<String>
    {
        val messages = getStringList(path.lowercase())
        for (i in messages.indices)
        {
            var message = messages[i].translateAlternateColorCodes()
            for (placeholder in placeholders.keys)
            {
                message = if (replaceFirst)
                {
                    message.replaceFirst(placeholder.toRegex(), placeholders[placeholder]!!)
                } else
                {
                    message.replace(placeholder, placeholders[placeholder]!!)
                }
            }
            messages[i] = message
        }
        return messages
    }

    fun keyMigrations(migrations: Map<String, String>)
    {
        for (migration in migrations)
        {
            if (!contains(migration.value) && contains(migration.key))
            {
                set(migration.value, get(migration.key))
                delete(migration.key)
            }
        }
    }

    fun valueMigrations(migrations: Map<String, String>)
    {
        for (key in getKeys(true))
        {
            val value = getString(key)
            for (migration in migrations)
            {
                if (value?.contains(migration.key) == true)
                {
                    set(key, value.replace(migration.key, migration.value))
                }
            }
        }
        saveConfig()
    }

    init
    {
        try
        {
            if (!folder.exists() && !folder.mkdirs())
            {
                throw Exception("Could not generate folder")
            }
            file = File(folder, name.lowercase())
            if (!file!!.exists())
            {
                try
                {
                    plugin.saveResource(name.lowercase(), false)
                } catch (e: Exception)
                {
                    if (!file!!.createNewFile())
                    {
                        throw Exception("Could not generate file '" + name.lowercase() + "'")
                    }
                }
            }
            load(file!!)
        } catch (e: Exception)
        {
            e.printStackTrace()
        }
    }
}