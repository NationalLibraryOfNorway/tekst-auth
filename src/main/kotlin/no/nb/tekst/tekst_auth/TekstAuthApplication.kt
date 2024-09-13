package no.nb.tekst.tekst_auth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
class TekstAuthApplication

fun main(args: Array<String>) {
	runApplication<TekstAuthApplication>(*args)
}
