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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.content_layout.view.*
import kotlinx.coroutines.launch
import javax.inject.Inject


class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var viewModel: RedditViewModel

    lateinit var binding: ActivityMainBinding

    lateinit var bottomSheetBehavior: BottomSheetBehavior<CardView>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        setSupportActionBar(binding.toolbar);
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        binding.lifecycleOwner = this


        RedditApplication.getComponent()!!.inject(this)
        initView()
        observeData()
        subscribeAdapter()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.app_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.login_action -> {
                doLogin()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
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

    @SuppressLint("SetJavaScriptEnabled")
    fun doLogin() {
        val bottomSheetFragment = AuthenticationBottomSheet()
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.getTag())
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
                            this.statusImage.show()
                            this.btnLoadPost.isClickable = false
                        }
                    }
                    Result.Status.SUCCESS -> {
                        /**
                         * add post data
                         * */
                        subscribeUi(it.data!![0].data!!.children!![0].data)
                        /**
                         * if comment list not empty then set the list otherwise hide [RecyclerView]
                         * */
                        val commentList = it.data[1].data!!.children!!
                        if (commentList.isNotEmpty()) {
                            lifecycleScope.launch {
                                viewModel.setCommentData(commentList)
                            }

                        } else {
                            binding.contentLayout.commentRecyclerView.visibility = View.GONE
                        }
                        binding.apply {
                            this.statusImage.hide()
                            this.btnLoadPost.isClickable = true
                        }
                    }
                    Result.Status.ERROR -> {
                        binding.apply {
                            this.statusImage.hide()
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


    private fun subscribeAdapter() {

        viewModel.getCommentList().observe(this, Observer {
            when (it.status) {
                Result.Status.SUCCESS -> {
                    binding.contentLayout.commentRecyclerView.apply {
                        this.visibility = View.VISIBLE
                        this.adapter = CommentAdapter(it.data!!, this@MainActivity)
                    }
                }
                Result.Status.ERROR -> {
                    it.message!!.snack(Color.LTGRAY, binding.root)
                }

                else -> {

                }
            }

        })
    }
}
