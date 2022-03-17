package me.sd_master92.core

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