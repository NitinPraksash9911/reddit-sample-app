package `in`.nitin.redditsample.datasource.network

import `in`.nitin.redditsample.datasource.model.RedditModel
import `in`.nitin.redditsample.datasource.model.login.Login
import retrofit2.Response
import retrofit2.http.*


interface ApiServices {
    @GET("random.json")
    suspend fun getRandomPost(): Response<List<RedditModel>>

    //
    @POST
    suspend fun loginIn(
        @HeaderMap headers: Map<String, String>,
        @Url loginUrl: String,
        @Query("user") username: String,
        @Query("passwd") password: String,
        @Query("api_type") type: String
    ): Response<Login>

}