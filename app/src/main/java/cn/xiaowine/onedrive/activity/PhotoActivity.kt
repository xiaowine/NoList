package cn.xiaowine.onedrive.activity

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import cn.xiaowine.onedrive.R
import cn.xiaowine.onedrive.utils.Utils
import cn.xiaowine.onedrive.utils.Utils.isNull
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.io.File


class PhotoActivity : Activity() {
    private val activity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)
        savedInstanceState.isNull {
            finishActivity(0)
        }
//        try {
        val data = intent.extras
        val imageViewB = findViewById<ImageView>(R.id.imageViewB)
        val imageInfo = findViewById<TextView>(R.id.imageInfo)
        val imageUrl = data!!.getString("url")!!
        val imageInfoText = data.getString("imageInfo")!!
        val size = data.getString("size")!!

        Glide.with(activity).load(imageUrl).listener(object : RequestListener<Drawable?> {
            override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable?>, isFirstResource: Boolean): Boolean {
                imageInfo.text = "加载失败"
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any, target: Target<Drawable?>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                imageInfo.text = imageInfoText
                imageViewB.background = null
                imageViewB.setOnLongClickListener {
                    AlertDialog.Builder(activity).apply {
                        setTitle(imageInfoText)
                        setMessage("是否下载？\n文件大小：${Utils.formatSize(activity, size.toLong())}")
                        setPositiveButton("下载") { _, _ ->
                            val request = DownloadManager.Request(Uri.parse(imageUrl))
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                            request.setTitle(imageInfoText)
                            val saveFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), imageInfoText)
                            request.setDestinationUri(Uri.fromFile(saveFile))
                            val manager = activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                            manager.enqueue(request)
                        }
                        setNegativeButton("取消") { _, _ -> }
                    }.show()
                    true
                }
                return false
            }
        }).placeholder(R.drawable.loading).error(R.drawable.error).into(imageViewB)

        imageViewB.setOnClickListener {
            finish()
        }

    }
}