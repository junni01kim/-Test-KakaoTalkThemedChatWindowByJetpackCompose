package com.example.navigationinjava

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.skt.tmap.TMapData
import com.skt.tmap.TMapPoint
import com.skt.tmap.TMapView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    // 지도의 레이아웃 정보를 갖는 변수(RelativeLayout을 이용한다.)
    private lateinit var tmapViewRelativeLayout:RelativeLayout
    // 지도를 사용할 때 사용하는 객체이다.
    // TmapView(Viewer를 속성설정을 위한 변수)
    private lateinit var tMapView:TMapView
    // 장소 데이터를 탐색할 때 쓰는 함수들의 묶음
    private lateinit var tmapData:TMapData

    // Tmap 이용에 가장 중요한 코드들을 초기화하고 나머지는 run()에서 실행된다.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // xml을 연동한다.
        tmapViewRelativeLayout = findViewById(R.id.tmapViewContainer)
        tMapView = TMapView(this)
        tmapData = TMapData()
        tmapViewRelativeLayout.addView(tMapView)

        // 실행 코드가 들어있는 함수
        runner()
    }

    // 앱키를 받기위해선 정보를 받기위한 약간의 네트워크 시간이 존재하는데 텀이 필요하다.
    fun setAppKey(){
        Log.d("log", "setAppKey")
        tMapView.setSKTMapApiKey("GaysikTxiU3X0maxNBKNu6pi4yfFqgfy7x0HNqUv")
        // delayTime은 코루틴을 이용하여 적용시킨다. (Q. 코루틴이 무엇인지 학습하기)
    }

    class DrawRouteThread(val tMapView: TMapView, val tmapData: TMapData, _departure:String, _destination:String) : Thread() {
        private var departure:TMapPoint? = null
        private var destination:TMapPoint? = null

        init {
            departure = tmapData.findTitlePOI(_departure)[0].poiPoint
            destination =  tmapData.findTitlePOI(_destination)[0].poiPoint
        }

        fun drawRoute() {
            Log.d("log", "drawRoute")
            tmapData.findPathData(departure,destination) {
                // 길찾기 선의 속성값(현재는 색, 굵기) 설정 코드
                it.setLineWidth(3f)
                it.setLineColor(Color.BLUE)
                it.setLineAlpha(255)

                it.setOutLineWidth(5f)
                it.setOutLineColor(Color.RED)
                it.setOutLineAlpha(255)
                tMapView.addTMapPolyLine(it)
            }
        }

        // 대중교통을 포함한 경로 json을 얻기 위한 함수
        fun getTransMitJSON(){
            Log.d("log", "getTransMitJSON")
            val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
            val url = "https://apis.openapi.sk.com/transit/routes"
            val client = OkHttpClient()

            val json = JSONObject()
            json.put("startX", departure?.longitude)
            json.put("startY", departure?.latitude)
            json.put("endY", destination?.longitude)
            json.put("endX", destination?.latitude)
            json.put("lang", 0)
            json.put("format", "json")
            json.put("count", 10)


            val body = json.toString().toRequestBody(JSON)

            val request = Request.Builder()
                .url(url)
                .post(body)
                .addHeader("accept", "application/json")
                .addHeader("content-type", "application/json")
                .addHeader("appKey", "e8wHh2tya84M88aReEpXCa5XTQf3xgo01aZG39k5")
                .build()


            // 네트워크 통신의 시간이 있기 때문에 스레드를 이용하여 정보를 얻는다.
            var json_string:String? = null
            val response = client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    TODO("Not yet implemented")
                }
                // main thread말고 별도의 thread에서 실행해야 함.
                override fun onResponse(call: Call, response: Response) {
                    Thread{
                        var str = response.body?.string()
                        Log.d("print", str.toString())
                        json_string = str?.trimIndent();
                    }.start()
                }
            })
        }

        override fun run() {
            Log.d("log", "drawRouteThread")
            // 네트워크 받는 동안 대기
            while (departure == null)
                sleep(100)
            while (destination == null)
                sleep(100)

            drawRoute()
            //getTransMitJSON()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun runner() {
        Log.d("log", "run")
        // 앱 키 설정: 앱키 설정 후 딜레이 부여

        GlobalScope.launch {
            setAppKey()
            delay(800)

            val drawRouteThread = DrawRouteThread(tMapView, tmapData, "경복궁","혜화")
            drawRouteThread.start()
        }
    }
}