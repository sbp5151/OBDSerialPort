package com.jld.obdserialport.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.jld.obdserialport.R;
import com.jld.obdserialport.runnable.LocationReceiveRun;

import java.io.File;

public class MapNaviUtils {

    public static final String PN_MAPAUTO_MAP = "com.autonavi.amapautolite"; // 高德地图车机版包名
    public static final String PN_GAODE_MAP = "com.autonavi.minimap"; // 高德地图包名
    public static final String PN_BAIDU_MAP = "com.baidu.BaiduMap"; // 百度地图包名
    public static final String DOWNLOAD_GAODE_MAP = "http://www.autonavi.com/"; // 高德地图下载地址
    public static final String DOWNLOAD_BAIDU_MAP = "http://map.baidu.com/zt/client/index/"; // 百度地图下载地址

    /**
     * 检查应用是否安装
     *
     * @return
     */
    public static boolean isGdMapInstalled() {
        return isInstallPackage(PN_GAODE_MAP);
    }

    public static boolean isGdAutoMapInstalled() {
        return isInstallPackage(PN_MAPAUTO_MAP);
    }

    public static boolean isBaiduMapInstalled() {
        return isInstallPackage(PN_BAIDU_MAP);
    }

    private static boolean isInstallPackage(String packageName) {
        return new File("/data/data/" + packageName).exists();
    }

    /**
     * 百度坐标系 (BD-09) 与 火星坐标系 (GCJ-02)的转换
     * 即 百度 转 谷歌、高德
     *
     * @param latLng
     * @returns
     */
//    public static LatLng BD09ToGCJ02(LatLng latLng) {
//        double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
//        double x = latLng.longitude - 0.0065;
//        double y = latLng.latitude - 0.006;
//        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * x_pi);
//        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * x_pi);
//        double gg_lat = z * Math.sin(theta);
//        double gg_lng = z * Math.cos(theta);
//        return new LatLng(gg_lat, gg_lng);
//    }

    /**
     * 火星坐标系 (GCJ-02) 与百度坐标系 (BD-09) 的转换
     * 即谷歌、高德 转 百度
     *
     * @returns
     */
//    public static LatLng GCJ02ToBD09(LatLng latLng) {
//        double x_pi = 3.14159265358979324 * 3000.0 / 180.0;
//        double z = Math.sqrt(latLng.longitude * latLng.longitude + latLng.latitude * latLng.latitude) + 0.00002 * Math.sin(latLng.latitude * x_pi);
//        double theta = Math.atan2(latLng.latitude, latLng.longitude) + 0.000003 * Math.cos(latLng.longitude * x_pi);
//        double bd_lat = z * Math.sin(theta) + 0.006;
//        double bd_lng = z * Math.cos(theta) + 0.0065;
//        return new LatLng(bd_lat, bd_lng);
//    }
    public static void openAutoMap(Context context, double dlat, double dlon, String dname) {
        if (LocationReceiveRun.mGpsBean != null) {
            Intent intent = new Intent();
//            intent.setAction("AUTONAVI_STANDARD_BROADCAST_RECV");
//            intent.putExtra("KEY_TYPE", 10007);
//            intent.putExtra("EXTRA_SNAME", LocationReceiveRun.mGpsBean.getAddress());
//            intent.putExtra("EXTRA_SLON", LocationReceiveRun.mGpsBean.getLongitude());
//            intent.putExtra("EXTRA_SLAT", LocationReceiveRun.mGpsBean.getLatitude());
//            intent.putExtra("EXTRA_DNAME", dname);
//            intent.putExtra("EXTRA_DLON", dlon);
//            intent.putExtra("EXTRA_DLAT", dlat);
//            intent.putExtra("EXTRA_DEV", 0);
//            intent.putExtra("EXTRA_M", 0);
            intent.setAction("AUTONAVI_STANDARD_BROADCAST_RECV");
            intent.putExtra("KEY_TYPE", 10038);
            intent.putExtra("POINAME",dname);
            intent.putExtra("LAT",dlat);
            intent.putExtra("LON",dlon);
            intent.putExtra("DEV",0);
            intent.putExtra("STYLE",0);
            intent.putExtra("SOURCE_APP","OBDSerialPort");
            context.sendBroadcast(intent);
        } else
            Toast.makeText(context, context.getString(R.string.request_location_fail), Toast.LENGTH_SHORT).show();

    }

    /**
     * 打开高德地图导航功能
     *
     * @param context
     * @param slat    起点纬度
     * @param slon    起点经度
     * @param sname   起点名称 可不填（0,0，null）
     * @param dlat    终点纬度
     * @param dlon    终点经度
     * @param dname   终点名称 必填
     */
    public static void openGaoDeNavi(Context context, double slat, double slon, String sname, double dlat, double dlon, String dname) {
        String uriString = null;
        StringBuilder builder = new StringBuilder("amapuri://route/plan?sourceApplication=maxuslife");
        if (0 == slat) {
//            //如果不传起点（注释下面这段），默认就是现在我的位置（手机当前定位）
//            AMapLocation location = LocationService.getInstance().getAMapLocation();
//            if (LocationService.isSuccess(location)) {
//                builder.append("&sname=我的位置")
//                        .append("&slat=").append(location.getLatitude())
//                        .append("&slon=").append(location.getLongitude());
//            }
        } else {
            builder.append("&sname=").append(sname)
                    .append("&slat=").append(slat)
                    .append("&slon=").append(slon);
        }
        builder.append("&dlat=").append(dlat)
                .append("&dlon=").append(dlon)
                .append("&dname=").append(dname)
                .append("&dev=0")
                .append("&t=0");
        uriString = builder.toString();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setPackage(PN_GAODE_MAP);
        intent.setData(Uri.parse(uriString));
        context.startActivity(intent);
    }

    /**
     * 打开百度地图导航功能(默认坐标点是高德地图，需要转换)
     * @param context
     * @param slat 起点纬度
     * @param slon 起点经度
     * @param sname 起点名称 可不填（0,0，null）
     * @param dlat 终点纬度
     * @param dlon 终点经度
     * @param dname 终点名称 必填
     */
//    public static void openBaiDuNavi(Context context,double slat, double slon, String sname, double dlat, double dlon, String dname){
//        String uriString = null;
//        //终点坐标转换
//        LatLng destination = new LatLng(dlat,dlon);
//        LatLng destinationLatLng = GCJ02ToBD09(destination);
//        dlat = destinationLatLng.latitude;
//        dlon = destinationLatLng.longitude;
//        StringBuilder builder = new StringBuilder("baidumap://map/direction?mode=driving&");
//        if (slat != 0){
//            //起点坐标转换
//            LatLng origin = new LatLng(slat,slon);
//            LatLng originLatLng = GCJ02ToBD09(origin);
//            slat = originLatLng.latitude;
//            slon = originLatLng.longitude;
//            builder.append("origin=latlng:")
//                    .append(slat)
//                    .append(",")
//                    .append(slon)
//                    .append("|name:")
//                    .append(sname);
//        }
//        builder.append("&destination=latlng:")
//                .append(dlat)
//                .append(",")
//                .append(dlon)
//                .append("|name:")
//                .append(dname);
//        uriString = builder.toString();
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setPackage(PN_BAIDU_MAP);
//        intent.setData(Uri.parse(uriString));
//        context.startActivity(intent);
//    }

}
