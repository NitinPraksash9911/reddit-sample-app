package `in`.nitin.redditsample.ui

import `in`.nitin.redditsample.R
import `in`.nitin.redditsample.application.RedditApplication
import `in`.nitin.redditsample.databinding.ActivityMainBinding
import `in`.nitin.redditsample.datasource.model.Data_
import `in`.nitin.redditsample.datasource.network.Result
import `in`.nitin.redditsample.viewmodel.RedditViewModel
import `in`.nitin.redditsample.viewmodel.ViewModelProviderFactory
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.content_layout.view.*
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

        binding.btnLoadPost.setOnClickListener {

            viewModel.refreshData()
        }
    }

    private fun observeData() {
        viewModel.getPostData().observe(this, Observer {
            if (it != null) {
                when (it.status) {

                    Result.Status.LOADING -> {
                        binding.contentLayout.status_image.show()
                        binding.btnLoadPost.isClickable = false
                    }
                    Result.Status.SUCCESS -> {
                        subscribeUi(
                            it.data!![0].data!!.children!![0].data
                        )

                        binding.contentLayout.status_image.hide()
                        binding.btnLoadPost.isClickable = true

                    }
                    Result.Status.ERROR -> {
                        binding.contentLayout.status_image.hide()
                        binding.btnLoadPost.isClickable = true
                        it.message!!.snack(Color.RED, binding.root)

                    }
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun subscribeUi(data_: Data_?) {
        binding.contentLayout.titleTv.text = data_!!.title!!.trim()
        binding.contentLayout.dateTv.text = data_.created!!.toLong().timeConverter()
        binding.contentLayout.authorTv.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.contentLayout.authorTv.text = data_.author!!.trim()
        binding.contentLayout.selfTextTv.text =
            data_.selftext?.trim()!!.ifEmpty { "No Self Text" }
        binding.contentLayout.numCommentsTv.text = "${data_.numComments.toString()} comments"
        binding.contentLayout.likesTv.text =
            "${if (data_.likes == null) "0" else data_.likes} likes"

    }


}
