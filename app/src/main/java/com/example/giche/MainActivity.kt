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
var senhaTalho = "-1"
var senhaPeixe = "-1"
var timer = Timer()

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIp()
        changeToMain()
    }

    fun changeToMain(){
        timer.cancel()
        setContentView(R.layout.activity_main)
    }

    fun changeTalho(view: View) {
        currId = 1
        changeToOptionPicked()
        if("-1".compareTo(senhaTalho) != 0)
            getSenhaPressed()
    }

    fun changePeixaria(view: View) {
        currId = 2
        changeToOptionPicked()
        if("-1".compareTo(senhaPeixe) != 0)
            getSenhaPressed()
    }

    fun changeToOptionPicked() {
        timer = setTimer()
        setContentView(R.layout.option_picked_main)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
    }

    fun setTimer() : Timer{
        val timer = Timer()
        timer.scheduleAtFixedRate(timerTask {
            getNext()
            getCurrent()
            getAverage()
        },0,10000)
        return timer
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
                if(currId == 1) {
                    if (response.toInt().compareTo(senhaTalho.toInt() + 1) == 0)
                        enableGetSenha()
                }
                else{
                    if (response.toInt().compareTo(senhaPeixe.toInt() + 1) == 0)
                        enableGetSenha()
                }
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
                if(currId == 1)
                    senhaTalho = response.toString()
                else
                    senhaPeixe = response.toString()
                getSenhaPressed()
            },
            Response.ErrorListener {
            })
        requestQueue.add(stringRequest)
    }

    fun getSenhaPressed(){
        buttonSenha.visibility = View.INVISIBLE
        headerSenha.visibility = View.VISIBLE
        currSenha.visibility = View.VISIBLE

        if(currId == 1)
            currSenha.text = senhaTalho
        else
            currSenha.text = senhaPeixe
    }

    fun enableGetSenha(){
        headerSenha.visibility = View.INVISIBLE
        currSenha.visibility = View.INVISIBLE
        buttonSenha.visibility = View.VISIBLE
        if(currId == 1) {
            senhaTalho = "-1"
        }
        else {
            senhaPeixe = "-1"
        }
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



