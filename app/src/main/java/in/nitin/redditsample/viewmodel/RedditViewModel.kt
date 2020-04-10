package `in`.nitin.redditsample.viewmodel

import `in`.nitin.redditsample.datasource.model.Child
import `in`.nitin.redditsample.datasource.model.Comments
import `in`.nitin.redditsample.datasource.model.RedditModel
import `in`.nitin.redditsample.datasource.model.Replies
import `in`.nitin.redditsample.datasource.model.login.Login
import `in`.nitin.redditsample.datasource.network.Result
import `in`.nitin.redditsample.datasource.respository.RedditRemoteDataSource
import `in`.nitin.redditsample.ui.timeConverter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class RedditViewModel @Inject constructor(
    private val remoteDataSource: RedditRemoteDataSource
) :
    ViewModel() {
    private val postMutableLiveData = MutableLiveData<Result<List<RedditModel>>>()
    private val commentsLivedata = MutableLiveData<Result<List<Comments>>>()
    private val logindata = MutableLiveData<Result<Login>>()


    var username = MutableLiveData<String>()
    var passowrd = MutableLiveData<String>()
    var errorUser = MutableLiveData<String>()
    var errorPass = MutableLiveData<String>()
    var viewEnable = MutableLiveData<Boolean>()


    init {
        viewEnable.value = true
    }

    fun refreshData() {
        viewModelScope.launch {
            postMutableLiveData.value = Result.loading()
            postMutableLiveData.value = remoteDataSource.fetchPost()
        }
    }

    fun getPostData(): LiveData<Result<List<RedditModel>>> {
        return postMutableLiveData

    }


    /**
     * fetching comment list from random post api response
     * it might take some time that's why we are fetching it in background
     * */
    suspend fun setCommentData(commentsList: List<Child>) {
        val commentData = viewModelScope.async(IO) { getCommentsList(commentsList) }
        commentsLivedata.value = commentData.await()
    }

    fun getCommentList(): LiveData<Result<List<Comments>>> {
        return commentsLivedata
    }

    /**
     * getting comments from  API response of Random post data
     * */
    private fun getCommentsList(commentsList: List<Child>): Result<List<Comments>> {

        val list = ArrayList<Comments>()
        val gson = Gson()
        try {
            for (comment in commentsList) {
                /**
                 * these are only contains comments
                 * */
                val commentAtZeroLeveL = Comments(
                    0,
                    comment = comment.data!!.body!!,
                    postedOn = comment.data!!.created!!.toLong().timeConverter(),
                    author = comment.data!!.author!!
                )
                list.add(commentAtZeroLeveL)

                /*************************************************************/
                if (comment.data!!.replies!! != "") {
                    /**
                     * Reply data is in-consistence from api that's why we are converting it to json
                     * and then to [Replies] data class
                     * */
                    val replyJsonData = gson.toJson(comment.data!!.replies)
                    val replies = gson.fromJson(replyJsonData, Replies::class.java)
                    val replyList = replies.data!!.children!!
                    /**
                     * these are the replies of above comment
                     * */
                    for (commentReply in replyList) {
                        val commentAtOneLeveL = Comments(
                            1,
                            comment = commentReply.data!!.body!!,
                            postedOn = commentReply.data!!.created!!.toLong().timeConverter(),
                            author = commentReply.data!!.author!!
                        )
                        list.add(commentAtOneLeveL)

                        /*************************************************************/
                        if (commentReply.data!!.replies!! != "") {
                            /**
                             * again reply data is in-consistence from api that's why we are converting it to json
                             * and then to [Replies] data class
                             * */
                            val replyJsonData2 = gson.toJson(commentReply.data!!.replies)
                            val repliesOfReply = gson.fromJson(replyJsonData2, Replies::class.java)
                            val replyListOfReply = repliesOfReply.data!!.children
                            /**
                             * these are the replies of above each reply
                             * */
                            for (replyOfReply in replyListOfReply!!) {
                                val commentAtSecondLevel = Comments(
                                    2,
                                    comment = replyOfReply.data!!.body!!,
                                    postedOn = replyOfReply.data!!.created!!.toLong()
                                        .timeConverter(),
                                    author = replyOfReply.data!!.author!!
                                )
                                list.add(commentAtSecondLevel)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Result.error(e.message!!, null)
        }
        return Result.success(list)
    }


    /**
     * check password and user before calling login api
     * */
    fun validation() {
        if (username.value.isNullOrEmpty()) {
            errorUser.value = "required"
        } else if (passowrd.value.isNullOrEmpty()) {
            errorUser.value = ""
            errorPass.value = "required"
        } else {
            errorPass.value = ""
            errorUser.value = ""
            login(username = username.value!!, password = passowrd.value!!)
        }
    }

    /**
     * Login-> if user put wrong passwrod or username then we get success
     * so in success condition we are checking that [json.data] is null or not
     * if it is null that's mean user put wrong credential
     *
     * And
     *
     * we are using viewEnable live data to enable or disable the editTexts and button
     * while login api is running
     * */
    private fun login(username: String, password: String) {
        viewModelScope.launch {
            logindata.value = Result.loading()
            viewEnable.value = false
            val result = remoteDataSource.doLogin(username, password)
            when (result.status) {
                Result.Status.SUCCESS -> {
                    if (result.data!!.json.data == null) {
                        logindata.value = Result.error("Wrong Password")
                        errorPass.value = "Wrong Password"
                    } else {
                        logindata.value = Result.success(result.data)
                    }
                    viewEnable.value = true

                }
                Result.Status.ERROR -> {
                    logindata.value = Result.error(result.message!!, null)
                    viewEnable.value = true

                }
                else -> {

                }
            }
        }
    }


    fun getLogindata(): LiveData<Result<Login>> {
        return logindata
    }

}