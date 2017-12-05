package com.study.zpq.baidumapstudy;

import android.app.Activity;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.ArcOptions;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private MapView mapView;
    private BaiduMap baiduMap;
    private double latitude=39.55,longitude=116.24;
    private Overlay PolygonOverlay;
    private Overlay markerAOverlay;
    private Overlay markerBOverlay;
    private Overlay textOverlay;
    private Overlay groundOverlay;
    private Overlay CircleOverlay;
    private Overlay dotOverlay;
    private Overlay circleOverlay;
    private Overlay arcOverlay;
    private GeoCoder geoCoder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //此方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        mapView =(MapView)findViewById(R.id.baiDuMv);
        baiduMap = mapView.getMap();

        //普通地图
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        //初始化编码
        InitGeoCoder();


        //卫星地图
//        baiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);

        //显示实时交通图
        baiduMap.setTrafficEnabled(true);

        //显示热力图
//        baiduMap.setBaiduHeatMapEnabled(true);

        //定义MarkerOptions坐标点
        addMarkerOverlay();

        //定义PolygonOptions坐标点
//        addPolygonOverlay();
        
        //文字覆盖物
//        addTextOverlay();

        //地形图图层覆盖物
//        addGroundOverlay();

        //折线覆盖物
//        addPolylineOverlay();

        //圆点覆盖物
        //addDotOverlay();

        //圆形（空心）覆盖物
//        addCircleOverlay();

        //弧线覆盖物
//        addArcOverlay();




        baiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                Toast.makeText(MainActivity.this, mapPoi.getName(),Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        baiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {

                geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(marker.getPosition()));
            }

            @Override
            public void onMarkerDragStart(Marker marker) {

            }
        });

    }

    private void InitGeoCoder() {
        // 创建地理编码检索实例
        geoCoder = GeoCoder.newInstance();
        //
        OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
            // 反地理编码查询结果回调函数
            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
                if (result == null
                        || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    // 没有检测到结果
                    Toast.makeText(MainActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG).show();
                }
                Toast.makeText(MainActivity.this, "位置：" + result.getAddress(), Toast.LENGTH_SHORT).show();
            }

            // 地理编码查询结果回调函数
            @Override
            public void onGetGeoCodeResult(GeoCodeResult result) {
                if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
                    // 没有检测到结果
                }
            }
        };
        // 设置地理编码检索监听者
        geoCoder.setOnGetGeoCodeResultListener(listener);
    }

    /**
     * 显示弹出窗口覆盖物
     */
    private void displayInfoWindow(final LatLng latLng) {
        // 创建infowindow展示的view
        Button btn = new Button(getApplicationContext());
        btn.setBackgroundResource(R.drawable.popup);
        btn.setText("位置："+latLng.latitude + "  " + latLng.longitude);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
                .fromView(btn);
        // infowindow点击事件
        InfoWindow.OnInfoWindowClickListener infoWindowClickListener = new InfoWindow.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick() {
                geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
                //隐藏InfoWindow
                baiduMap.hideInfoWindow();
            }
        };
        // 创建infowindow
        InfoWindow infoWindow = new InfoWindow(bitmapDescriptor, latLng, -47,
                infoWindowClickListener);

        // 显示InfoWindow
        baiduMap.showInfoWindow(infoWindow);
    }

    private void addArcOverlay() {
        LatLng pt1 = new LatLng(latitude, longitude - 0.01);
        LatLng pt2 = new LatLng(latitude - 0.01, longitude - 0.01);
        LatLng pt3 = new LatLng(latitude, longitude + 0.01);
        ArcOptions arcOptions = new ArcOptions()
            .points(pt1, pt2, pt3)  //设置弧线的起点、中点、终点坐标
            .width(5)               //线宽
            .color(0xFF000000);
        arcOverlay = baiduMap.addOverlay(arcOptions);
    }

    private void addCircleOverlay() {
        CircleOptions circleOptions = new CircleOptions()
            .center(new LatLng(latitude, longitude))    //设置圆心坐标
            .fillColor(0XFFfaa755)              //圆的填充颜色
            .radius(20)                        //设置半径
            .stroke(new Stroke(5, 0xAA00FF00)); //设置边框
        circleOverlay = baiduMap.addOverlay(circleOptions);
    }

    private void addDotOverlay() {
        DotOptions dotOptions = new DotOptions();
        dotOptions.center(new LatLng(latitude, longitude));//设置圆心坐标
        dotOptions.color(0XFFfaa755);//颜色
        dotOptions.radius(25);//设置半径
        dotOverlay = baiduMap.addOverlay(dotOptions);
    }

    private void addPolylineOverlay() {
        CircleOptions circleOptions = new CircleOptions()
            .center(new LatLng(latitude, longitude))    //设置圆心坐标
            .fillColor(0XFFfaa755)  //圆的填充颜色
            .radius(100)            //设置半径
            .stroke(new Stroke(10, 0xAA00FF00));//设置边框
        CircleOverlay = baiduMap.addOverlay(circleOptions);
    }

    private void addGroundOverlay() {
        LatLng southwest = new LatLng(latitude-0.1, longitude-0.1);      //西南
        LatLng northeast = new LatLng(latitude+0.1, longitude+0.1);      //东北
        LatLngBounds latLngBounds = new LatLngBounds.Builder().include(southwest)
                .include(northeast).build();    //得到一个地理范围对象
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.ground_overlay);
        GroundOverlayOptions groundOverlayOptions = new GroundOverlayOptions()
                .image(bitmapDescriptor)            //显示的图片
                .positionFromBounds(latLngBounds)   //显示的位置
                .transparency(0.7f);                //显示的透明度
        groundOverlay = baiduMap.addOverlay(groundOverlayOptions);


    }

    private void addTextOverlay() {
        LatLng latLng = new LatLng(latitude, longitude);
        TextOptions textOptions = new TextOptions()
                .bgColor(0xAAFFFF00)    //设置文字覆盖物背景颜色
                .fontSize(28)           //设置文字大小
                .fontColor(0xFFFF00FF)  //设置文字颜色
                .text("我在这里")        //文字内容
                .rotate(-30)            //设置文字的旋转角度
                .position(latLng);      //设置位置  
        textOverlay = baiduMap.addOverlay(textOptions);
    }

    private void addPolygonOverlay() {
        LatLng latLng1 = new LatLng(latitude+0.2,longitude);
        LatLng latLng2 = new LatLng(latitude,longitude-0.3);
        LatLng latLng3 = new LatLng(latitude-0.2,longitude-0.1);
        LatLng latLng4 = new LatLng(latitude-0.2,longitude+0.1);
        LatLng latLng5 = new LatLng(latitude,longitude+0.3);

        List<LatLng> listLatLngs = new ArrayList<LatLng>();
        listLatLngs.add(latLng1);
        listLatLngs.add(latLng2);
        listLatLngs.add(latLng3);
        listLatLngs.add(latLng4);
        listLatLngs.add(latLng5);

        PolygonOptions polygonOptions = new PolygonOptions()
                .points(listLatLngs)
                .fillColor(0xAAFFFF00)
                .stroke(new Stroke(2, 0xAAFFFF00));
        PolygonOverlay = baiduMap.addOverlay(polygonOptions);
    }

    private void addMarkerOverlay() {
        BitmapDescriptor myMarkerA = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka);
        LatLng pointA = new LatLng(latitude, longitude);
        //构造markerOption, 用于在地图上添加marker
        OverlayOptions optionsA = new MarkerOptions()
                .position(pointA) //设置marker的位置
                .icon(myMarkerA)         //设置marker图标
                .zIndex(9)       //设置marker所在层级
                .draggable(true);   //设置手势拖拽

        //在地图上添加marker,并显示
        markerAOverlay = baiduMap.addOverlay(optionsA);

        BitmapDescriptor myMarkerB = BitmapDescriptorFactory.fromResource(R.drawable.icon_markb);
        LatLng pointB = new LatLng(latitude, longitude);
        //构造markerOption, 用于在地图上添加marker
        OverlayOptions optionsB = new MarkerOptions()
                .position(pointB) //设置marker的位置
                .icon(myMarkerB)         //设置marker图标
                .zIndex(9)       //设置marker所在层级
                .draggable(true);   //设置手势拖拽

        //在地图上添加marker,并显示
        markerBOverlay = baiduMap.addOverlay(optionsB);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        // 在activity执行onDestroy时执行mMapView.onDestroy()
        mapView.onDestroy();
        // 释放地理编码检索实例
        geoCoder.destroy();
        mapView = null;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        // 在activity执行onResume时执行mMapView. onResume ()
        mapView.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        // 在activity执行onPause时执行mMapView. onPause ()
        mapView.onPause();
    }

}