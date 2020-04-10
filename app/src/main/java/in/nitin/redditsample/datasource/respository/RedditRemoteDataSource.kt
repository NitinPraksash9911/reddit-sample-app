package `in`.nitin.redditsample.datasource.respository

import `in`.nitin.redditsample.datasource.network.ApiConstant
import `in`.nitin.redditsample.datasource.network.ApiFactory
import `in`.nitin.redditsample.datasource.network.ApiServices
import `in`.nitin.redditsample.datasource.network.ResponseHandler
import javax.inject.Inject


class RedditRemoteDataSource @Inject constructor(private val apiFactory: ApiFactory) :
    ResponseHandler() {

    suspend fun fetchPost() = getResult {
        apiFactory.createService(ApiServices::class.java).getRandomPost()
    }

    suspend fun doLogin(
        username: String,
        password: String
    ) = getResult {
        val header = HashMap<String, String>()
        header["Content-Type"] = "application/json"

        apiFactory.createService(ApiServices::class.java)
            .loginIn(header, ApiConstant.LOGIN_URL, username, password, ApiConstant.API_TYPE)
    }
}