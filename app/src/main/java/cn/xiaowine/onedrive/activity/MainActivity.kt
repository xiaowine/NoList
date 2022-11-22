@file:Suppress("DEPRECATION")

package cn.xiaowine.onedrive.activity


import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.xiaowine.onedrive.R
import cn.xiaowine.onedrive.adapter.CustomAdapter
import cn.xiaowine.onedrive.utils.Utils.formatSize
import com.drake.net.Net
import kotlin.concurrent.thread
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    private var total: Int = 0
    private var page: Int = 1
    private var loading: Boolean = false
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CustomAdapter
    private var resArray: ArrayList<JSONObject> = ArrayList()
    val activity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = getString(R.string.app_name)
//        toolbar.subtitle = "SubTitle"
        recyclerView = findViewById(R.id.recyclerview)
        val mLayoutManager = GridLayoutManager(activity, 2, RecyclerView.VERTICAL, false)
        recyclerView.layoutManager = mLayoutManager
        recyclerView.setItemViewCacheSize(2000)
        recyclerView.isDrawingCacheEnabled = true
        recyclerView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (total == recyclerView.childCount) {
                    Toast.makeText(activity, "已经加载完了", Toast.LENGTH_SHORT).show()
                    return
                }
                if (!loading && !recyclerView.canScrollVertically(1)) {
                    loading = true
                    page = 1
                    Toast.makeText(activity, "加载更多", Toast.LENGTH_SHORT).show()
                    refresh()
                }
                if (!loading && !recyclerView.canScrollVertically(-1)) {
                    loading = true
                    page += 1
                    Toast.makeText(activity, "加载中", Toast.LENGTH_SHORT).show()
                    refresh(false)
                }
            }
        })

        adapter = CustomAdapter(activity)
        adapter.setOnItemClickListener(object : CustomAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, view: View) {
                val intent = Intent(activity, PhotoActivity::class.java)
                val bundle = Bundle()
                bundle.putString("url", resArray[position].getString("thumb"))
                bundle.putString("imageInfo", resArray[position].getString("name"))
                bundle.putString("size", resArray[position].getString("size"))
                intent.putExtras(bundle)
                startActivity(intent)

//                val uri = Uri.parse(resArray[position])
//                startActivity(Intent(Intent.ACTION_VIEW, uri))
            }
        })
        recyclerView.adapter = adapter
        refresh()
    }


    private fun refresh(isInit: Boolean = true) {
        val progressDialog = ProgressDialog(activity)
        progressDialog.setTitle(if (isInit) "加载中" else "刷新中")
        progressDialog.show()
        if (isInit && adapter.itemCount != 0) {
            adapter.removeAllData()
        }
        thread {
            try {
                val data = Net.post("https://alist.xiaowine.cc/api/fs/list") { json(JSONObject("""{"path":"/OneDrive","password":"","page":$page,"per_page":30,"refresh":false}""")) }.execute<String>()
                val jsonArray = JSONObject(data).getJSONObject("data").getJSONArray("content")
                if (isInit) total = JSONObject(data).getJSONObject("data").getInt("total")
                for (i in 0 until jsonArray.length()) {
                    val thisObject = jsonArray.getJSONObject(i)
                    if (thisObject.getInt("type") == 1) {
                        continue
                    }
                    resArray.add(thisObject)
                    recyclerView.post {
                        val list = arrayListOf(thisObject.getString("name"), formatSize(activity, thisObject.getInt("size").toLong()), thisObject.getString("thumb"))
                        adapter.addData(list)
                    }
                }
                progressDialog.dismiss()
            } catch (_: Exception) {
                recyclerView.post {
                    adapter.addData(arrayListOf("错误！！！！", ""))
                }
                progressDialog.dismiss()
            }
            loading = false
        }
    }
}
