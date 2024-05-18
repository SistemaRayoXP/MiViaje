package com.example.miviaje;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteFinder {
    private Context context;
    private DatabaseHelper dbHelper;

    public RouteFinder(Context context, String dbName) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context, dbName);
    }

    public String findOptimalRoute(LatLng origin, LatLng destination) {
        List<RoutePoint> originPoints = getNearbyPoints(origin);
        List<RoutePoint> destinationPoints = getNearbyPoints(destination);

        Map<String, List<RoutePoint>> originRoutes = new HashMap<>();
        Map<String, List<RoutePoint>> destinationRoutes = new HashMap<>();

        // Mapear puntos cercanos al origen
        for (RoutePoint point : originPoints) {
            if (!originRoutes.containsKey(point.routeName)) {
                originRoutes.put(point.routeName, new ArrayList<>());
            }
            originRoutes.get(point.routeName).add(point);
        }

        // Mapear puntos cercanos al destino
        for (RoutePoint point : destinationPoints) {
            if (!destinationRoutes.containsKey(point.routeName)) {
                destinationRoutes.put(point.routeName, new ArrayList<>());
            }
            destinationRoutes.get(point.routeName).add(point);
        }

        // Encontrar rutas comunes
        List<String> commonRoutes = new ArrayList<>();
        for (String routeName : originRoutes.keySet()) {
            if (destinationRoutes.containsKey(routeName)) {
                commonRoutes.add(routeName);
            }
        }

        // Evaluar rutas comunes para encontrar la óptima
        String optimalRoute = null;
        double minDistance = Double.MAX_VALUE;

        for (String routeName : commonRoutes) {
            List<RoutePoint> originRoutePoints = originRoutes.get(routeName);
            List<RoutePoint> destinationRoutePoints = destinationRoutes.get(routeName);

            // Calcular la menor distancia combinada
            for (RoutePoint originPoint : originRoutePoints) {
                for (RoutePoint destinationPoint : destinationRoutePoints) {
                    double totalDistance = originPoint.distanceToPoint + destinationPoint.distanceToPoint;
                    if (totalDistance < minDistance) {
                        minDistance = totalDistance;
                        optimalRoute = routeName;
                    }
                }
            }
        }

        if (optimalRoute == null) {
            Toast.makeText(context, "No se encontró ruta óptima", Toast.LENGTH_LONG).show();
        }
        return optimalRoute;
    }

    private List<RoutePoint> getNearbyPoints(LatLng coordinate) {
        List<RoutePoint> points = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT * FROM puntos_ruta";
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String routeName = cursor.getString(cursor.getColumnIndexOrThrow("nombre_ruta"));
                double lat = cursor.getDouble(cursor.getColumnIndexOrThrow("lat"));
                double lon = cursor.getDouble(cursor.getColumnIndexOrThrow("lon"));

                double distance = calculateDistance(coordinate.latitude, coordinate.longitude, lat, lon);
                if (distance <= 3000) { // 3 km
                    points.add(new RoutePoint(id, routeName, lat, lon, distance));
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return points;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] result = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, result);
        return result[0];
    }

    private static class RoutePoint {
        int id;
        String routeName;
        double latitude;
        double longitude;
        double distanceToPoint;

        public RoutePoint(int id, String routeName, double latitude, double longitude, double distanceToPoint) {
            this.id = id;
            this.routeName = routeName;
            this.latitude = latitude;
            this.longitude = longitude;
            this.distanceToPoint = distanceToPoint;
        }
    }
}
