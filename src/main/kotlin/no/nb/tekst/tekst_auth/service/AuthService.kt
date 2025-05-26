package no.nb.tekst.tekst_auth.service

import no.nb.tekst.tekst_auth.config.KeycloakConfig
import no.nb.tekst.tekst_auth.exception.AuthException
import no.nb.tekst.tekst_auth.exception.ServerErrorException
import no.nb.tekst.tekst_auth.model.TokenResponse
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono

@Service
class AuthService (
    private val keycloakConfig: KeycloakConfig
) {

    private val openIdWebclient = WebClient.builder()
        .baseUrl(keycloakConfig.nbauthBaseUrl + "/realm/" + keycloakConfig.nbauthRealm + "/protocol/openid-connect")
        .defaultHeader("Content-Type", "application/x-www-form-urlencoded")
        .build()

    fun getToken(code: String, redirectUrl: String): Mono<TokenResponse> {
        return openIdWebclient.post()
            .uri { uri -> uri.pathSegment("token").build() }
            .body(
                BodyInserters.fromValue(
                "client_id=${keycloakConfig.nbauthClientId}" +
                        "&client_secret=${keycloakConfig.nbauthClientSecret}" +
                        "&code=$code" +
                        "&grant_type=authorization_code" +
                        "&redirect_uri=$redirectUrl"
            ))
            .retrieve()
            .onStatus(
                { it.is4xxClientError },
                { throw AuthException("Could not get token. Might be an expired code? (Received status code ${it.statusCode()})") }
            )
            .onStatus(
                { it.is5xxServerError },
                { throw ServerErrorException("Something went wrong when trying to access authentication server (received status code: ${it.statusCode()})") }
            )
            .bodyToMono<TokenResponse>()
    }

    fun refreshToken(refreshToken: String): Mono<TokenResponse> {
        return openIdWebclient.post()
            .uri { uri -> uri.pathSegment("token").build() }
            .body(
                BodyInserters.fromValue(
                "client_id=${keycloakConfig.nbauthClientId}" +
                        "&client_secret=${keycloakConfig.nbauthClientSecret}" +
                        "&grant_type=refresh_token" +
                        "&refresh_token=$refreshToken"
            ))
            .retrieve()
            .onStatus(
                { it.is4xxClientError },
                { throw AuthException("Could not refresh token. Might be expired? (Received status code ${it.statusCode()})") }
            )
            .onStatus(
                { it.is5xxServerError },
                { throw ServerErrorException("Something went wrong when trying to access authentication server (received status code: ${it.statusCode()})") }
            )
            .bodyToMono<TokenResponse>()
    }

    fun logOut(refreshToken: String): Mono<String> {
        return openIdWebclient.post()
            .uri { uri -> uri.pathSegment("logout").build() }
            .body(
                BodyInserters.fromValue(
                "client_id=${keycloakConfig.nbauthClientId}" +
                        "&client_secret=${keycloakConfig.nbauthClientSecret}" +
                        "&refresh_token=$refreshToken"
            ))
            .retrieve()
            .onStatus(
                { it.is4xxClientError },
                { throw AuthException("Could not log out. Might not be refresh token? (Received status code ${it.statusCode()})") }
            )
            .onStatus(
                { it.is5xxServerError },
                { throw ServerErrorException("Something went wrong when logging out (received status code: ${it.statusCode()})")}
            )
            .bodyToMono<String>()
    }
}