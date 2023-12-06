package me.sd_master92.core.database

import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import me.sd_master92.core.file.CustomFile
import java.sql.*

class CustomDatabase
{
    private val database: String
    private val username: String
    private val password: String
    var connection: Connection? = null
        private set
    private var host: String

    constructor(host: String, database: String, username: String, password: String)
    {
        this.host = host
        this.database = database
        this.username = username
        this.password = password
    }

    constructor(config: CustomFile, path: String)
    {
        host = config.getString("$path.host") + ":" + config.getString("$path.port")
        database = config.getString("$path.database")!!
        username = config.getString("$path.user")!!
        password = config.getString("$path.password")!!
    }

    fun connect(): Boolean
    {
        if (!host.contains(":"))
        {
            host += ":3306"
        }
        return try
        {
            connection = DriverManager.getConnection(
                "jdbc:mysql://$host/$database?allowMultiQueries=true&useTimezone=true&serverTimezone=UTC",
                username,
                password
            )
            isConnected
        } catch (e: Exception)
        {
            false
        }
    }

    fun disconnect(): Boolean
    {
        return try
        {
            connection!!.close()
            true
        } catch (e: Exception)
        {
            false
        }
    }

    val isConnected: Boolean
        get() = try
        {
            connection != null && connection!!.isValid(3)
        } catch (e: SQLException)
        {
            false
        }

    suspend fun executeAsync(statement: PreparedStatement): Boolean
    {
        return withContext(SupervisorJob()) {
            try
            {
                statement.execute()
                true
            } catch (e: Exception)
            {
                print(statement)
                error(e)
                false
            }
        }
    }

    suspend fun queryAsync(statement: PreparedStatement): ResultSet?
    {
        return withContext(SupervisorJob()) {
            try
            {
                statement.executeQuery()
            } catch (e: Exception)
            {
                print(statement)
                error(e)
                null
            }
        }
    }

    fun getTable(name: String): CustomTable
    {
        return CustomTable(this, name)
    }

    fun print(text: String)
    {
        println(PREFIX + text)
    }

    fun error(e: Exception)
    {
        println(PREFIX + e.message)
        e.printStackTrace()
    }

    companion object
    {
        private const val PREFIX = "[PluginCore] "
    }
}