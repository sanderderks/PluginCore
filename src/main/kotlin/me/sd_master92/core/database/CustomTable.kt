package me.sd_master92.core.database

import me.sd_master92.core.database.CustomColumn.DataType
import me.sd_master92.core.setValue
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
        val statement = database.connection!!.prepareStatement("CREATE TABLE $name ($column ${dataType.value})")
        return database.execute(statement)
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
        val statement = database.connection!!.prepareStatement("DROP TABLE $table")
        return database.execute(statement)
    }

    fun getColumn(name: String): CustomColumn
    {
        return CustomColumn(database, this, name)
    }

    fun insertData(columns: Array<String>, values: Array<Any>): Boolean
    {
        val columnsAsString = StringBuilder()
        for (column in columns)
        {
            columnsAsString.append(column).append(",")
        }
        columnsAsString.deleteCharAt(columnsAsString.length - 1)

        val placeholders = StringBuilder()
        for (value in values)
        {
            placeholders.append("?,")
        }
        placeholders.deleteCharAt(placeholders.length - 1)

        val statement =
            database.connection!!.prepareStatement("INSERT INTO $name ($columnsAsString) VALUES ($placeholders)")
        var i = 1
        for (value in values)
        {
            statement.setValue(i, value)
            i++
        }
        return database.execute(statement)
    }

    fun updateData(whereColumn: String, whereValue: Any, updateColumn: String, updateValue: Any?): Boolean
    {
        val statement = database.connection!!.prepareStatement("UPDATE $name SET $updateColumn=? WHERE $whereColumn=?")
        statement.setValue(1, updateValue)
        statement.setValue(2, whereValue)
        return database.execute(statement)
    }

    fun getData(column: String, value: Any): ResultSet?
    {
        val statement = database.connection!!.prepareStatement("SELECT * FROM $name WHERE $column=?")
        statement.setValue(1, value)
        return database.query(statement)
    }

    fun getAll(): ResultSet?
    {
        val statement = database.connection!!.prepareStatement("SELECT * FROM $name")
        return database.query(statement)
    }

    fun deleteData(column: String, value: Any): Boolean
    {
        val statement = database.connection!!.prepareStatement("DELETE FROM $name WHERE $column=?")
        statement.setValue(1, value)
        return database.execute(statement)
    }
}