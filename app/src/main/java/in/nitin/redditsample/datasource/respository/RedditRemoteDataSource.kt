package `in`.nitin.redditsample.datasource.respository

import `in`.nitin.redditsample.datasource.network.ApiFactory
import `in`.nitin.redditsample.datasource.network.ApiServices
import `in`.nitin.redditsample.datasource.network.ResponseHandler
import javax.inject.Inject


class RedditRemoteDataSource @Inject constructor(private val apiFactory: ApiFactory) :
    ResponseHandler() {

    suspend fun fetchPost() = getResult {
        apiFactory.createService(ApiServices::class.java).getRandomPost()
    }

}