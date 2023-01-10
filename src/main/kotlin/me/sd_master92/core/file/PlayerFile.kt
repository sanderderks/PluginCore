package me.sd_master92.core.file

import me.sd_master92.core.plugin.CustomPlugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.io.File
import java.util.*
import java.util.stream.Collectors

/**
 * create a new PlayerFile instance
 *
 * @param uuid   uuid of this player
 * @param plugin main plugin class
 */
open class PlayerFile private constructor(var uuid: String, private val plugin: CustomPlugin) :
    CustomFile(File(plugin.dataFolder.toString() + File.separator + "players"), "$uuid.yml", plugin)
{
    /**
     * create a new PlayerFile instance
     * (this will automatically save all possible player properties)
     *
     * @param player the player
     * @param plugin main plugin class
     */
    private constructor(player: Player, plugin: CustomPlugin) : this(player.uniqueId.toString(), plugin)
    {
        name = player.name
    }

    override fun delete(): Boolean
    {
        if(super.delete())
        {
            ALL.remove(uuid)
            return true
        }
        return false
    }

    /**
     * save first join timestamp
     */
    fun setFirstJoinTime()
    {
        if (getTimeStamp("first_join") == 0L)
        {
            setTimeStamp("first_join")
        }
    }

    /**
     * get first join timestamp
     */
    val firstJoinTime: Long
        get() = getTimeStamp("first_join")

    /**
     * get the name of the player
     *
     * @return name or "unknown"
     */
    override fun getName(): String
    {
        val name = getString("name")
        return name ?: "unknown"
    }

    /**
     * save the name of the player
     *
     * @param name player name
     * @return successful or not
     */
    fun setName(name: String): Boolean
    {
        set("name", name)
        return saveConfig()
    }

    private fun init()
    {
        ALL[uuid] = this
    }

    companion object
    {
        private var ALL: MutableMap<String, PlayerFile> = HashMap()

        fun init(plugin: CustomPlugin)
        {
            val files = File(plugin.dataFolder.toString() + File.separator + "players").listFiles()
            ALL = if (files != null)
            {
                Arrays.stream(files).map { file: File -> PlayerFile(file.name.replace(".yml", ""), plugin) }
                    .collect(Collectors.toList()).associateBy { file -> file.uuid }.toMutableMap()
            } else HashMap()
        }

        /**
         * get an existing player file
         *
         * @param uuid   player uuid
         * @param plugin main plugin class
         * @return PlayerFile or null
         */
        fun getByUuid(plugin: CustomPlugin, uuid: String): PlayerFile
        {
            return ALL.getOrDefault(uuid, PlayerFile(uuid, plugin))
        }

        /**
         * get an existing player file
         *
         * @param plugin main plugin class
         * @param player player
         * @return PlayerFile or null
         */
        fun getByUuid(plugin: CustomPlugin, player: Player): PlayerFile
        {
            return ALL.getOrDefault(player.uniqueId.toString(), PlayerFile(player, plugin))
        }

        /**
         * get an existing player file
         *
         * @param plugin main plugin class
         * @param player player
         * @return PlayerFile or null
         */
        fun getByName(plugin: CustomPlugin, player: Player): PlayerFile
        {
            return ALL.values.firstOrNull { file -> file.name == player.name } ?: PlayerFile(player, plugin)
        }

        /**
         * get a PlayerFile by player name
         *
         * @param name   player name
         * @return PlayerFile or null
         */
        fun getByName(name: String): PlayerFile?
        {
            return ALL.values.firstOrNull { file -> file.name == name }
        }

        /**
         * get all PlayerFiles
         *
         * @return empty or filled list of PlayerFiles
         */
        fun getAll(): Map<String, PlayerFile>
        {
            return ALL
        }
    }

    init
    {
        setFirstJoinTime()
        init()
    }
}