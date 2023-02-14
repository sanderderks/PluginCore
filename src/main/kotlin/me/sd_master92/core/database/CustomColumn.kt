package me.sd_master92.core.database

class CustomColumn(val database: CustomDatabase, val table: CustomTable, var name: String)
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

    fun renameOrCreate(newName: String, dataType: DataType): Boolean
    {
        val statement = database.connection!!.prepareStatement("ALTER TABLE ${table.name} CHANGE $name $newName ${dataType.value}")
        return if(database.execute(statement))
        {
            name = newName
            true
        } else
        {
            createIFNotExists(dataType)
        }
    }

    fun delete(): Boolean
    {
        return if(exists())
        {
            val statement = database.connection!!.prepareStatement("ALTER TABLE ${table.name} DROP COLUMN $name")
            database.execute(statement)
        } else {
            true
        }
    }

    enum class DataType(val value: String)
    {
        INT_PRIMARY("int PRIMARY KEY AUTO_INCREMENT"),
        VARCHAR_PRIMARY("varchar(255) PRIMARY KEY"),
        INT("int"),
        BOOLEAN("bool"),
        LONG("bigint"),
        VARCHAR("varchar(255)");

    }
}