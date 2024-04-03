package com.example.navigationinjava

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.skt.tmap.TMapData
import com.skt.tmap.TMapPoint
import com.skt.tmap.TMapView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    // 지도의 레이아웃 정보를 갖는 변수
    private lateinit var tmapViewRelativeLayout:RelativeLayout
    // 지도의 객체
    private lateinit var tMapView:TMapView
    private lateinit var tmapData:TMapData

    private lateinit var hansungUniversity:TMapPoint
    private lateinit var hansungUniversityStation:TMapPoint

    val client = OkHttpClient()

    fun setAppKey() {
        // 앱 키 설정
        tMapView.setSKTMapApiKey("GaysikTxiU3X0maxNBKNu6pi4yfFqgfy7x0HNqUv")

        // 코루틴을 이용한 딜레이 적용
        CoroutineScope(Dispatchers.Main).launch {
            delayTime()
            drawMarker()
            delayTime()
            setMap()
            delayTime()
            drawRoute()
            //logTransmit()
            renewGetTransMitPost()
            Log.d("poi print", "출력완료")
            tMapView.setCenterPoint(hansungUniversity.latitude, hansungUniversity.longitude)
        }
    }


    fun setMap() {
        // 처음 줌 크기
        tMapView.zoomLevel = 15;
        // 지도의 초기 위치를 지정하는 함수 (지금 이러면 의미없는 반복문이 발생한다.)
        tmapData.findAllPOI("한성대학교",0){
                poiItem ->
            val poi = poiItem[0].poiPoint
            hansungUniversity = poi
        }
    }

    fun drawRoute() {
        tmapData.findPathData(hansungUniversity,hansungUniversityStation) {
            it.setLineWidth(3f)
            it.setLineColor(Color.BLUE)
            it.setLineAlpha(255)

            it.setOutLineWidth(5f)
            it.setOutLineColor(Color.RED)
            it.setOutLineAlpha(255)

            tMapView.addTMapPolyLine(it)
            val info = tMapView.getDisplayTMapInfo(it.getLinePointList())
            //tMapView.setCenterPoint(info.point.latitude, info.point.longitude)
        }
    }

    fun renewGetTransMitPost(){
        val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
        val url = "https://apis.openapi.sk.com/transit/routes"
        val client = OkHttpClient()

        val json = JSONObject()
        json.put("startX", "126.926493082645")
        json.put("startY", "37.6134436427887")
        json.put("endY", "37.6134436427887")
        json.put("endX", "127.126936754911")
        json.put("endY", "37.5004198786564")
        json.put("lang", 0)
        json.put("format", "json")
        json.put("count", 10)


        val body = RequestBody.create(JSON, json.toString())

        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("accept", "application/json")
            .addHeader("content-type", "application/json")
            .addHeader("appKey", "e8wHh2tya84M88aReEpXCa5XTQf3xgo01aZG39k5")
            .build()

        val response = client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }
            // main thread말고 별도의 thread에서 실행해야 함.
            override fun onResponse(call: Call, response: Response) {
                Thread{
                    var str = response.body?.string()
                    Log.d("print", str.toString())
                }.start()
            }
        })
    }


    fun drawMarker() {
        // 1개의 값만 찾고 싶은 경우(가장 정확한 값만 찾는 기능은 모르겠음)
        // 한성대 입구역을 키워드로 여러개 장소를 찾는 키워드
        tmapData.findTitlePOI("한성대입구역") {
            for (item in it) {
                Log.d(
                    "poi tag",
                    "POI Name: " + item.getPOIName().toString() + ", " +
                            "Address: " + item.getPOIAddress().replace("null", "") + ", " +
                            "Point: " + item.getPOIPoint().toString()
                )
            }
            tMapView.addTMapPOIItem(it)
            hansungUniversityStation = it[0].poiPoint
        }
    }

    suspend fun delayTime() {
        delay(1000)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // xml에 RelativeLayout으로 구성되어 있다.
        tmapViewRelativeLayout = findViewById(R.id.tmapViewContainer)
        tMapView = TMapView(this)
        tmapData = TMapData()

        // 뷰 설정
        tmapViewRelativeLayout.addView(tMapView)

        setAppKey()

    }
}