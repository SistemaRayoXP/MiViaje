package com.example.miviaje;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RouteExtractor {
    private Context context;
    private DatabaseHelper dbHelper;

    public RouteExtractor(Context context, String dbName) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context, dbName);
    }

    public List<LatLng> getRouteSegment(LatLng origin, LatLng destination, String routeName) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT ruta FROM rutas WHERE nombre_ruta = ?";
        Cursor cursor = db.rawQuery(query, new String[]{routeName});

        List<LatLng> routeSegment = new ArrayList<>();
        if (cursor.moveToFirst()) {
            String rutaJson = cursor.getString(cursor.getColumnIndexOrThrow("ruta"));
            try {
                JSONArray routeArray = new JSONArray(rutaJson);
                LatLng minPuntoOrigin = new LatLng(0,0);
                LatLng minPuntoDestiny = new LatLng(0,0);
                double minOrigin = (double) Long.MAX_VALUE;
                double minDestiny = (double) Long.MAX_VALUE;
                int contador = 0;
                for (int i = 0; i < routeArray.length(); i++) {
                    JSONObject point = routeArray.getJSONObject(i);
                    double lat = point.getDouble("lat");
                    double lon = point.getDouble("lon");
                    if(calculateDistance(origin.latitude,origin.longitude,lat,lon)< minOrigin){
                        minPuntoOrigin = new LatLng(lat,lon);
                    }
                    if(calculateDistance(destination.latitude,destination.longitude,lat,lon)< minDestiny){
                        minPuntoDestiny= new LatLng(lat,lon);
                    }
                    contador = i;
                }
                Toast.makeText(context, ""+contador, Toast.LENGTH_SHORT).show();
                Toast.makeText(context, minPuntoOrigin.toString() + "," + minPuntoDestiny.toString(), Toast.LENGTH_SHORT).show();
                boolean withinSegment = false;
                for (int i = 0; i < routeArray.length(); i++) {
                    JSONObject point = routeArray.getJSONObject(i);
                    double lat = point.getDouble("lat");
                    double lon = point.getDouble("lon");
                    LatLng latLng = new LatLng(lat, lon);
                    if(esPuntoIgual(latLng,minPuntoOrigin)){
                        withinSegment = true;
                    }
                    if (withinSegment) {
                        routeSegment.add(latLng);
                    }
                    if(esPuntoIgual(latLng,minPuntoDestiny)){
                        withinSegment = false;
                    }
                }

                if (!routeSegment.isEmpty()) {
                    Toast.makeText(context, "Se encontró un segmento de ruta", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "No se encontraron puntos cercanos en la ruta", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "Error al procesar la ruta", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, "Ruta no encontrada", Toast.LENGTH_LONG).show();
        }
        cursor.close();
        return routeSegment;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] result = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, result);
        return result[0];
    }

    private boolean esPuntoIgual(LatLng p1, LatLng p2){
        return ((p1.latitude== p2.latitude)&&(p1.longitude==p2.longitude));

    }
}