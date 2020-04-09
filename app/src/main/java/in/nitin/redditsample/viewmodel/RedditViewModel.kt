package `in`.nitin.redditsample.viewmodel

import `in`.nitin.redditsample.datasource.respository.RedditRemoteDataSource
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class RedditViewModel @Inject constructor(
    private val remoteDataSource: RedditRemoteDataSource
) :
    ViewModel() {


}