package me.sd_master92.core.database

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
        return database.execute("ALTER TABLE " + table.name + " ADD " + name + " " + dataType.value)
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
        return database.execute("ALTER TABLE " + table.name + " DROP COLUMN " + name)
    }

    fun insertData(value: String): Boolean
    {
        return database.execute("INSERT INTO " + table.name + " (" + name + ") VALUES (" + value + ")")
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