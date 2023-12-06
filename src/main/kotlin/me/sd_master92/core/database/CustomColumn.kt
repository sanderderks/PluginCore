package me.sd_master92.core.database

import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext

class CustomColumn(val database: CustomDatabase, val table: CustomTable, var name: String)
{
    suspend fun exists(): Boolean
    {
        return withContext(SupervisorJob())
        {
            try
            {
                database.connection!!.metaData.getColumns(null, null, table.name, name).next()
            } catch (e: Exception)
            {
                database.error(e)
                false
            }
        }
    }

    suspend fun create(dataType: DataType): Boolean
    {
        val statement =
            database.connection!!.prepareStatement("ALTER TABLE ${table.name} ADD COLUMN $name ${dataType.value}")
        return database.executeAsync(statement)
    }

    suspend fun createIFNotExists(dataType: DataType): Boolean
    {
        return if (!exists())
        {
            create(dataType)
        } else true
    }

    suspend fun renameOrCreate(newName: String, dataType: DataType): Boolean
    {
        val statement =
            database.connection!!.prepareStatement("ALTER TABLE ${table.name} CHANGE $name $newName ${dataType.value}")
        return if (database.executeAsync(statement))
        {
            name = newName
            true
        } else
        {
            createIFNotExists(dataType)
        }
    }

    suspend fun delete(): Boolean
    {
        return if (exists())
        {
            val statement = database.connection!!.prepareStatement("ALTER TABLE ${table.name} DROP COLUMN $name")
            database.executeAsync(statement)
        } else
        {
            true
        }
    }

    enum class DataType(val value: String)
    {
        INT_PRIMARY("int PRIMARY KEY AUTO_INCREMENT"),
        VARCHAR_PRIMARY("varchar(100) PRIMARY KEY"),
        INT("int"),
        BOOLEAN("bool"),
        LONG("bigint"),
        VARCHAR("varchar(255)");

    }
}