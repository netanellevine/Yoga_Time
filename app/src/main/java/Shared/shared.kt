package Shared


import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.yogatime.R
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.URL
import android.app.Activity
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast

data class Lesson(@SerializedName("lessonName") val lessonName: String,
                  @SerializedName("maxNumberOfParticipants") val numberOfParticipants: Int,
                  @SerializedName("level") val level: String,
                  @SerializedName("price") val price: Double,
                  @SerializedName("description") val description: String,
                  @SerializedName("ParticipantsList") var ParticipantsList: MutableList<String> = mutableListOf() )

data class fullLesson(@SerializedName("doc_id") val docId: String,
                      @SerializedName("date") val date: String,
                      @SerializedName("lesson") val lesson: Lesson)

data class instructorLesson(@SerializedName("date") val date: String,
                      @SerializedName("lesson") val lesson: Lesson)

data class instructorStats(@SerializedName("avgParticipants") val avgParticipants: Double,
                            @SerializedName("avgRevenue") val avgRevenue: Double,
                           @SerializedName("totalLessons") val totalLessons: Int,
                           @SerializedName("totalRevenue") val totalRevenue: Double)


data class participantFilter(@SerializedName("instructorName") val instructorName: String = "any",
                             @SerializedName("lessonName") val lessonName: String = "any",
                             @SerializedName("level") val level: List<String> = listOf(
                                 "string"
                             ),
                             @SerializedName("price") val price: Double =0.0,
                             @SerializedName("date") val date: String = "any" )

const val server = "y-time-1132316634.eu-west-3.elb.amazonaws.com"


@RequiresApi(Build.VERSION_CODES.N)
fun getRequest(path:String, requestMap: HashMap<String,Any>): String {
        var queryStr = ""
        requestMap.forEach { (key, value) ->
            queryStr += "${key}=${value}&"
        }
        if (queryStr != "") {
            queryStr = queryStr.substring(0, queryStr.length - 1)
            queryStr = "?$queryStr"
        }
        val url = URL("http://${server}/${path}${queryStr}")
        val connection = url.openConnection()
        var res = ""
        BufferedReader(InputStreamReader(connection.getInputStream())).use { inp ->
            var line: String?

            while (inp.readLine().also { line = it } != null) {
                res += line
            }
        }
        Log.d("Server", res)
    return res
}

fun post(path:String, postMap: HashMap<String,Any>): String {
    val postData = Gson().toJson(postMap).toString()
    val url = URL("http://${server}/${path}")
    Log.d("POST request",postData)
    val conn = url.openConnection()
    conn.doOutput = true
    conn.setRequestProperty("Content-Type", "application/json")
    conn.setRequestProperty("Content-Length", postData.length.toString())
    conn.setRequestProperty("requestMethod","POST")


    var res = ""
    DataOutputStream(conn.getOutputStream()).use { it.writeBytes(postData) }
    BufferedReader(InputStreamReader(conn.getInputStream())).use { bf ->
        var line: String?

        while (bf.readLine().also { line = it } != null) {
            res += line
        }

    }
    return res
}



fun postRequest(path:String, postMap: HashMap<String,Any>): String {
    val postData = postMap["lesson"] as String
    var queryStr: String = "userId="+postMap["userId"]
    queryStr+= "&key="+ postMap["key"]
    queryStr+= "&userToAdd="+ postMap["userToAdd"]

    val url = URL("http://${server}/${path}?${queryStr}")
    Log.d("POST request",postData)
    val conn = url.openConnection()
    conn.doOutput = true
    conn.setRequestProperty("Content-Type", "application/json")
    conn.setRequestProperty("Content-Length", postData.length.toString())
    conn.setRequestProperty("requestMethod","POST")


    var res = ""
    DataOutputStream(conn.getOutputStream()).use { it.writeBytes(postData) }
    BufferedReader(InputStreamReader(conn.getInputStream())).use { bf ->
        var line: String?

        while (bf.readLine().also { line = it } != null) {
            res += line
        }

    }
    return res
}

fun postRequestValidate(path:String, postMap: HashMap<String,Any>): String {
    val postData = postMap["lesson"] as String
    var queryStr: String = "userId="+postMap["userId"]
    queryStr+= "&key="+ postMap["key"]

    val url = URL("http://${server}/${path}?${queryStr}")
    Log.d("POST request",postData)
    val conn = url.openConnection()
    conn.doOutput = true
    conn.setRequestProperty("Content-Type", "application/json")
    conn.setRequestProperty("Content-Length", postData.length.toString())
    conn.setRequestProperty("requestMethod","POST")


    var res = ""
    DataOutputStream(conn.getOutputStream()).use { it.writeBytes(postData) }
    BufferedReader(InputStreamReader(conn.getInputStream())).use { bf ->
        var line: String?

        while (bf.readLine().also { line = it } != null) {
            res += line
        }

    }
    return res
}


fun postRequestCreation(path:String, postMap: HashMap<String,Any>): String {
    val postData = Gson().toJson(postMap)

    val url = URL("http://${server}/${path}")
    Log.d("POST request",postData)
    val conn = url.openConnection()
    conn.doOutput = true
    conn.setRequestProperty("Content-Type", "application/json")
    conn.setRequestProperty("Content-Length", postData.length.toString())
    conn.setRequestProperty("requestMethod","POST")


    var res = ""
    DataOutputStream(conn.getOutputStream()).use { it.writeBytes(postData) }
    BufferedReader(InputStreamReader(conn.getInputStream())).use { bf ->
        var line: String?

        while (bf.readLine().also { line = it } != null) {
            res += line
        }

    }
    return res
}




fun Toast.showCustomToast(message: String, activity: Activity) {
    val layout = activity.layoutInflater.inflate (
        R.layout.toast,
        activity.findViewById(R.id.toast_container)
    )

    // set the text of the TextView of the message
    val textView = layout.findViewById<TextView>(R.id.toast_text)
    textView.text = message

    // use the application extension function
    this.apply {
        setGravity(Gravity.CENTER, 0, 40)
        duration = Toast.LENGTH_LONG
        view = layout
        show()


    }


}

