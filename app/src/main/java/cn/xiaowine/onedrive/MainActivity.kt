package cn.xiaowine.onedrive


import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.xiaowine.onedrive.utils.Utils.formatSize
import com.drake.net.Post
import com.drake.net.utils.scopeNetLife
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import kotlin.concurrent.thread
import org.json.JSONObject


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CustomAdapter
    private var resArray: ArrayList<String> = ArrayList()
    val activity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Title";
        toolbar.subtitle = "SubTitle"

        findViewById<FloatingActionButton>(R.id.floatingActionButton).setOnClickListener(activity)
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = RecyclerView.VERTICAL
        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL))
        adapter = CustomAdapter(activity)
        adapter.setOnItemClickListener(object : CustomAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, view: View) {
                Toast.makeText(activity, "${(view as TextView).text} 开始下载！！", Toast.LENGTH_SHORT).show()
                val request = DownloadManager.Request(Uri.parse(resArray[position]))
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setTitle(view.text.toString());
                val saveFile = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), view.text.toString())
                request.setDestinationUri(Uri.fromFile(saveFile))
                val manager = activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val downloadId = manager.enqueue(request)
//                val uri = Uri.parse(resArray[position])
//                startActivity(Intent(Intent.ACTION_VIEW, uri))
            }
        })
        recyclerView.adapter = adapter
        refresh()
    }


    override fun onClick(view: View) {
        when (view.id) {
            R.id.floatingActionButton -> refresh()
        }
    }

    private fun refresh() {
        val progressDialog = ProgressDialog(activity)
        progressDialog.setTitle("加载中")
        progressDialog.show()
        if (adapter.itemCount != 0) {
            adapter.removeAllData()
        }
        thread {
            scopeNetLife {
                val data = Post<String>("http://xiaowine.chinanorth3.cloudapp.chinacloudapi.cn:5244/api/public/path") {
                    param("page_num", "1")
                    param("page_size", "30")
                    param("password", "")
                    param("path", "/")
                }.await()
                val jsonArray = JSONObject(data).getJSONObject("data").getJSONArray("files")
                for (i in 0 until jsonArray.length()) {
                    val thisObject = jsonArray.getJSONObject(i)
                    if (thisObject.getInt("type") == 1) {
                        continue
                    }
                    resArray.add(thisObject.getString("url"))
                    recyclerView.post {
                        val list = arrayListOf(thisObject.getString("name"), formatSize(activity, thisObject.getInt("size").toLong()), thisObject.getString("thumbnail"))
                        adapter.addData(list)

                    }
                }

                progressDialog.dismiss()
            }.catch {
                recyclerView.post {
                    adapter.addData(arrayListOf("错误！！！！", ""))
                }
                progressDialog.dismiss()
            }
        }
    }
}