package com.fsds.kotlin.pluginmanager

interface Plugin<T, C> {

    var pluginState: PluginState

    /**
     * init - setup the plugin
     */
    fun init(vararg configs: C?): Boolean

    /**
     * activate - activate the plugin: should be set to DISABLED by default
     */
    fun activate(): Boolean

    /**
     * deactivate - deactivate the plugin
     */
    fun deactivate(): Boolean

    /**
     * execute - perform the plugin's unit of work
     */
    fun execute(vararg models: T?): Boolean
}
