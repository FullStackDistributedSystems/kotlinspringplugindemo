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
