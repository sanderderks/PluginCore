package me.sd_master92.core

import java.sql.PreparedStatement

fun String.appendWhenTrue(value: Boolean, append: String): String
{
    if (value)
    {
        return this + append
    }
    return this
}

fun Boolean.reverseWhenTrue(value: Boolean): Boolean
{
    if (value)
    {
        return !this
    }
    return this
}

fun PreparedStatement.setValue(i: Int, value: Any): PreparedStatement
{
    when (value)
    {
        is String -> this.setString(i, value)
        is Int    -> this.setInt(i, value)
        is Double -> this.setDouble(i, value)
        is Long   -> this.setLong(i, value)
        else      -> this.setString(i, value.toString())
    }
    return this
}