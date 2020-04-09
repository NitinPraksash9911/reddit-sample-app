package `in`.nitin.redditsample.datasource.network

import `in`.nitin.redditsample.application.RedditApplication
import `in`.nitin.redditsample.datasource.network.ApiConstant.CONNECT_TIME_OUT
import `in`.nitin.redditsample.datasource.network.ApiConstant.HEADER_CACHE_CONTROL
import `in`.nitin.redditsample.datasource.network.ApiConstant.HEADER_PRAGMA
import `in`.nitin.redditsample.datasource.network.ApiConstant.NETWORK_INTERCEPTOR_MAX_AGE
import `in`.nitin.redditsample.datasource.network.ApiConstant.READ_TIME_OUT
import `in`.nitin.redditsample.datasource.network.ApiConstant.WRITE_TIME_OUT
import com.google.gson.GsonBuilder
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ApiFactory @Inject constructor() {

    /**
     * [apiBaseUrl]
     * because we are adding it from parameter in the request
     * */
    private val apiBaseUrl = ""

    private var retrofit: Retrofit? = null


    /**
     * open socket using retrofit for network req/response.
     *
     * @param serviceClass
     * @param <S>
     * @return
    </S> */
    fun <S> createService(serviceClass: Class<S>?): S {
        if (retrofit == null) {
            buildClient()
        }
        return retrofit!!.create(serviceClass!!)
    }

    /**
     * retrofit builder for applying converters, base url etc.
     *
     * @param this@createBuilder
     * @return
     */

//    private val listofredditModel: Type? =
//        object : TypeToken<List<RedditModel?>?>() {}.getType()

    private fun String.createBuilder(): Retrofit.Builder {
        return Retrofit.Builder()
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().create()
                )
            )
            .baseUrl(this)
    }


    /**
     *
     * @param this@buildClient
     * */
    private fun buildClient() {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        val builder = apiBaseUrl.createBuilder()

        val httpClient =
            httpClientBuilder

        if (!httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging)
        }
        if (!httpClient.interceptors().contains(networkInterceptor)) {
            httpClient.addNetworkInterceptor(networkInterceptor)
        }
        if (!httpClient.interceptors().contains(internetConnectionInterceptor)) {
            httpClient.addInterceptor(internetConnectionInterceptor)
        }

        builder.client(httpClient.build())

        retrofit = builder.build()
    }


    /**
     * [httpClientBuilder]
     * creating okHttp client builder with some static or constant parameters
     * */
    private val httpClientBuilder: OkHttpClient.Builder
        get() {
            val httpClient = OkHttpClient.Builder()
            httpClient.connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .followSslRedirects(true)
                .followRedirects(true)
            return httpClient

        }


    /**
     *  [networkInterceptor]
     *network interceptor is useful if user making a request more than once in given maxAge()
     * then retrofit will
     * NOTE: it is only going to use when network is available
     * */
    private val networkInterceptor: Interceptor
        get() =
            Interceptor {
                val response = it.proceed(it.request())
                val cacheControl = CacheControl.Builder()
                    .maxAge(NETWORK_INTERCEPTOR_MAX_AGE, TimeUnit.SECONDS)
                    .build()
                response.newBuilder()
                    /**
                     *[HEADER_PRAGMA]->it is a header attached to http request and tell the request not to use caching ever
                     * so that's why we removing it as we want the request to cache for 5 seconds
                     * */
                    .removeHeader(HEADER_PRAGMA)
                    /**
                     * when we request okHttp call a default cache control comes from the server which contain
                     * header [HEADER_CACHE_CONTROL]
                     * so what we are doing here, we removing header comes from the sever and apply our own cache control header
                     * */
                    .removeHeader(HEADER_CACHE_CONTROL)
                    .header(HEADER_CACHE_CONTROL, cacheControl.toString())
                    .build()
            }


    /**
     * [internetConnectionInterceptor] check availability  of internet
     * if not then show custom error defined below
     * */
    private val internetConnectionInterceptor: Interceptor
        get() = Interceptor { chain ->

            if (!RedditApplication.getNetworkStatus()) {
                throw NoConnectivityException()
            } else if (!isInternetAvailable()) {
                throw NoInternetException()
            } else {
                chain.proceed(chain.request())
            }

        }


    /**
     * check speed of internet is very slow and give below custom exception
     * [NoConnectivityException], [NoInternetException]
     * */
    private fun isInternetAvailable(): Boolean {
        return try {
            val timeoutMs = 1500
            val sock = Socket()
            val sockaddr = InetSocketAddress("8.8.8.8", 53)
            sock.connect(sockaddr, timeoutMs)
            sock.close()
            true
        } catch (e: IOException) {
            false
        }

    }


    /**
     * [CustomExceptionClass] throws when internet connectivity is very slow
     * */
    private class NoConnectivityException : IOException() {
        override val message: String
            get() = "No network available, please check your WiFi or Data connection"
    }

    /**
     * [CustomExceptionClass] throws when internet is not available
     * */
    private class NoInternetException() : IOException() {
        override val message: String
            get() = "No internet available, please check your connected WIFi or Data"
    }

}