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
        val statement =
            database.connection!!.prepareStatement("ALTER TABLE ? ADD ? ?")
        statement.setString(1, table.name)
        statement.setString(2, name)
        statement.setString(3, dataType.value)
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
        val statement =
            database.connection!!.prepareStatement("ALTER TABLE ? DROP COLUMN ?")
        statement.setString(1, table.name)
        statement.setString(2, name)
        return database.execute(statement)
    }

    fun insertData(value: Any): Boolean
    {
        val statement =
            database.connection!!.prepareStatement("INSERT INTO ? (?) VALUES (?)")
        statement.setString(1, table.name)
        statement.setString(2, name)
        statement.setValue(3, value)
        return database.execute(statement)
    }

    enum class DataType(val value: String)
    {
        INT_PRIMARY("int"),
        VARCHAR_PRIMARY("varchar(255) PRIMARY KEY"),
        INT("int"),
        LONG("bigint"),
        VARCHAR("varchar(255)");

    }
}