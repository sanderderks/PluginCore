package me.sd_master92.core.plugin

import me.sd_master92.core.errorLog
import me.sd_master92.core.file.CustomFile
import me.sd_master92.core.infoLog
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection

abstract class CustomPlugin @JvmOverloads constructor(
    private val configName: String = "config.yml",
    val spigot: Int = 0
) : JavaPlugin()
{
    lateinit var config: CustomFile
    val pluginName = description.name
    val version = description.version
    val author = description.authors[0] ?: "sd_master92"

    private var versionLastChecked: Calendar? = null

    protected abstract fun enable()
    protected abstract fun disable()

    override fun onEnable()
    {
        infoLog("")
        infoLog("                      $pluginName")
        infoLog(">----------------------------------------------------")
        infoLog("                     By $author")
        if (spigot > 0)
        {
            checkUpdates()
        }
        config = CustomFile(configName, this)
        enable()
        if (isEnabled)
        {
            infoLog("")
            infoLog(ChatColor.GREEN.toString() + "v$version has been enabled.")
            infoLog("")
            infoLog(">----------------------------------------------------")
        }
    }

    override fun onDisable()
    {
        disable()
        infoLog("")
        infoLog(ChatColor.RED.toString() + "v$version has been disabled.")
        infoLog("")
        infoLog(">----------------------------------------------------")
    }

    fun sendDownloadUrl(player: Player)
    {
        player.sendMessage(
            ChatColor.GRAY.toString() + "Download " + ChatColor.LIGHT_PURPLE +
                    "CustomVoting " + ChatColor.GRAY + "v$latestVersion:"
        )
        player.sendMessage(ChatColor.GREEN.toString() + "https://www.spigotmc.org/resources/$spigot/")
    }

    private fun checkUpdates()
    {
        infoLog("")
        infoLog("| checking for updates")
        infoLog("|")
        if (versionStatus().isValid())
        {
            infoLog("|___up to date!")
        } else
        {
            errorLog("|   a new version is available")
            errorLog("|   download $pluginName v${latestVersion} at:")
            errorLog("|___https://www.spigotmc.org/resources/$spigot/")
        }
    }

    fun registerListener(listener: Listener)
    {
        server.pluginManager.registerEvents(listener, this)
    }

    enum class VersionStatus
    {
        OUTDATED,
        LATEST,
        BETA;

        fun isValid(): Boolean
        {
            return this == LATEST || this == BETA
        }
    }

    fun versionStatus(): VersionStatus
    {
        try
        {
            val subVersions = version.split(".").map { it.toInt() }
            val latestSubVersions = latestVersion.split(".").map { it.toInt() }
            subVersions.forEachIndexed { i, subVersion ->
                val latestSubVersion = latestSubVersions[i]
                if (subVersion > latestSubVersion)
                {
                    return VersionStatus.BETA
                } else if (subVersion < latestSubVersion)
                {
                    return VersionStatus.OUTDATED
                }
            }
        } catch (_: Exception)
        {
            return VersionStatus.OUTDATED
        }
        return VersionStatus.LATEST
    }

    var latestVersion: String = "1.0"
        private set
        get()
        {
            if (versionLastChecked == null
                || Calendar.getInstance()[Calendar.DAY_OF_YEAR] != versionLastChecked!![Calendar.DAY_OF_YEAR]
                || Calendar.getInstance()[Calendar.HOUR_OF_DAY] - versionLastChecked!![Calendar.HOUR_OF_DAY] >= 1
            )
            {
                latestVersion = try
                {
                    val connection =
                        URL("https://api.spigotmc.org/legacy/update.php?resource=$spigot").openConnection() as HttpsURLConnection
                    connection.requestMethod = "GET"
                    BufferedReader(InputStreamReader(connection.inputStream)).readLine()
                } catch (e: Exception)
                {
                    "1.0"
                }
                versionLastChecked = Calendar.getInstance()
            }
            return field;
        }

    companion object
    {
        var SAVE_TEXT = ChatColor.GREEN.toString() + "Save"
        var BACK_TEXT = ChatColor.RED.toString() + "Back"
        var STATUS_TEXT = ChatColor.GRAY.toString() + "Status:"
        var ON_TEXT = ChatColor.GREEN.toString() + "ON"
        var OFF_TEXT = ChatColor.RED.toString() + "OFF"
    }
}