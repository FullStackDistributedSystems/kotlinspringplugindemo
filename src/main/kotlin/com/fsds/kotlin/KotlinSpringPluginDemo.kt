package com.fsds.kotlin

import com.fsds.kotlin.pluginmanager.PluginManagerService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.scheduling.annotation.EnableScheduling
import javax.annotation.PostConstruct

@EnableCaching
@EnableScheduling
@SpringBootApplication
open class KotlinSpringPluginDemo {

    private val log = LoggerFactory.getLogger(KotlinSpringPluginDemo::class.java)

    @Autowired
    lateinit var pluginManagerService: PluginManagerService

    @PostConstruct
    open fun init() {

        pluginManagerService.activate()

        pluginManagerService.init("app.name=${this.javaClass}", "app.description=Just an awesome app")

        log.info("Initialized ${this.javaClass} with Plugin Manager Service: $pluginManagerService.")
    }

}

fun main(args: Array<String>) {

    SpringApplication.run(KotlinSpringPluginDemo::class.java)
}

