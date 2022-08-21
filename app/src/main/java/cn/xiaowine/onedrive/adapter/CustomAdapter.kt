package cn.xiaowine.onedrive.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cn.xiaowine.onedrive.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target


class CustomAdapter(private val context: Context, private val dataSet: ArrayList<ArrayList<String>>? = ArrayList()) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int, view: View)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }


    fun addData(value: ArrayList<String>, position: Int = itemCount) {
        dataSet!!.add(position, value)
        notifyItemInserted(position)
        notifyItemChanged(position)
    }


    fun removeData(position: Int) {
        dataSet!!.removeAt(position)
        notifyItemRemoved(position)
        notifyItemChanged(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeAllData() {
        dataSet!!.clear()
        notifyDataSetChanged()
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView
        val fileSize: TextView
        val imageView: ImageView

        init {
            textView = view.findViewById(R.id.FileTitle)
            fileSize = view.findViewById(R.id.FileSize)
            imageView = view.findViewById(R.id.imageView)
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.items, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.setOnClickListener {
            listener.onItemClick(position, viewHolder.textView)
        }
        viewHolder.textView.text = dataSet!![position][0]
        viewHolder.fileSize.text = dataSet[position][1]
        try {
            if (dataSet[position][2].isEmpty()) {
                viewHolder.imageView.visibility = View.GONE
            }

            Glide.with(context).load("url").listener(object : RequestListener<Drawable?> {
                override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable?>, isFirstResource: Boolean): Boolean {
                    return false
                }

                override fun onResourceReady(resource: Drawable?, model: Any, target: Target<Drawable?>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
//                    val lp: ViewGroup.LayoutParams = viewHolder.itemView.layoutParams
////                    lp.height = viewHolder.textView.height + viewHolder.fileSize.height + 20
//                    lp.width = viewHolder.itemView.width
//                    viewHolder.itemView.layoutParams = lp
                    viewHolder.imageView.background = null
                    return false
                }
            }).error(R.drawable.error).load(dataSet[position][2]).into(viewHolder.imageView)

//            Glide.with(context).asBitmap().placeholder(R.drawable.loading).format(DecodeFormat.PREFER_RGB_565).load().dontAnimate().sizeMultiplier(0.3f).override(viewHolder.imageView.width, viewHolder.imageView.height).into(viewHolder.imageView)
        }
    catch (_: IndexOutOfBoundsException) {
            viewHolder.imageView.visibility = View.GONE
        }


    }


    override fun getItemCount() = dataSet!!.size
}
