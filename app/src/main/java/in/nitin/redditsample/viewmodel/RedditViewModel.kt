package `in`.nitin.redditsample.viewmodel

import `in`.nitin.redditsample.datasource.model.RedditModel
import `in`.nitin.redditsample.datasource.network.Result
import `in`.nitin.redditsample.datasource.respository.RedditRemoteDataSource
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class RedditViewModel @Inject constructor(
    private val remoteDataSource: RedditRemoteDataSource
) :
    ViewModel() {
    private val postMutableLiveData = MutableLiveData<Result<List<RedditModel>>>()


    fun refreshData() {
        viewModelScope.launch {
            postMutableLiveData.value = Result.loading()
            val response = remoteDataSource.fetchPost()
            if (response.status == Result.Status.SUCCESS) {
                postMutableLiveData.value = Result.success(response.data!!)
            } else if (response.status == Result.Status.ERROR) {
                postMutableLiveData.value = Result.error(response.message!!, null)
            }
        }
    }

    fun getPostData(): LiveData<Result<List<RedditModel>>> {
        return postMutableLiveData

    }

}