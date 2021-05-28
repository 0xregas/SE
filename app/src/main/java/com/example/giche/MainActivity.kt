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
var ticketTalho = "-1"
var ticketPeixe = "-1"
var timer = Timer()

class MainActivity : AppCompatActivity() {

    //-----------------------------------------------------
    //------------------UI, Transitions--------------------
    //-----------------------------------------------------

    //Creates the app.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getIp()
        changeToMain()
    }

    //Changes to the main screen and stops the timer , so it doesn't update when the resources aren't being shown.
    fun changeToMain(){
        timer.cancel()
        setContentView(R.layout.activity_main)
    }

    // Changes the screen and sets the id to 1. If a ticket was already taken for that id, it also disables the button.
    fun changeTalho() {
        currId = 1
        changeToOptionPicked()
        if("-1".compareTo(ticketTalho) != 0)
            getTicketPressed()
    }

    // Changes the screen and sets the id to 2. If a ticket was already taken for that id, it also disables the button.
    fun changePeixaria() {
        currId = 2
        changeToOptionPicked()
        if("-1".compareTo(ticketPeixe) != 0)
            getTicketPressed()
    }

    // Changes the screen so it represents the ticket the user has instead of the button to get one.
    fun getTicketPressed(){
        buttonSenha.visibility = View.INVISIBLE
        headerSenha.visibility = View.VISIBLE
        currTicket.visibility = View.VISIBLE

        if(currId == 1)
            currTicket.text = ticketTalho
        else
            currTicket.text = ticketPeixe
    }

    // Re-enables the button to get a ticket.
    fun enableGetTicket(){
        headerSenha.visibility = View.INVISIBLE
        currTicket.visibility = View.INVISIBLE
        buttonSenha.visibility = View.VISIBLE
        if(currId == 1) {
            ticketTalho = "-1"
        }
        else {
            ticketPeixe = "-1"
        }
    }

    // Changes to data screen
    fun changeToOptionPicked() {
        timer = setTimer()
        setContentView(R.layout.option_picked_main)
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
    }


    // Sets up action bar with a button to go to the main screen
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home ->{
                changeToMain()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    //-----------------------------------------------------
    //----------------------Requests-----------------------
    //-----------------------------------------------------

    // Gets the IP where the server is at. As long as the server responds with true
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
    // Gets the average time between tickets in seconds
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

    // Gets the current ticket being taken care of
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
                    if (response.toInt().compareTo(ticketTalho.toInt() + 1) == 0)
                        enableGetTicket()
                }
                else{
                    if (response.toInt().compareTo(ticketPeixe.toInt() + 1) == 0)
                        enableGetTicket()
                }
            },
            Response.ErrorListener {
            })
        requestQueue.add(stringRequest)
    }

    // Gets the number of the ticket the user will get if he does the request now
    fun getNext() {
        val cache = DiskBasedCache(cacheDir, 1024 * 1024)
        val network = BasicNetwork(HurlStack())
        val requestQueue = RequestQueue(cache, network).apply {
            start()
        }
        val url = "http://192.168.1."+Ip+":8080/nextTicket?id=" + currId
        val stringRequest = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->
                nextTicket.text = response.toString()
            },
            Response.ErrorListener {
            })
        requestQueue.add(stringRequest)
    }

    // Gets a ticket for the user.
    fun getNew() {
        val cache = DiskBasedCache(cacheDir, 1024 * 1024)
        val network = BasicNetwork(HurlStack())
        val requestQueue = RequestQueue(cache, network).apply {
            start()
        }
        val url = "http://192.168.1."+Ip+":8080/getNew?id=" + currId
        val stringRequest = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->
                if(currId == 1)
                    ticketTalho = response.toString()
                else
                    ticketPeixe = response.toString()
                getTicketPressed()
            },
            Response.ErrorListener {
            })
        requestQueue.add(stringRequest)
    }


    //-----------------------------------------------------
    //------------------Regular Updater--------------------
    //-----------------------------------------------------

    //Updates the necessary resources every 10 seconds.
    fun setTimer() : Timer{
        val timer = Timer()
        timer.scheduleAtFixedRate(timerTask {
            getNext()
            getCurrent()
            getAverage()
        },0,10000)
        return timer
    }

}

//Translates seconds into minutes and adds some bounds.
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



