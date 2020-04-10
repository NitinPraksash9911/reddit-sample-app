package `in`.nitin.redditsample.ui

import `in`.nitin.redditsample.R
import `in`.nitin.redditsample.application.RedditApplication
import `in`.nitin.redditsample.databinding.FragmentBottomSheetDialogBinding
import `in`.nitin.redditsample.datasource.network.Result
import `in`.nitin.redditsample.viewmodel.RedditViewModel
import `in`.nitin.redditsample.viewmodel.ViewModelProviderFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Inject


class AuthenticationBottomSheet : BottomSheetDialogFragment() {

    lateinit var fragBinding: FragmentBottomSheetDialogBinding
    lateinit var redditViewModel: RedditViewModel


    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setStyle(DialogFragment.STYLE_NORMAL, R.style.BottomSheetStyle)

        RedditApplication.getComponent()!!.inject(this)
        redditViewModel =
            ViewModelProvider(activity!!, providerFactory).get(RedditViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_bottom_sheet_dialog,
            container,
            false
        )
        fragBinding.lifecycleOwner = this

        fragBinding.viewmodel = redditViewModel

        observeLogin()
        return fragBinding.root
    }

    private fun observeLogin() {
        redditViewModel.getLogindata().observe(viewLifecycleOwner, Observer {

            when (it.status) {
                Result.Status.LOADING -> {
                    fragBinding.statusImage.show()

                }
                Result.Status.SUCCESS -> {
                    fragBinding.statusImage.hide()
                    Toast.makeText(context, "Login Success", Toast.LENGTH_LONG).show()

                }
                Result.Status.ERROR -> {
                    fragBinding.statusImage.hide()
                    Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                }
            }
        })
    }


}