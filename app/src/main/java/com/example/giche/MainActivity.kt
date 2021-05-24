package com.example.giche


import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import kotlinx.android.synthetic.main.option_picked_main.*
import java.util.*
import kotlin.concurrent.timerTask


var currId = 0


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun changeTalho(view: View) {
        currId = 1
        getCurrent()
        getAverage()
        getNext();
        Timer().schedule(timerTask { getNext() ;
                                    getCurrent();
                                    getAverage();}
                                    ,10000, 10000);

        setContentView(R.layout.option_picked_main)
    }

    fun changePeixaria(view: View) {
        currId = 2
        getCurrent()
        getAverage()
        getNext()
        Timer().schedule(timerTask { getNext() ;
                                    getCurrent();
                                    getAverage();}
                                    ,10000, 10000);

    setContentView(R.layout.option_picked_main)
    }

    fun getAverage() {
        val cache = DiskBasedCache(cacheDir, 1024 * 1024)
        val network = BasicNetwork(HurlStack())
        val requestQueue = RequestQueue(cache, network).apply {
            start()
        }
        val url = "http://192.168.1.7:8080/getAverage?id=" + currId
        val stringRequest = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->
                val value = roundUp (response.toInt())
                averageTime.text = value
            },
            Response.ErrorListener {
            })
        requestQueue.add(stringRequest)
    }

    fun getCurrent() {
        val cache = DiskBasedCache(cacheDir, 1024 * 1024)
        val network = BasicNetwork(HurlStack())
        val requestQueue = RequestQueue(cache, network).apply {
            start()
        }
        val url = "http://192.168.1.7:8080/actualNum?id=" + currId
        val stringRequest = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->
                actualNum.text = response.toString()
            },
            Response.ErrorListener {
            })
        requestQueue.add(stringRequest)
    }

    fun getNext() {
        val cache = DiskBasedCache(cacheDir, 1024 * 1024)
        val network = BasicNetwork(HurlStack())
        val requestQueue = RequestQueue(cache, network).apply {
            start()
        }
        val url = "http://192.168.1.7:8080/nextSenha?id=" + currId
        val stringRequest = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->
                nextSenha.text = response.toString()
            },
            Response.ErrorListener {
            })
        requestQueue.add(stringRequest)
    }

    fun getNew(view : View) {
        val cache = DiskBasedCache(cacheDir, 1024 * 1024)
        val network = BasicNetwork(HurlStack())
        val requestQueue = RequestQueue(cache, network).apply {
            start()
        }
        val url = "http://192.168.1.7:8080/getNew?id=" + currId
        val stringRequest = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->
                buttonSenha.visibility = View.INVISIBLE
                currSenha.text = response.toString()
                headerSenha.visibility = View.VISIBLE
                currSenha.visibility = View.VISIBLE
            },
            Response.ErrorListener {
            })
        requestQueue.add(stringRequest)
    }

}

fun roundUp(response: Int): String {
    if(response < 60){
        return "-1m"
    }
    else if (response > 3600){
        return "+1h"
    }
    else{
        val helper = response.div(60).toInt()
        return "~" + helper.toString() + "m"
    }
}



