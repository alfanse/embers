package adf.embers.examples.http4k

import org.assertj.core.api.Assertions.assertThat
import org.http4k.client.JavaHttpClient
import org.http4k.core.Method
import org.http4k.core.Request
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class Http4kIntegrationTest {
    companion object {
        private val client = JavaHttpClient()
        private lateinit var server: AutoCloseable
        private const val BASE_URL = "http://localhost:8002"

        @JvmStatic
        @BeforeAll
        fun setup() {
            // Start the server in a separate thread
            Thread {
                main()
            }.start()
            
            // Give the server some time to start
            Thread.sleep(1000)
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            // Server will be stopped when the JVM exits
        }
    }

    @Test
    fun `test query lifecycle`() {
        // Add a query
        val addResponse = client(
            Request(Method.POST, "$BASE_URL/admin/query")
                .form("name", "test_query")
                .form("sql", "SELECT 'Hello, http4k!' as message")
                .form("description", "Test query for http4k integration")
        )
        
        assertThat(addResponse.status.code).isEqualTo(200)
        
        // Execute the query
        val queryResponse = client(
            Request(Method.GET, "$BASE_URL/query/test_query")
        )
        
        assertThat(queryResponse.status.code).isEqualTo(200)
        assertThat(queryResponse.bodyString()).contains("Hello, http4k!")
        
        // Test cached query
        val cachedResponse = client(
            Request(Method.GET, "$BASE_URL/cache/test_query")
        )
        
        assertThat(cachedResponse.status.code).isEqualTo(200)
        assertThat(cachedResponse.bodyString()).contains("Hello, http4k!")
        
        // Delete the query
        val deleteResponse = client(
            Request(Method.DELETE, "$BASE_URL/admin/query/test_query")
        )
        
        assertThat(deleteResponse.status.code).isEqualTo(200)
        
        // Verify query is deleted
        val notFoundResponse = client(
            Request(Method.GET, "$BASE_URL/query/test_query")
        )
        
        assertThat(notFoundResponse.status.code).isEqualTo(400)
    }
}
