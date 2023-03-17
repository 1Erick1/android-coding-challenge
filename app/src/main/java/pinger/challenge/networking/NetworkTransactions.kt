package pinger.challenge.networking

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.BufferedSource
import retrofit2.Retrofit
import java.io.IOException

open class NetworkTransactions {
    private val api: FileDownloadAPI by lazy {
        Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com")
            .build().create(FileDownloadAPI::class.java)
    }

    suspend fun downloadApacheFile(

    ): Flow<String> {
        val response = api.downloadApacheLogStream()
        return getLinesOfInputFromSource(response.source())
    }

    private suspend fun getLinesOfInputFromSource(source: BufferedSource): Flow<String> {
        return flow {
            try {
                while (!source.exhausted()) {
                    val line = source.readUtf8Line()
                    if (line != null) {
                        emit(line)
                    }
                }
            } catch (ex: IOException) {
                ex.printStackTrace()
                error(ex)
            }
        }
    }
}