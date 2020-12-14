package se.haleby.wordguessinggame

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

inline fun <reified T : Any> loggerFor(): Logger = LoggerFactory.getLogger(T::class.java)

@SpringBootApplication
class Bootstrap : WebMvcConfigurer {

    // Redirect to /games when navigating to /
    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addRedirectViewController("/", "/games")
    }

    // TODO Configure Occurrent and other beans
}

fun main(args: Array<String>) {
    runApplication<Bootstrap>(*args)
}
