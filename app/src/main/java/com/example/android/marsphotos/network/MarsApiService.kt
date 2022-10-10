package com.example.android.marsphotos.network

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.invoke
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

private const val BASE_URL =
    "https://android-kotlin-fun-mars-server.appspot.com/photos"

class MarsApiService {

    private fun getPhotos(): String {
        val connection = connect()
        if (connection.toString().contains("Error")) {
            Log.d("ASD", "Connection Error")
            return connection.toString()
        }
        //DOWNLOAD
        try {
            val con = connection as HttpURLConnection
            //if response is HTTP OK
            if (con.responseCode == 200) {
                //GET INPUT FROM STREAM
                val inputStream = BufferedInputStream(con.inputStream)
                val bufferedReader = BufferedReader(InputStreamReader(inputStream))

                val jsonData = StringBuffer()
                var line: String?

                do {
                    line = bufferedReader.readLine()

                    if (line == null) {
                        break
                    }
                    jsonData.append(line + "\n")

                } while (true)

                //CLOSE RESOURCES
                bufferedReader.close()
                inputStream.close()

                //RETURN JSON
                return jsonData.toString()

            } else {
                Log.d("ASD", "Error")
                return "Error " + con.responseMessage
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.d("ASD", "Stacktrace Error")
            return "Error " + e.message
        }
    }

    private fun connect(): Any {
        try {
            val url = URL(BASE_URL)
            val con = url.openConnection() as HttpURLConnection

            //CON PROPS
            con.requestMethod = "GET"
            con.connectTimeout = 15000
            con.readTimeout = 15000
            con.doInput = true

            return con

        } catch (e: MalformedURLException) {
            e.printStackTrace()
            Log.d("ASD", "MalformedURLException Error")
            return "URL ERROR " + e.message

        } catch (e: IOException) {
            e.printStackTrace()
            Log.d("ASD", "IOException Error")
            return "CONNECT ERROR " + e.message
        }
    }

    fun parse(): List<MarsPhoto> {
        try {
            val ja = JSONArray(getPhotos())
            var jo: JSONObject
            val list = mutableListOf<MarsPhoto>()

            for (i in 0 until ja.length()) {
                jo = ja.getJSONObject(i)

                val id = jo.getString("id")
                val imgSrcUrl = jo.getString("img_src")

                list.add(MarsPhoto(id, imgSrcUrl))
            }
            return list
        } catch (e: JSONException) {
            Log.d("ASD", "JSON Error")
            e.printStackTrace()
            return listOf()
        }
    }
}

object MarsApi {
    //val objMarsApi:MarsApiService = MarsApiService.
}