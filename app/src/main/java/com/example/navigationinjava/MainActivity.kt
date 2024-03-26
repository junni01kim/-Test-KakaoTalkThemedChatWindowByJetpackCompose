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


class MainActivity : AppCompatActivity() {
    // 지도의 레이아웃 정보를 갖는 변수
    private lateinit var tmapViewRelativeLayout:RelativeLayout
    // 지도의 객체
    private lateinit var tMapView:TMapView
    private lateinit var tmapData:TMapData

    private lateinit var hansungUniversity:TMapPoint
    private lateinit var hansungUniversityStation:TMapPoint

    fun setAppKey() {
        // 앱 키 설정
        tMapView.setSKTMapApiKey("GaysikTxiU3X0maxNBKNu6pi4yfFqgfy7x0HNqUv")

        // 코루틴을 이용한 딜레이 적용
        CoroutineScope(Dispatchers.Main).launch {
            delayTime()
            drawMarker()
            setMap()
            delayTime()
            drawRoute()
        }
    }

    fun setMap() {
        // 처음 줌 크기
        tMapView.zoomLevel = 15;
        // 지도의 초기 위치를 지정하는 함수 (지금 이러면 의미없는 반복문이 발생한다.)
        tmapData.findAllPOI("한성대학교",0){
                poiItem ->
            val poi = poiItem.get(0).poiPoint
            hansungUniversity = poi
            //tMapView.setCenterPoint(poi.latitude, poi.longitude)
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
            tMapView.setCenterPoint(info.point.latitude, info.point.longitude)
        }
    }

    fun drawMarker() {
        // 1개의 값만 찾고 싶은 경우(가장 정확한 값만 찾는 기능은 모르겠음)
        tmapData.findAllPOI("한성대입구역", 1){
                poiItem ->
            for(i in 0..<poiItem.size) {
                var item = poiItem.get(i)
                Log.d("poi tag",
                    "POI Name: " + item.poiName.toString() + ", " +
                            "Address: " + item.poiAddress.replace("null", "") + ", " +
                            "Point: " + item.poiPoint.toString()
                );

                tMapView.addTMapPOIItem(poiItem)
                hansungUniversityStation = poiItem[0].poiPoint
            }
            tMapView.addTMapPOIItem(poiItem);
        }
        /*// 한성대 입구역을 키워드로 여러개 장소를 찾는 키워드
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
        }*/
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