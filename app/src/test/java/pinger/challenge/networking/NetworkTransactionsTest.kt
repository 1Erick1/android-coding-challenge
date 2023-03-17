package pinger.challenge.networking

import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

class NetworkTransactionsTest {

    private val mockNetworkClass = MockNetworkClass()

    @Test
    fun apacheFileIsReturned() {
        val testObserver = TestObserver<String>()
        //mockNetworkClass.downloadApacheFile(testObserver, Schedulers.trampoline(), Schedulers.trampoline())

        testObserver.assertValueCount(11)
        testObserver.assertComplete()
    }
}

class MockNetworkClass : NetworkTransactions() {
    private val mockWebServer = MockWebServer()

    override fun getApi(): FileDownloadAPI {
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("").toString())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        mockWebServer.enqueue(MockResponse().setBody(getMockResponse()))
        return retrofit.create<FileDownloadAPI>(FileDownloadAPI::class.java)
    }

    private fun getMockResponse(): String {
        return "123.4.5.9 - - [03/Sep/2013:18:34:48 -0600] \"GET /team/ HTTP/1.1\" 200 3327 \"-\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:23.0) Gecko/20100101 Firefox/23.0\"\n" +
                "123.4.5.6 - - [03/Sep/2013:18:34:58 -0600] \"GET /products/car/ HTTP/1.1\" 200 3327 \"-\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:23.0) Gecko/20100101 Firefox/23.0\"\n" +
                "123.4.5.8 - - [03/Sep/2013:18:35:08 -0600] \"GET /products/desk/ HTTP/1.1\" 200 3327 \"-\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.65 Safari/537.36\"\n" +
                "123.4.5.6 - - [03/Sep/2013:18:35:18 -0600] \"GET /products/desk/ HTTP/1.1\" 200 3327 \"-\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:23.0) Gecko/20100101 Firefox/23.0\"\n" +
                "123.4.5.9 - - [03/Sep/2013:18:35:28 -0600] \"GET /products/phone/ HTTP/1.1\" 200 3327 \"-\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:23.0) Gecko/20100101 Firefox/23.0\"\n" +
                "123.4.5.8 - - [03/Sep/2013:18:35:38 -0600] \"GET /products/phone/ HTTP/1.1\" 500 821 \"-\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.65 Safari/537.36\"\n" +
                "123.4.5.2 - - [03/Sep/2013:18:35:48 -0600] \"GET /access/ HTTP/1.1\" 200 3327 \"-\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.65 Safari/537.36\"\n" +
                "123.4.5.2 - - [03/Sep/2013:18:35:58 -0600] \"GET /products/desk/ HTTP/1.1\" 200 3327 \"-\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:23.0) Gecko/20100101 Firefox/23.0\"\n" +
                "123.4.5.4 - - [03/Sep/2013:18:36:08 -0600] \"GET /products/desk/ HTTP/1.1\" 200 3327 \"-\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.65 Safari/537.36\"\n" +
                "123.4.5.8 - - [03/Sep/2013:18:36:18 -0600] \"GET /contact/ HTTP/1.1\" 200 3327 \"-\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_8_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.65 Safari/537.36\"\n" +
                "123.4.5.5 - - [03/Sep/2013:18:36:28 -0600] \"GET /about/ HTTP/1.1\" 200 3327 \"-\" \"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:23.0) Gecko/20100101 Firefox/23.0\""
    }
}