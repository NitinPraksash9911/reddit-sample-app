package `in`.nitin.redditsample.datasource.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RedditModel {
    @SerializedName("kind")
   var kind: String? = null

    @SerializedName("data")
   var data: Data? = null

}