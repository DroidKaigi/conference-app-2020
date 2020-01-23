package io.github.droidkaigi.confsched2020.image

import io.mockk.every
import io.mockk.mockk
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class SessionizeVaryHeaderRemoveInterceptorTest {
    private lateinit var interceptor: SessionizeVaryHeaderRemoveInterceptor

    companion object {
        private const val SESSIONIZE_IMAGE_URL =
            "https://sessionize.com/image?f=foo"
        private const val OTHER_URL =
            "https://deploy-preview-49--droidkaigi-api-dev.netlify.com/2020/timetable/index.json"
    }

    @Before
    fun setUp() {
        interceptor = SessionizeVaryHeaderRemoveInterceptor()
    }

    @Test
    fun intercept_sessionize_response() {
        val request = createRequest(SESSIONIZE_IMAGE_URL)
        val rawResponse = createResponse(request, 200, Headers.of("Vary", "*"))
        val mockChain = createMockChain(request, rawResponse)

        val response = interceptor.intercept(mockChain)

        assertNull("Vary header is removed", response.header("Vary"))
    }

    @Test
    fun intercept_sessionize_failure_response() {
        val request = createRequest(SESSIONIZE_IMAGE_URL)
        val rawResponse = createResponse(request, 400, Headers.of("Vary", "*"))
        val mockChain = createMockChain(request, rawResponse)

        val response = interceptor.intercept(mockChain)

        assertNotNull("Vary header is not removed", response.header("Vary"))
    }

    @Test
    fun intercept_other_response() {
        val request = createRequest(OTHER_URL)
        val rawResponse = createResponse(request, 200, Headers.of("Vary", "*"))
        val mockChain = createMockChain(request, rawResponse)

        val response = interceptor.intercept(mockChain)

        assertNotNull("Vary header is not removed", response.header("Vary"))
    }

    private fun createRequest(url: String): Request {
        return Request.Builder()
            .url(url)
            .build()
    }

    private fun createResponse(request: Request, code: Int, headers: Headers): Response {
        return Response.Builder()
            .request(request)
            .message("OK")
            .code(code)
            .headers(headers)
            .protocol(Protocol.HTTP_1_1)
            .build()
    }

    private fun createMockChain(request: Request, response: Response): Interceptor.Chain {
        val mockChain = mockk<Interceptor.Chain>()
        every { mockChain.request() } returns request
        every { mockChain.proceed(any()) } returns response
        return mockChain
    }
}
