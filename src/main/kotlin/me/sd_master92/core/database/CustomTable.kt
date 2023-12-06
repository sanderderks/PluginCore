package me.sd_master92.core.database

import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import me.sd_master92.core.database.CustomColumn.DataType
import me.sd_master92.core.setValue
import java.sql.ResultSet

class CustomTable(
    val database: CustomDatabase, val name: String
)
{

    suspend fun existsAsync(): Boolean
    {
        return withContext(SupervisorJob()) {
            try
            {
                database.connection!!.metaData.getTables(null, null, name, null).next()
            } catch (e: Exception)
            {
                database.error(e)
                false
            }
        }
    }

    suspend fun createAsync(column: String, dataType: DataType): Boolean
    {
        return withContext(SupervisorJob()) {
            val statement = database.connection!!.prepareStatement("CREATE TABLE $name ($column ${dataType.value})")
            database.executeAsync(statement)
        }
    }

    suspend fun createIFNotExistsAsync(column: String, dataType: DataType): Boolean
    {
        val exists = existsAsync()
        return withContext(SupervisorJob()) {
            if (!exists)
            {
                createAsync(column, dataType)
            } else true
        }
    }

    suspend fun deleteAsync(table: String): Boolean
    {
        return withContext(SupervisorJob()) {
            val statement = database.connection!!.prepareStatement("DROP TABLE $table")
            database.executeAsync(statement)
        }
    }

    fun getColumn(name: String): CustomColumn
    {
        return CustomColumn(database, this, name)
    }

    suspend fun insertDataAsync(columns: Array<String>, values: Array<Any>): Boolean
    {
        return withContext(SupervisorJob()) {
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
            database.executeAsync(statement)
        }
    }

    suspend fun updateDataAsync(whereColumn: String, whereValue: Any, updateColumn: String, updateValue: Any?): Boolean
    {
        return withContext(SupervisorJob()) {
            val statement =
                database.connection!!.prepareStatement("UPDATE $name SET $updateColumn=? WHERE $whereColumn=?")
            statement.setValue(1, updateValue)
            statement.setValue(2, whereValue)
            database.executeAsync(statement)
        }
    }

    suspend fun getDataAsync(column: String, value: Any): ResultSet?
    {
        return withContext(SupervisorJob()) {
            val statement = database.connection!!.prepareStatement("SELECT * FROM $name WHERE $column=?")
            statement.setValue(1, value)
            database.queryAsync(statement)
        }
    }

    suspend fun getAllAsync(): ResultSet?
    {
        return withContext(SupervisorJob()) {
            val statement = database.connection!!.prepareStatement("SELECT * FROM $name")
            database.queryAsync(statement)
        }
    }

    suspend fun deleteDataAsync(column: String, value: Any): Boolean
    {
        return withContext(SupervisorJob()) {
            val statement = database.connection!!.prepareStatement("DELETE FROM $name WHERE $column=?")
            statement.setValue(1, value)
            database.executeAsync(statement)
        }
    }
}
