package `in`.nitin.redditsample.ui

import `in`.nitin.redditsample.R
import `in`.nitin.redditsample.application.RedditApplication
import `in`.nitin.redditsample.databinding.ActivityMainBinding
import `in`.nitin.redditsample.datasource.model.Child
import `in`.nitin.redditsample.datasource.model.Comments
import `in`.nitin.redditsample.datasource.model.Data_
import `in`.nitin.redditsample.datasource.model.Replies
import `in`.nitin.redditsample.datasource.network.Result
import `in`.nitin.redditsample.viewmodel.RedditViewModel
import `in`.nitin.redditsample.viewmodel.ViewModelProviderFactory
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import kotlinx.android.synthetic.main.content_layout.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var viewModel: RedditViewModel

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        RedditApplication.getComponent()!!.inject(this)
        initView()
        observeData()
    }


    private fun initView() {

        viewModel = ViewModelProvider(this, providerFactory).get(RedditViewModel::class.java)

        binding.contentLayout.commentRecyclerView.apply {
            this.layoutManager = LinearLayoutManager(this@MainActivity)
            this.setToggleItemOnClick(true)
            this.setAccordion(false)
        }
        /**
         * to refresh post data
         * */
        binding.btnLoadPost.setOnClickListener {
            viewModel.refreshData()
        }
    }

    private fun observeData() {
        viewModel.getPostData().observe(this, Observer {
            if (it != null) {
                when (it.status) {

                    Result.Status.LOADING -> {
                        binding.apply {
                            /**
                             * here we are using extension function to hide and show image
                             * defined in [ExtensionFunc.kt]
                             * */
                            this.contentLayout.status_image.show()
                            this.btnLoadPost.isClickable = false
                        }
                    }
                    Result.Status.SUCCESS -> {
                        /**
                         * add post data
                         * */
                        subscribeUi(
                            it.data!![0].data!!.children!![0].data
                        )
                        /**
                         * if comment list not empty then set the list otherwise hide [RecyclerView]
                         * */
                        val commentList = it.data[1].data!!.children!!
                        if (commentList.isNotEmpty()) {
                            subscribeAdapter(commentList)
                        } else {
                            binding.contentLayout.commentRecyclerView.visibility = View.GONE
                        }
                        binding.apply {

                            this.contentLayout.status_image.hide()
                            this.btnLoadPost.isClickable = true
                        }
                    }
                    Result.Status.ERROR -> {
                        binding.apply {
                            this.contentLayout.status_image.hide()
                            this.btnLoadPost.isClickable = true
                            /**
                             * here we also using extension function to show the snackbar
                             * defined in [ExtensionFunc.kt]
                             * */
                            it.message!!.snack(Color.RED, this.root)
                        }
                    }
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun subscribeUi(data_: Data_?) {

        binding.contentLayout.apply {

            this.titleTv.text = data_!!.title!!.trim()
            this.dateTv.text = data_.created!!.toLong().timeConverter()
            this.authorTv.paintFlags = Paint.UNDERLINE_TEXT_FLAG
            this.authorTv.text = data_.author!!.trim()
            this.selfTextTv.text =
                data_.selftext?.trim()!!.ifEmpty { "No Self Text" }
            this.numCommentsTv.text = "${data_.numComments.toString()} comments"
            this.likesTv.text =
                "${if (data_.likes == null) "0" else data_.likes} likes"
        }


    }

    private fun subscribeAdapter(comments: List<Child>) {

        binding.contentLayout.commentRecyclerView.visibility = View.VISIBLE
        /**
         * Here we are using [CoroutineScope] because comment list might be very large so getting comments from response data
         * could take some time to fetch comments add to [Comments] data class
         * */
        CoroutineScope(Dispatchers.Main).launch {
            val commenList = async(Dispatchers.IO) { getCommentsList(comments) }
            binding.contentLayout.commentRecyclerView.adapter =
                CommentAdapter(commenList.await(), this@MainActivity)
        }


    }


    private fun getCommentsList(commentsList: List<Child>): List<Comments> {
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
            /**
             * sometimes reply data is in-consistence from very starting (from zero level of comment)
             * */
            e.message!!.snack(Color.LTGRAY, binding.root)
        }
        return list
    }
}
