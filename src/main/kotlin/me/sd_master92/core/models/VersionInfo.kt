package me.sd_master92.core.models

import me.sd_master92.core.plugin.CustomPlugin
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class VersionInfo(plugin: CustomPlugin)
{
    var upToDate = false
        private set
    var latestVersion = "1.0"
        private set

    init
    {
        this.latestVersion = try
        {
            val connection =
                URL("https://api.spigotmc.org/legacy/update.php?resource=${plugin.spigot}").openConnection() as HttpsURLConnection
            connection.requestMethod = "GET"
            BufferedReader(InputStreamReader(connection.inputStream)).readLine()
        } catch (e: Exception)
        {
            "1.0"
        }
        upToDate = CustomPlugin.VERSION.equals(this.latestVersion, true)
    }
}