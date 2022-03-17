package me.sd_master92.core.models

import me.sd_master92.core.plugin.CustomPlugin

class VersionInfo(plugin: CustomPlugin)
{
    var upToDate = false
        private set
    var latestVersion = "1.0"
        private set
    init
    {
        latestVersion = plugin.getLatestVersion()
        upToDate = CustomPlugin.VERSION.equals(latestVersion, true)
    }
}