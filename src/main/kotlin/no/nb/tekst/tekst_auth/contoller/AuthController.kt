package no.nb.tekst.tekst_auth.contoller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import no.nb.tekst.tekst_auth.model.Token
import no.nb.tekst.tekst_auth.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@Tag(name = "Authentication", description = "Endpoints related to keycloak authentication")
@RequestMapping("/v1/auth")
class AuthController (
    private val authService: AuthService
) {

    @PostMapping("/login", produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Log in with code and redirect url",
        description = "After retrieving code from login system, you can use this endpoint to get token. " +
                      "Redirect URL must match the one given to the login system."
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Logged in"),
        ApiResponse(responseCode = "400", description = "Invalid code or redirect URL", content = [Content()]),
        ApiResponse(responseCode = "500", description = "Server error", content = [Content()])
    ])
    fun login(
        @RequestParam redirectUrl: String,
        @RequestBody code: String
    ): Mono<ResponseEntity<Token>> {
        return authService.getToken(code, redirectUrl).map {
            ResponseEntity.ok(it.mapToToken())
        }
    }

    @PostMapping("/refresh", produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Refresh authentication using refresh token",
        description = "After retrieving refresh token during regular login, you can use this endpoint to update the token. " +
                "Both a new access token and refresh token will be given."
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Token refreshed"),
        ApiResponse(responseCode = "400", description = "Error in token (expired?)", content = [Content()]),
        ApiResponse(responseCode = "500", description = "Server error", content = [Content()])
    ])
    fun refreshToken(
        @RequestBody refreshToken: String
    ): Mono<ResponseEntity<Token>> {
        return authService.refreshToken(refreshToken)
            .map { ResponseEntity.ok(it.mapToToken()) }
    }

    @PostMapping("/logout", produces = [MediaType.TEXT_PLAIN_VALUE])
    @Operation(
        summary = "Log out a user",
        description = "You can use this endpoint to log out a user using the refresh token."
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "Logged out"),
        ApiResponse(responseCode = "400", description = "Error in token (expired?)", content = [Content()]),
        ApiResponse(responseCode = "500", description = "Server error", content = [Content()])
    ])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun logout(
        @RequestBody refreshToken: String
    ): Mono<ResponseEntity<Nothing>> {
        return authService.logOut(refreshToken)
            .map { ResponseEntity.noContent().build() }
    }
}