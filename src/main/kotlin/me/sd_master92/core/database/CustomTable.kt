package me.sd_master92.core.database

import me.sd_master92.core.database.CustomColumn.DataType
import java.sql.ResultSet

class CustomTable(
    val database: CustomDatabase, val name: String
)
{
    fun exists(): Boolean
    {
        return try
        {
            database.connection!!.metaData.getTables(null, null, name, null).next()
        } catch (e: Exception)
        {
            database.error(e)
            false
        }
    }

    fun create(column: String, dataType: DataType): Boolean
    {
        return database.execute("CREATE TABLE " + name + " (" + column + " " + dataType.value + ")")
    }

    fun createIFNotExists(column: String, dataType: DataType): Boolean
    {
        return if (!exists())
        {
            create(column, dataType)
        } else true
    }

    fun delete(table: String): Boolean
    {
        return database.execute("DROP TABLE $table")
    }

    fun getColumn(name: String): CustomColumn
    {
        return CustomColumn(database, this, name)
    }

    fun insertData(columns: Array<String>, values: Array<String>): Boolean
    {
        val columnsAsString = StringBuilder()
        for (column in columns)
        {
            columnsAsString.append(column).append(",")
        }
        columnsAsString.deleteCharAt(columnsAsString.length - 1)
        val valuesAsString = StringBuilder()
        for (value in values)
        {
            valuesAsString.append(value).append(",")
        }
        valuesAsString.deleteCharAt(valuesAsString.length - 1)
        return database.execute("INSERT INTO $name ($columnsAsString) VALUES ($valuesAsString)")
    }

    fun updateData(where: String, changes: String): Boolean
    {
        return database.execute("UPDATE $name SET $changes WHERE $where")
    }

    fun getData(where: String): ResultSet
    {
        return database.query("SELECT * FROM $name WHERE $where")!!
    }

    val all: ResultSet
        get() = database.query("SELECT * FROM $name")!!

    fun deleteData(where: String): Boolean
    {
        return database.execute("DELETE FROM $name WHERE $where")
    }
}