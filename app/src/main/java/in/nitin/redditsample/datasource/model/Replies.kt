package `in`.nitin.redditsample.datasource.model

import androidx.annotation.Nullable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Replies {
    @SerializedName("kind")
    @Nullable
   var kind: String? = null

    @SerializedName("data")
   var data: Data__? = null
}