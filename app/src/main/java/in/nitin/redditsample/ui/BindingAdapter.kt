package `in`.nitin.redditsample.ui

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout


@BindingAdapter("app:setError")
fun setError(textinput: TextInputLayout, error: String?) {
    textinput.error = error
}
