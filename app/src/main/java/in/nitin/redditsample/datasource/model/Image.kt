package `in`.nitin.redditsample.datasource.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Image {
    @SerializedName("source")
   var source: Source? = null

    @SerializedName("resolutions")
   var resolutions: List<Resolution>? =
        null

    @SerializedName("variants")
   var variants: Variants? = null

    @SerializedName("id")
   var id: String? = null

}