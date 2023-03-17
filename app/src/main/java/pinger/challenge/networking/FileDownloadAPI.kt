package pinger.challenge.networking

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.http.GET


interface FileDownloadAPI {

    @GET("/cplachta-pinger/android_coding_challenge/master/Apache.log")
    suspend fun downloadApacheLogStream(): ResponseBody
}