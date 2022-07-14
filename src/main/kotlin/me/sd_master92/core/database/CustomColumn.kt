package me.sd_master92.core.database

import me.sd_master92.core.setValue

class CustomColumn(val database: CustomDatabase, val table: CustomTable, val name: String)
{
    fun exists(): Boolean
    {
        return try
        {
            database.connection!!.metaData.getColumns(null, null, table.name, name).next()
        } catch (e: Exception)
        {
            database.error(e)
            false
        }
    }

    fun create(dataType: DataType): Boolean
    {
        val statement = database.connection!!.prepareStatement("ALTER TABLE ${table.name} ADD COLUMN $name ${dataType.value}")
        return database.execute(statement)
    }

    fun createIFNotExists(dataType: DataType): Boolean
    {
        return if (!exists())
        {
            create(dataType)
        } else true
    }

    fun delete(): Boolean
    {
        val statement = database.connection!!.prepareStatement("ALTER TABLE ${table.name} DROP COLUMN $name")
        return database.execute(statement)
    }

    fun insertData(value: Any): Boolean
    {
        val statement = database.connection!!.prepareStatement("INSERT INTO ${table.name} (?) VALUES (?)")
        statement.setString(1, name)
        statement.setValue(2, value)
        return database.execute(statement)
    }

    enum class DataType(val value: String)
    {
        INT_PRIMARY("int"),
        VARCHAR_PRIMARY("varchar(255) PRIMARY KEY"),
        INT("int"),
        BOOLEAN("bool"),
        LONG("bigint"),
        VARCHAR("varchar(255)");

    }
}