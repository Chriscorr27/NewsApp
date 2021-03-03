package com.example.newsapp

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest


class MainActivity : AppCompatActivity(), NewsItemClicked {
    private  lateinit var  madapter : NewsListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<RecyclerView>(R.id.recyclerView).layoutManager = LinearLayoutManager(this)
        fetchData("")
        madapter = NewsListAdapter(this)
        this.findViewById<RecyclerView>(R.id.recyclerView).adapter = madapter

        findViewById<ImageView>(R.id.logo).setOnClickListener {
            fetchData("")
        }
    }
    private fun  fetchData(q:String){
        findViewById<ProgressBar>(R.id.pBar).visibility=ProgressBar.VISIBLE
        val url = "https://gnews.io/api/v4/top-headlines?token=f3be8112730629b4cf46efa76f300f99&lang=en&country=in&q=$q"
        val jsonObjectRequest = JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                {
                    val newJsonArray = it.getJSONArray("articles")
                    val newsArray=ArrayList<News>()
                    for(i in 0 until newJsonArray.length())
                    {
                        val newJsonObject = newJsonArray.getJSONObject(i)
                        val news = News(
                                newJsonObject.getString("title"),
                                newJsonObject.getJSONObject("source").getString("name"),
                                newJsonObject.getString("image"),
                                newJsonObject.getString("url")
                        )
                        newsArray.add(news)
                    }
                    madapter.update(newsArray)
                    findViewById<ProgressBar>(R.id.pBar).visibility=ProgressBar.INVISIBLE
                },
                {
                    Toast.makeText(this,"Error",Toast.LENGTH_LONG).show()
                    findViewById<ProgressBar>(R.id.pBar).visibility=ProgressBar.INVISIBLE
                }
        )

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    override fun onItemClicked(item: News) {
        val url = item.url
        val builder =  CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }

    fun searchEvent(view: View) {
        val q=findViewById<EditText>(R.id.search).text.toString()
        fetchData(q)
    }
}

