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

    fun makeDelay() {
        // 앱 키 설정
        tMapView.setSKTMapApiKey("GaysikTxiU3X0maxNBKNu6pi4yfFqgfy7x0HNqUv")

        CoroutineScope(Dispatchers.Main).launch {
            waitForAppKeyAuthentication()
            // 인증이 완료되면 경로를 그리는 작업 실행
            var tmapData = TMapData()

            tmapData.findAllPOI("한성대", 2){
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

        // 뷰 설정
        tmapViewRelativeLayout.addView(tMapView)

        makeDelay()

        // 처음 줌 크기
        //tMapView.zoomLevel = 15;
        // 지도의 초기 위치를 지정하는 함수
        //tMapView.setCenterPoint(37.570028, 126.986072)

    }
}