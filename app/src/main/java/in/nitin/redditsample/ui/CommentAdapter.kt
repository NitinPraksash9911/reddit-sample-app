package `in`.nitin.redditsample.ui

import `in`.nitin.redditsample.databinding.ItemLayoutBinding
import `in`.nitin.redditsample.datasource.model.Comments
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.multilevelview.MultiLevelAdapter
import kotlinx.android.synthetic.main.item_layout.view.*

class CommentAdapter(private var mListItem: List<Comments>, private var mContext: Context) :
    MultiLevelAdapter(mListItem) {

    private lateinit var mHolder: CommentViewHolder


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return CommentViewHolder(
            ItemLayoutBinding.inflate(
                LayoutInflater.from(mContext),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        mHolder = holder as CommentViewHolder

        holder.bindTo(mListItem.get(position), getItemViewType(position))

        val density = mContext.resources.displayMetrics.density
        (mHolder.itemView.parentLayout.layoutParams as MarginLayoutParams).leftMargin =
            (getItemViewType(position) * 20 * density + 0.5f).toInt()

    }

    class CommentViewHolder(private val binding: ItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindTo(comments: Comments, viewPosition: Int) {
            when (viewPosition) {
                1 -> binding.parentLayout.setCardBackgroundColor(Color.parseColor("#404040"))
                2 -> binding.parentLayout.setCardBackgroundColor(Color.parseColor("#606060"))
                else -> binding.parentLayout.setCardBackgroundColor(Color.parseColor("#303030"))
            }
            binding.comment = comments
            binding.executePendingBindings()
        }

    }

}