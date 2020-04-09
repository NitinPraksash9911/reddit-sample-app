package `in`.nitin.redditsample.datasource.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Preview {
    @SerializedName("images")
   var images: List<Image>? =
        null

    @SerializedName("enabled")
   var enabled: Boolean? = null

}