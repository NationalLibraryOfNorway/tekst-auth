package no.nb.tekst.tekst_auth.model


import com.fasterxml.jackson.annotation.JsonProperty
import com.nimbusds.jwt.JWTParser
import java.time.ZoneId
import java.time.ZonedDateTime

data class TokenResponse (
    @JsonProperty("access_token")
    val accessToken: String,

    @JsonProperty("expires_in")
    val expiresIn: Int,

    @JsonProperty("refresh_expires_in")
    val refreshExpiresIn: Int,

    @JsonProperty("refresh_token")
    val refreshToken: String,

    @JsonProperty("token_type")
    val tokenType: String?,

    @JsonProperty("id_token")
    val idToken: String?,

    @JsonProperty("not-before-policy")
    val notBeforePolicy: Int?,

    @JsonProperty("session_state")
    val sessionState: String?,

    val scope: String?
) {
    fun mapToToken(): Token {
        val username = JWTParser.parse(this.accessToken).jwtClaimsSet.getClaim("preferred_username") as String
        val groups = JWTParser.parse(this.accessToken).jwtClaimsSet.getClaim("groups") as List<*>
        val name = JWTParser.parse(this.accessToken).jwtClaimsSet.getClaim("name") as String
        // All groups are strings, but the parser does not know this
        val filteredGroups = groups.filterIsInstance<String>()

        return Token(
            accessToken = accessToken,
            expires = ZonedDateTime.now(ZoneId.of("Europe/Oslo")).plusSeconds(expiresIn.toLong()),
            refreshToken = refreshToken,
            refreshExpires = ZonedDateTime.now(ZoneId.of("Europe/Oslo")).plusSeconds(refreshExpiresIn.toLong()),
            groups = filteredGroups,
            name = name,
            username = username
        )
    }
}
