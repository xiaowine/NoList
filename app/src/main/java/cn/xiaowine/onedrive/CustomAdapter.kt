package cn.xiaowine.onedrive

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class CustomAdapter(private val context: Context, private val dataSet: ArrayList<ArrayList<String>>? = ArrayList()) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
    private lateinit var listener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int, view: View)
    }

    fun setOnItemClickListener(listenser: OnItemClickListener) {
        this.listener = listenser
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
            val params = LinearLayout.LayoutParams(100, 100)
            imageView.layoutParams = params
        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.text_row_item, viewGroup, false)
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
            Glide.with(context).load(dataSet[position][2]).into(viewHolder.imageView)
        } catch (_: Exception) {
            viewHolder.imageView.visibility = View.GONE
        }


    }


    override fun getItemCount() = dataSet!!.size
}
