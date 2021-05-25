package com.example.giche


import android.os.Bundle
import android.view.MenuItem
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
var Ip= 0
var senha = "0"
var tsenha = 0

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIp()
        changeToMain()
    }

    fun changeToMain(){
        setContentView(R.layout.activity_main)
    }

    fun changeTalho(view: View) {
        currId = 1
       changeToOptionPicked()
    }

    fun changePeixaria(view: View) {
        currId = 2
        changeToOptionPicked()
    }

    fun changeToOptionPicked(){
        getCurrent()
        getAverage()
        getNext()
        Timer().schedule(timerTask { getNext()
            getCurrent()
            getAverage()}
            ,10000, 10000)
        setContentView(R.layout.option_picked_main)
        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home ->{
                changeToMain()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun getAverage() {
        val cache = DiskBasedCache(cacheDir, 1024 * 1024)
        val network = BasicNetwork(HurlStack())
        val requestQueue = RequestQueue(cache, network).apply {
            start()
        }
        val url = "http://192.168.1."+Ip+":8080/getAverage?id=" + currId
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
        val url = "http://192.168.1."+Ip+":8080/actualNum?id=" + currId
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
        val url = "http://192.168.1."+Ip+":8080/nextSenha?id=" + currId
        val stringRequest = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->
                nextSenha.text = response.toString()
            },
            Response.ErrorListener {
            })
        requestQueue.add(stringRequest)
    }

    fun getIp() {
        val cache = DiskBasedCache(cacheDir, 1024 * 1024)
        val network = BasicNetwork(HurlStack())
        val requestQueue = RequestQueue(cache, network).apply {
            start()
        }
        for (i in 0..9) {
            val ipHelper = i + Ip
            val url = "http://192.168.1."+ipHelper+":8080/checkIp"
            val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener<String> { response ->
                    if(response.toString().equals("true"))
                        Ip = ipHelper
                },
                Response.ErrorListener {
                })
            requestQueue.add(stringRequest)
        }
    }

    fun getNew(view : View) {
        val cache = DiskBasedCache(cacheDir, 1024 * 1024)
        val network = BasicNetwork(HurlStack())
        val requestQueue = RequestQueue(cache, network).apply {
            start()
        }
        val url = "http://192.168.1."+Ip+":8080/getNew?id=" + currId
        val stringRequest = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->
                senha = response.toString()
                tsenha = currId
                newSenhaPress()
            },
            Response.ErrorListener {
            })
        requestQueue.add(stringRequest)
    }

    fun newSenhaPress(){
        buttonSenha.visibility = View.INVISIBLE
        headerSenha.visibility = View.VISIBLE
        currSenha.visibility = View.VISIBLE
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
        val helper = response.div(60)
        return "~" + helper.toString() + "m"
    }
}



