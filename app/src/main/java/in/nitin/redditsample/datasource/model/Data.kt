package `in`.nitin.redditsample.datasource.model

import `in`.nitin.redditsample.datasource.model.Child
import com.google.gson.annotations.SerializedName

class Data {
    @SerializedName("modhash")
   var modhash: String? = null

    @SerializedName("dist")
   var dist: Any? = null

    @SerializedName("children")
   var children: List<Child>? = null

    @SerializedName("after")
   var after: Any? = null

    @SerializedName("before")
   var before: Any? = null

}