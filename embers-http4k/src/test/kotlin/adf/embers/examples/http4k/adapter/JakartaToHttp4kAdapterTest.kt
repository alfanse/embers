package adf.embers.examples.http4k.adapter

import org.http4k.asString
import jakarta.ws.rs.core.Response as JaxRsResponse
import org.http4k.core.Status
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class JakartaToHttp4kAdapterTest {

    @Test
    fun `converts status code correctly`() {
        // Given
        val jakartaResponse = JaxRsResponse.status(201).build()
        
        // When
        val http4kResponse = jakartaResponse.toHttp4kResponse()
        
        // Then
        assertEquals(201, http4kResponse.status.code)
        assertEquals(Status.CREATED, http4kResponse.status)
    }

    @Test
    fun `converts headers correctly`() {
        // Given
        val jakartaResponse = JaxRsResponse.ok("some-body")
            .header("X-Test-Header", "test-value")
            .header("X-Another-Header", "another-value")
            .build()
        
        // When
        val http4kResponse = jakartaResponse.toHttp4kResponse()
        
        // Then
        assertEquals("test-value", http4kResponse.header("X-Test-Header"))
        assertEquals("another-value", http4kResponse.header("X-Another-Header"))
    }

    @Test
    fun `converts string entity correctly`() {
        // Given
        val testBody = "{\"message\":\"test\"}"
        val jakartaResponse = JaxRsResponse.ok(testBody, "application/json").build()
        
        // When
        val http4kResponse = jakartaResponse.toHttp4kResponse()
        
        // Then
        assertEquals(testBody, http4kResponse.body.payload.asString())
    }

    @Test
    fun `converts byte array entity correctly`() {
        // Given
        val testData = "test data"
        val byteData = testData.toByteArray()
        val jakartaResponse = JaxRsResponse.ok(byteData, "application/octet-stream").build()
        
        // When
        val http4kResponse = jakartaResponse.toHttp4kResponse()
        
        // Then
        assertEquals(testData, http4kResponse.bodyString())
    }

    @Test
    fun `converts custom object entity using toString`() {
        // Given
        data class TestData(val id: Int, val name: String)
        val testObj = TestData(1, "test")
        val jakartaResponse = JaxRsResponse.ok(testObj).build()
        
        // When
        val http4kResponse = jakartaResponse.toHttp4kResponse()
        
        // Then
        assertEquals(testObj.toString(), http4kResponse.bodyString())
    }

    @Test
    fun `converts status code and message correctly`() {
        // Given
        val jakartaResponse = JaxRsResponse.status(400, "Bad Request").build()
        
        // When
        val http4kResponse = jakartaResponse.toHttp4kResponse()
        
        // Then
        assertEquals(400, http4kResponse.status.code)
        assertEquals(Status.BAD_REQUEST, http4kResponse.status)
    }

    @Test
    fun `converts using ResponseConverter utility`() {
        // Given
        val jakartaResponse = JaxRsResponse.status(204).build()
        
        // When
        val http4kResponse = ResponseConverter.convert(jakartaResponse)
        
        // Then
        assertEquals(204, http4kResponse.status.code)
        assertEquals(Status.NO_CONTENT, http4kResponse.status)
    }
}
