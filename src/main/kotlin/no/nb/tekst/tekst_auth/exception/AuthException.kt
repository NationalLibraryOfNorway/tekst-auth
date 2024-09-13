package no.nb.tekst.tekst_auth.exception

class AuthException(message: String): Exception("Could not authorize: $message")
