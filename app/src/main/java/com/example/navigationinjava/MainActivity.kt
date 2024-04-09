package com.example.navigationinjava

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.skt.tmap.TMapData
import com.skt.tmap.TMapPoint
import com.skt.tmap.TMapView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
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

    // 위치 값을 받기 위한 임시 코드(TMapPoint를 사용한다.)
    private lateinit var departure:TMapPoint
    private lateinit var destination:TMapPoint

    // 대중교통 api값을 Json으로 받기위한 OkHttp 클라이언트
    val client = OkHttpClient()

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
        run()
    }

    // 앱키를 받기위해선 정보를 받기위한 약간의 네트워크 시간이 존재하는데 텀이 필요하다.
    fun setAppKey(){
        Log.d("log", "setAppKey")
        tMapView.setSKTMapApiKey("GaysikTxiU3X0maxNBKNu6pi4yfFqgfy7x0HNqUv")
        // delayTime은 코루틴을 이용하여 적용시킨다. (Q. 코루틴이 무엇인지 학습하기)
    }

    // 줌 크기를 설정한다.
    fun setMap() {
        Log.d("log", "setMap")
        // 처음 줌 크기
        tMapView.zoomLevel = 15;
    }

    fun setDepartureAndDestination(departure:String, destination:String){
        Log.d("log", "setDepartureAndDestination")
        this.departure = getPosition(departure)
        this.destination = getPosition(destination)
    }

    // 한성대입구역 위치 값(PoiPoint)을 구하는 함수이다.
    fun getPosition(wantToFindPointName:String):TMapPoint {
        Log.d("log", "getPosition")
        val returnValue = tmapData.findTitlePOI(wantToFindPointName)[0].poiPoint
        Log.d("print",returnValue.toString()+returnValue)

        return returnValue
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun run() {
        Log.d("log", "run")
        // 앱 키 설정: 앱키 설정 후 딜레이 부여

        GlobalScope.launch {
            setAppKey()
            delay(2000)

            // 마커를 찍는 함수이다: allFindPOI 함수를 테스트 하기 위한 함수이다.
            //drawMarker("경복궁")
            //delay(500)

            // 이후 그림을 그릴 출발지와 도착지의 위치값(TMapPoint)을 구한다.
            setDepartureAndDestination("경복궁", "한성대입구역")
            delay(500)

            // 현재는 TmapView의 줌 크기만 작성되어 있다.
            //setMap()
            delay(1000)

            // departure에서 destination까지 길을 화면에 그려준다.
            drawRoute()
            delay(1000)

            // departure에서 destination까지 대중교통을 이용한 길을 JSON으로 전달해준다.
            //getTransMitJSON()

            //getGEO()

            // tMapView.setCenterPoint(departure.latitude, departure.longitude)
        }
    }

    // 한성대 입구역을 키워드로 등장하는 여러개 장소를 마커로 표시하는 함수이다.
    fun drawMarker(wantToGetPingName:String) {
        Log.d("log", "drawMarker")
        // wantToGetPingName과 연관된 POI 리스트(item)가 it으로 나온다.
        tmapData.findTitlePOI("경복궁") {
            // 연관성이 가장 높은 값부터 it으로 나온다. item[0]은 wantToGetPingName
            for (item in it) {
                // it의 데이터 정보를 "poi tag"로 log cat에 출력
                Log.d(
                    "print",
                    "POI Name: " + item.getPOIName().toString() + ", " +
                            "Address: " + item.getPOIAddress().replace("null", "") + ", " +
                            "Point: " + item.getPOIPoint().toString()
                )
            }
            // 각 장소에 POI를 마킹
            //tMapView.addTMapPOIItem(it)
        }
    }

    // 보행자 도보를 구하기 위한 코드(해당 함수는 setDepartureAndDestination을 미리 실행해야 한다.)
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

    // 지오코딩의 결과 값을 얻기위한 함수
    fun getGEO() {
        Log.d("log", "getGEO")
        val requestGeo = Request.Builder()
            .url("https://apis.openapi.sk.com/tmap/geo/geocoding?version=1&city_do=%EC%84%9C%EC%9A%B8&gu_gun=%EC%A4%91%EA%B5%AC&dong=%EC%9D%84%EC%A7%80%EB%A1%9C&addressFlag=F02&coordType=WGS84GEO")
            .get()
            .addHeader("accept", "application/json")
            .addHeader("content-type", "application/json")
            .addHeader("appKey", "e8wHh2tya84M88aReEpXCa5XTQf3xgo01aZG39k5")
            .build()

        val responseGeo = client.newCall(requestGeo).execute().cacheResponse
        Log.d("print", responseGeo.toString())
    }

    // 대중교통을 포함한 경로 json을 얻기 위한 함수
    fun getTransMitJSON(){
        Log.d("log", "getTransMitJSON")
        val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
        val url = "https://apis.openapi.sk.com/transit/routes"
        val client = OkHttpClient()

        val json = JSONObject()

        json.put("startX", departure.longitude)
        json.put("startY", departure.latitude)
        json.put("endY", destination.longitude)
        json.put("endX", destination.latitude)
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
}