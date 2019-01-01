# kotlin spring plugin demo

This is a tiny project to demonstrate some of the powerful feature of Spring and Kotlin to
create a powerful plugin-based architecture.  

Between `@Autowired`, `inline`, `reified`, and `KClass<T>`, we have everything we need to create
a well-defined and simple plugin architecture that is easy to reason about without spending too
much time on the book keeping associated with type erasure in Java.

But Kotlin?

Reified types are here to make life easier and more comfortable when using generic types, or other
logic around collections of objects that are instances of some type; very useful in a plugin system.

Kotlin's reified generics can be used for a lot more than bespoke plugin architectures.  However, the
ergonomic ease and power of combining `Kotlin` and `Spring` is undeniable.

## What's in the src?

  * `com.fsds.kotlin` - base package for the demo app.
  * `com.fsds.kotlin.pluginmanager` - package containing our plugin engine implementation.
    * `Plugin` - interface describing the contract of all plugins in the system.
    * `BasePlugin` - example parent / ancestor plugin at the top of a plugin hierarchy.
    * `PluginManagerService` - a `@Service` to manage all plugins descending from type `BasePlugin`.  The Plugin Manager Service is itself a plugin that inherits from `BasePlugin<Any?, Any?>`.
    * `PluginState` - small enumeration describing the currently available plugin states: `ENABLED`, and `DISABLED`.
    
## Plugin Manager Service
For ease of implementation, the plugin manager is just another plugin.  Here's how it works:

  * Get the unique set of plugins:
    ```kotlin
    @Autowired
    lateinit var plugins: Set<Plugin<Any?, Any?>>
    ```
  * Initialize the plugin manager:
    ```kotlin
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
    ```
  * Activate a plugin:
    ```kotlin
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
    ```
  * Deactivate a plugin:
    ```kotlin
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
    ```
  * Test the implementation:
    ```kotlin
    package com.fsds.kotlin.pluginmanager
    
    import org.assertj.core.api.Assertions
    import org.junit.Before
    import org.junit.Test
    import org.junit.runner.RunWith
    import org.slf4j.LoggerFactory
    import org.springframework.beans.factory.annotation.Autowired
    import org.springframework.boot.test.context.SpringBootTest
    import org.springframework.context.annotation.ComponentScan
    import org.springframework.test.context.ActiveProfiles
    import org.springframework.test.context.junit4.SpringRunner
    
    @RunWith(SpringRunner::class)
    @SpringBootTest
    @ComponentScan("com.fsds.kotlin.pluginmanager")
    @ActiveProfiles("local")
    class PluginManagerServiceTest {
    
        val log = LoggerFactory.getLogger(PluginManagerServiceTest::class.java)
    
        @Autowired
        lateinit var pluginManagerService: PluginManagerService
    
        @Before
        fun setUp() {
            pluginManagerService.init("service.name=PluginManagerServiceTest", "service.description=Just an integration test")
            pluginManagerService.activate()
        }
    
        @Test
        fun `activatePlugin when manager is active should activate plugin state`() {
    
            val result = pluginManagerService.activatePlugin(BasePlugin<Any?, Any?>().javaClass.kotlin)
            Assertions.assertThat(result).isTrue()
            log.info("pluginManagerService.activatePlugin(BasePlugin<Any?, Any?>().javaClass.kotlin) result=$result")
    
            val hasAnEnabledPlugin = pluginManagerService.plugins.stream().anyMatch { x -> x.pluginState == PluginState.ENABLED}
            Assertions.assertThat(hasAnEnabledPlugin).isTrue()
        }
    
        @Test
        fun `deactivatePlugin when manager is active should activate plugin state`() {
    
            val result = pluginManagerService.deactivatePlugin(BasePlugin<Any?, Any?>().javaClass.kotlin)
            Assertions.assertThat(result).isTrue()
            log.info("pluginManagerService.deactivatePlugin(BasePlugin<Any?, Any?>().javaClass.kotlin) result=$result")
    
            val hasADisabledPlugin = pluginManagerService.plugins.stream().anyMatch { x -> x.pluginState == PluginState.DISABLED}
            Assertions.assertThat(hasADisabledPlugin).isTrue()
        }
    }
    ```  
