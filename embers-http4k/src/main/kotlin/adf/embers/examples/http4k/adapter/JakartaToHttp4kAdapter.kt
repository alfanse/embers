package adf.embers.examples.http4k.adapter

import jakarta.ws.rs.core.Response as JaxRsResponse
import org.http4k.core.Response as Http4kResponse
import org.http4k.core.Status

/**
 * Extension function to convert a Jakarta REST Response to an http4k Response
 */
fun JaxRsResponse.toHttp4kResponse(): Http4kResponse {
    val http4kResponse = Http4kResponse(Status(this.status, ""))
    
    // Copy headers
    this.headers.forEach { (key, values) ->
        values.forEach { value ->
            http4kResponse.header(key, value.toString())
        }
    }
    
    // Set body if present
    val entity = this.entity
    return if (entity != null) {
        val body = when (entity) {
            is String -> entity
            is ByteArray -> String(entity)
            else -> entity.toString()
        }
        http4kResponse.body(body)
    } else {
        http4kResponse
    }
}

/**
 * Utility object for response conversions
 */
object ResponseConverter {
    /**
     * Converts a Jakarta Response to an http4k Response
     */
    @JvmStatic
    fun convert(jakartaResponse: JaxRsResponse): Http4kResponse {
        return jakartaResponse.toHttp4kResponse()
    }
}
