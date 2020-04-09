package `in`.nitin.redditsample.datasource.model

import `in`.nitin.redditsample.datasource.model.Data_
import com.google.gson.annotations.SerializedName

class Child {
    @SerializedName("kind")
   var kind: String? = null

    @SerializedName("data")
   var data: Data_? = null

}