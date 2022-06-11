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
        val statement =
            database.connection!!.prepareStatement("CREATE TABLE ? (? ?)")
        statement.setString(1, name)
        statement.setString(2, column)
        statement.setString(3, dataType.value)
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
        val statement = database.connection!!.prepareStatement("DROP TABLE ?")
        statement.setString(1, table)
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
            database.connection!!.prepareStatement("INSERT INTO ? (?) VALUES ($placeholders)")
        statement.setString(1, name)
        statement.setString(2, columnsAsString.toString())
        var i = 3
        for (value in values)
        {
            statement.setValue(i, value)
            i++
        }
        return database.execute(statement)
    }

    fun updateData(whereColumn: String, whereValue: Any, updateColumn: String, updateValue: Any): Boolean
    {
        val statement =
            database.connection!!.prepareStatement("UPDATE ? SET ?=? WHERE ?=?")
        statement.setString(1, name)
        statement.setString(2, whereColumn)
        statement.setValue(3, whereValue)
        statement.setString(4, updateColumn)
        statement.setValue(5, updateValue)
        return database.execute(statement)
    }

    fun getData(column: String, value: Any): ResultSet
    {
        val statement =
            database.connection!!.prepareStatement("SELECT * FROM ? WHERE ?=?")
        statement.setString(1, name)
        statement.setString(2, column)
        statement.setValue(3, value)
        return database.query(statement)!!
    }

    fun getAll(): ResultSet
    {
        val statement =
            database.connection!!.prepareStatement("SELECT * FROM ?")
        statement.setString(1, name)
        return database.query(statement)!!
    }

    fun deleteData(column: String, value: Any): Boolean
    {
        val statement =
            database.connection!!.prepareStatement("DELETE FROM ? WHERE ?=?")
        statement.setString(1, name)
        statement.setString(2, column)
        statement.setValue(3, value)
        return database.execute(statement)
    }
}