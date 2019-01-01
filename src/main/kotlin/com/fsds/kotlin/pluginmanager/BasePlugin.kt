package com.fsds.kotlin.pluginmanager

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
open class BasePlugin<T, C> : Plugin<Any?, Any?> {

    private val log = LoggerFactory.getLogger(BasePlugin::class.java)

    override lateinit var pluginState: PluginState

    override fun init(vararg configs: Any?): Boolean {

        log.info("Initializing ${this.javaClass.kotlin} with configuration objects: $configs")

        configs.forEach {
            it?.let { config -> log.info("Applying configuration object: $config") }
        }

        return true
    }

    override fun activate(): Boolean {

        log.info("Activating plugin ${this.javaClass.kotlin}...")

        pluginState = PluginState.ENABLED

        log.info("Successfully set plugin ${this.javaClass.kotlin} pluginState=${this.pluginState}")

        return true
    }

    @PostConstruct // Deactivated by default
    override fun deactivate(): Boolean {

        log.info("Deactivating plugin ${this.javaClass.kotlin}...")

        pluginState = PluginState.DISABLED

        log.info("Successfully set plugin ${this.javaClass.kotlin} pluginState=${this.pluginState}")

        return true
    }

    override fun execute(vararg models: Any?): Boolean {

        if (pluginState == PluginState.ENABLED) {

            log.info("Plugin State is: ${this.pluginState} - executing ${this.javaClass.kotlin} with models: $models")

            return true

        }

        log.info("Plugin State is: ${this.pluginState} - skipping execution of: ${this.javaClass.kotlin}")

        return false
    }
}
