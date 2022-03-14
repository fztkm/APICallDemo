package com.fztkm.apicalldemo

import android.app.Dialog
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.fztkm.apicalldemo.databinding.ActivityMainBinding
import com.fztkm.apicalldemo.databinding.DialogCustomProgressBinding
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL

class MainActivity : AppCompatActivity() {

    companion object{
        private val MOCKY_URL = "https://run.mocky.io/v3/fdf47237-46ae-4a45-9bef-61e999704b72"
    }

    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        CallAPILoginAsyncTask().execute()
    }

    private inner class CallAPILoginAsyncTask(): AsyncTask<Any, Void, String>(){

        private lateinit var customProgressDialog: Dialog

        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog()
        }

        override fun doInBackground(vararg p0: Any?): String {
            var result: String

            var connection: HttpURLConnection? = null

            try{
                val url = URL(MOCKY_URL)
                connection = (url.openConnection() as HttpURLConnection).apply {
                    doInput = true
                    doOutput = true
                }
                val httpResult: Int = connection.responseCode

                if(httpResult == HttpURLConnection.HTTP_OK){
                    val inputStream = connection.inputStream

                    val reader = BufferedReader(
                        InputStreamReader(inputStream))
                    val stringBuilder = StringBuilder()
                    var line: String?
                    try{
                        while(reader.readLine().also { line = it } != null){
                            stringBuilder.append(line + "\n")
                        }
                    }catch(e: IOException){
                        e.printStackTrace()
                    }finally {
                        try{
                            inputStream.close()
                        }catch (e: IOException){
                            e.printStackTrace()
                        }
                    }
                    result = stringBuilder.toString()
                }else{
                    result = connection.responseMessage
                }
            }catch (e: SocketTimeoutException){
                result = "Connection Timeout"
            }catch (e:Exception){
                result = "Error: " + e.message
            }finally {
                connection?.disconnect()
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            cancelProgressDialog()
            result?.let {
                Log.i("JSON RESPONSE RESULT", it)
            }
        }

        private fun showProgressDialog(){
            customProgressDialog = Dialog(this@MainActivity)
            val dialogBinding = DialogCustomProgressBinding.inflate(layoutInflater)
            customProgressDialog.setContentView(dialogBinding.root)
            customProgressDialog.show()
        }

        private fun cancelProgressDialog(){
            customProgressDialog.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}