package no.nb.tekst.tekst_auth.contoller

import no.nb.tekst.tekst_auth.exception.AuthException
import no.nb.tekst.tekst_auth.exception.ServerErrorException
import no.nb.tekst.tekst_auth.util.logger
import org.springframework.http.HttpStatus
import org.springframework.http.ProblemDetail
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.Instant

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(AuthException::class)
    fun handleAuthException(exception: AuthException): ProblemDetail {
        logger().info("AuthException occurred: ${exception.message}")

        val problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST)
        problemDetail.detail = exception.message ?: "Could not authenticate user."
        problemDetail.addDefaultProperties()
        return problemDetail
    }

    @ExceptionHandler(ServerErrorException::class)
    fun handleServerErrorException(exception: ServerErrorException): ProblemDetail {
        logger().error("ServerErrorException occurred: ${exception.message}")

        val problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR)
        problemDetail.detail = exception.message ?: "Something went wrong when trying to access authentication server."
        problemDetail.addDefaultProperties()
        return problemDetail
    }
}

fun ProblemDetail.addDefaultProperties() {
    this.setProperty("timestamp", Instant.now())
}