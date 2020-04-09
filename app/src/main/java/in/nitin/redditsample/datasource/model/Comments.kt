package `in`.nitin.redditsample.datasource.model

import com.multilevelview.models.RecyclerViewItem

class Comments(
    level: Int, var comment: String, var postedOn: String,
    var author: String
) : RecyclerViewItem(level) {


}