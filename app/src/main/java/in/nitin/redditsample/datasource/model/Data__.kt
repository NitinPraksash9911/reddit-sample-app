package `in`.nitin.redditsample.datasource.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Data__ {
    @SerializedName("modhash")
   var modhash: String? = null

    @SerializedName("dist")
   var dist: Any? = null

    @SerializedName("children")
   var children: List<Child_>? = null

    @SerializedName("after")
   var after: Any? = null

    @SerializedName("before")
   var before: Any? = null

}