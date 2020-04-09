package `in`.nitin.redditsample.ui

import `in`.nitin.redditsample.R
import `in`.nitin.redditsample.application.RedditApplication
import `in`.nitin.redditsample.databinding.ActivityMainBinding
import `in`.nitin.redditsample.datasource.network.Result
import `in`.nitin.redditsample.viewmodel.RedditViewModel
import `in`.nitin.redditsample.viewmodel.ViewModelProviderFactory
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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

                        binding.statusTv.text = "Loading"

                    }
                    Result.Status.SUCCESS -> {

                        binding.statusTv.text = "Success"


                    }
                    Result.Status.ERROR -> {
                        binding.statusTv.text = it.message


                    }
                }
            }
        })

    }

}
