package no.nb.tekst.tekst_auth.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "keycloak")
class KeycloakConfig (
    val nbauthBaseUrl: String,
    val nbauthClientId: String,
    val nbauthClientSecret: String,
    val nbauthRealm: String
)