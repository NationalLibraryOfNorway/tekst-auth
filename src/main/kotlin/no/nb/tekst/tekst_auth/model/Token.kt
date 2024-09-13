package no.nb.tekst.tekst_auth.model

import java.time.ZonedDateTime

data class Token(
    val accessToken: String,
    val expires: ZonedDateTime,
    val refreshToken: String,
    val refreshExpires: ZonedDateTime,
    val groups: List<String>,
    val name: String
)
