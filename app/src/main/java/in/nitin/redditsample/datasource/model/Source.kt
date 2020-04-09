package `in`.nitin.redditsample.datasource.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Source {
    @SerializedName("url")
   var url: String? = null

    @SerializedName("width")
   var width: Int? = null

    @SerializedName("height")
   var height: Int? = null

}