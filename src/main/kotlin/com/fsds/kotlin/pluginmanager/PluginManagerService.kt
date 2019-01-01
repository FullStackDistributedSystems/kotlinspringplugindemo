package com.fsds.kotlin.pluginmanager

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import kotlin.reflect.KClass

/**
 * Even the plugin manager is a plugin.  Is this wise?  Not sure, but we'll find out.  There are trade offs...
 */
@Service
open class PluginManagerService : BasePlugin<Any?, Any?>() {

    val log = LoggerFactory.getLogger(PluginManagerService::class.java)

    @Autowired
    lateinit var plugins: Set<Plugin<Any?, Any?>>

    override fun init(vararg configs: Any?): Boolean {

        plugins.forEach { plugin ->
            try {
                log.info("Initializing plugin: $plugin...")

                plugin.init("service.name=cool plugin", "service.description=just an awesome service")
            } catch (ex: Exception) {

                log.error("Error initializing plugins: ${ex.message} caused by: ${ex.cause}")

                return false
            }
        }

        return super.init(*configs)
    }

    inline fun <reified T : BasePlugin<Any?, Any?>> activatePlugin(pluginType: KClass<T>): Boolean {

        return try {

            log.info("Attempting to activate plugin: $pluginType")

            plugins.forEach {
                if (pluginType.isInstance(it)) {

                    log.info("Plugin Manager activated plugin: $it")

                    it.activate()

                    return true
                }
            }

            false

        } catch (ex: Exception) {

            log.error("Error activating plugin: $pluginType with message: ${ex.message} caused by: ${ex.cause}")

            false
        }
    }

    inline fun <reified T : BasePlugin<Any?, Any?>> deactivatePlugin(pluginType: KClass<T>): Boolean {

        return try {

            log.info("Attempting to deactivate plugin: $pluginType")

            plugins.forEach {
                if (pluginType.isInstance(it)) {

                    log.info("Plugin Manager deactivated plugin: $it")

                    it.deactivate()

                    return true
                }
            }

            false

        } catch (ex: Exception) {

            log.error("Error deactivating plugin: $pluginType with message: ${ex.message} caused by: ${ex.cause}")

            false
        }
    }
}
