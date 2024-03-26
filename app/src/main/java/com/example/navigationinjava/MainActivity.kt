package com.example.navigationinjava

import android.os.Bundle
import android.util.Log
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.skt.tmap.TMapData
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

    fun setAppKey() {
        // 앱 키 설정
        tMapView.setSKTMapApiKey("GaysikTxiU3X0maxNBKNu6pi4yfFqgfy7x0HNqUv")

        // 코루틴을 이용한 딜레이 적용
        CoroutineScope(Dispatchers.Main).launch {
            waitForAppKeyAuthentication()
            setMap()
            drawMarker()
        }
    }

    fun setMap() {
        // 처음 줌 크기
        tMapView.zoomLevel = 15;
        // 지도의 초기 위치를 지정하는 함수
        tMapView.setCenterPoint(37.570028, 126.986072)
    }

    fun drawMarker() {
        tmapData.findAllPOI("경복궁", 5){
                poiItem ->
            for(i in 0..<poiItem.size) {
                var item = poiItem.get(i)
                Log.d("poi tag",
                    "POI Name: " + item.getPOIName().toString() + ", " +
                            "Address: " + item.getPOIAddress().replace("null", "") + ", " +
                            "Point: " + item.getPOIPoint().toString()
                );
            }
            tMapView.addTMapPOIItem(poiItem);
        }
    }

    suspend fun waitForAppKeyAuthentication() {
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