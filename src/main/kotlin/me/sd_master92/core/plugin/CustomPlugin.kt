package me.sd_master92.core.plugin

import me.sd_master92.core.file.CustomFile
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

abstract class CustomPlugin @JvmOverloads constructor(
    private val configName: String = "config.yml",
    private val spigot: Int = 0
) : JavaPlugin()
{
    lateinit var config: CustomFile

    protected abstract fun enable()
    protected abstract fun disable()

    override fun onEnable()
    {
        infoLog("")
        infoLog("                      $NAME")
        infoLog(">----------------------------------------------------")
        infoLog("                     By $AUTHOR")
        if (spigot > 0)
        {
            checkUpdates()
        }
        config = CustomFile(configName, this)
        enable()
        if (isEnabled)
        {
            infoLog("")
            infoLog(ChatColor.GREEN.toString() + "v" + VERSION + " has been enabled.")
            infoLog("")
            infoLog(">----------------------------------------------------")
        }
    }

    override fun onDisable()
    {
        disable()
        infoLog("")
        infoLog(ChatColor.RED.toString() + "v" + VERSION + " has been disabled.")
        infoLog("")
        infoLog(">----------------------------------------------------")
    }

    val resourceUrl: URL?
        get() = try
        {
            URL(
                "https://api.spigotmc.org/legacy/update" +
                        ".php?resource=" + spigot
            )
        } catch (e: Exception)
        {
            null
        }

    fun sendDownloadUrl(player: Player)
    {
        player.sendMessage(
            ChatColor.GRAY.toString() + "Download " + ChatColor.LIGHT_PURPLE +
                    "CustomVoting " + ChatColor.GRAY + "v" + latestVersion + ":"
        )
        player.sendMessage(ChatColor.GREEN.toString() + "https://www.spigotmc.org/resources/$spigot/")
    }

    val isUpToDate: Boolean
        get() = VERSION.equals(latestVersion, ignoreCase = true)
    val latestVersion: String?
        get() = try
        {
            val connection = resourceUrl!!.openConnection() as HttpsURLConnection
            connection.requestMethod = "GET"
            BufferedReader(InputStreamReader(connection.inputStream)).readLine()
        } catch (e: Exception)
        {
            null
        }

    private fun checkUpdates()
    {
        infoLog("")
        infoLog("| checking for updates")
        infoLog("|")
        val latestVersion = latestVersion
        if (latestVersion != null)
        {
            if (VERSION.equals(latestVersion, ignoreCase = true))
            {
                infoLog("|___up to date!")
            } else
            {
                errorLog("|   a new version is available")
                errorLog("|   download $NAME v$latestVersion at:")
                errorLog("|___https://www.spigotmc.org/resources/$spigot/")
            }
        } else
        {
            errorLog("|___could not check for updates")
        }
    }

    fun registerListener(listener: Listener)
    {
        server.pluginManager.registerEvents(listener, this)
    }

    fun infoLog(message: String)
    {
        server.consoleSender.sendMessage("[$NAME] $message")
    }

    fun errorLog(message: String, e: Exception? = null)
    {
        server.consoleSender.sendMessage(ChatColor.YELLOW.toString() + "[" + NAME + "] " + ChatColor.RESET + message)
        e?.let { println(it.toString()) }
    }

    companion object
    {
        var NAME: String = "Unknown"
            private set
        var VERSION: String = "1.0"
            private set
        var AUTHOR: String = "sd_master92"
            private set
    }

    init
    {
        NAME = description.name
        VERSION = description.version
        description.authors[0]?.let { AUTHOR = it }
    }
}