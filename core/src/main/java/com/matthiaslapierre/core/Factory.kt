package com.matthiaslapierre.core

interface Factory<T> {
    fun create(): T
}