package com.yapp.web2.exception

import com.yapp.web2.exception.custom.NoRefreshTokenException
import com.yapp.web2.exception.custom.PrefixMisMatchException
import com.yapp.web2.exception.custom.TokenMisMatchException
import com.yapp.web2.util.CustomStatusCode
import com.yapp.web2.util.Message
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.SignatureException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.servlet.http.HttpServletRequest
import kotlin.RuntimeException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log: Logger get() = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(SignatureException::class)
    fun handleSignatureException(e: Exception): ResponseEntity<ErrorResponse> {
        log.error("handleSignatureException", e)
        val response = ErrorResponse.of(e.message)

        return getResponse(response, HttpStatus.UNAUTHORIZED.value())
    }

    @ExceptionHandler(value = [Exception::class, RuntimeException::class])
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        log.error("handleException", e)
        val response = ErrorResponse.of(e.message)

        return getResponse(response, HttpStatus.INTERNAL_SERVER_ERROR.value())
    }

    @ExceptionHandler(ExpiredJwtException::class)
    fun handleExpiredJwtException(e: ExpiredJwtException): ResponseEntity<ErrorResponse> {
        log.error("ExpiredJwtException", e)
        val response = ErrorResponse.of(Message.NO_REFRESH_TOKEN)

        return getResponse(response, HttpStatus.UNAUTHORIZED.value())
    }

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ErrorResponse> {
        log.error("handleBusinessException", e)
        val response = ErrorResponse.of(e.message)

        return getResponse(response, HttpStatus.INTERNAL_SERVER_ERROR.value())
    }

    @ExceptionHandler(PrefixMisMatchException::class)
    fun handlePrefixMisMatchException(e: PrefixMisMatchException): ResponseEntity<ErrorResponse> {
        log.error("handleTPrefixMisMatchException", e)
        val response = ErrorResponse.of(e.message)

        return getResponse(response, HttpStatus.UNAUTHORIZED.value())
    }

    @ExceptionHandler(TokenMisMatchException::class)
    fun handleTokenMisMatchException(e: TokenMisMatchException): ResponseEntity<ErrorResponse> {
        log.error("handleTokenMisMatchException", e)
        val response = ErrorResponse.of(e.message)

        return getResponse(response, HttpStatus.UNAUTHORIZED.value())
    }

    @ExceptionHandler(NoRefreshTokenException::class)
    fun handleNoRefreshTokenException(e: NoRefreshTokenException): ResponseEntity<ErrorResponse> {
        log.error("handleNoRefreshTokenException", e)
        val response = ErrorResponse.of(e.message)

        return getResponse(response, HttpStatus.UNAUTHORIZED.value())
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        log.error("methodArgumentNotValidException", e)
        val errors = mutableListOf<Error>()
        // TODO: message 수정
        val response = ErrorResponse.of("이름을 정합시다", errors)

        e.bindingResult.allErrors.forEach { errorObject ->
            val error = Error().apply {
                val field = errorObject as FieldError
                this.field = field.field
                this.message = errorObject.defaultMessage
                this.value = errorObject.rejectedValue
            }
            errors.add(error)
        }
        return getResponse(response, HttpStatus.BAD_REQUEST.value())
    }

    fun getResponse(response: ErrorResponse, statusCode: Int): ResponseEntity<ErrorResponse> {
        val headers = HttpHeaders()
        headers.add("Content-Type", "application/json")

        return ResponseEntity(response, headers, statusCode)
    }
}