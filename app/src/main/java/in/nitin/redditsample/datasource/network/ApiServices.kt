package `in`.nitin.redditsample.datasource.network

import `in`.nitin.redditsample.datasource.model.RedditModel
import retrofit2.Response
import retrofit2.http.GET


interface ApiServices {
    @GET("random.json")
    suspend fun getRandomPost() : Response<List<RedditModel>>
}