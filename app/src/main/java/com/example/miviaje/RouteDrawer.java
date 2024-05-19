package com.example.miviaje;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RouteDrawer {

    private GoogleMap map;
    private Context context;
    private Polyline currentPolyline;
    private List<Marker> markers = new ArrayList<>();

    public RouteDrawer(GoogleMap map, Context context) {
        this.map = map;
        this.context = context;
    }

    public void drawRoute(List<LatLng> coordinates) {
        if (coordinates.size() < 2) {
            throw new IllegalArgumentException("At least two coordinates are required");
        }

        // Clear the previous route and markers
        if (currentPolyline != null) {
            currentPolyline.remove();
        }
        for (Marker marker : markers) {
            marker.remove();
        }
        markers.clear();

        String origin = coordinates.get(0).latitude + "," + coordinates.get(0).longitude;
        String destination = coordinates.get(coordinates.size() - 1).latitude + "," + coordinates.get(coordinates.size() - 1).longitude;

        StringBuilder waypoints = new StringBuilder();
        for (int i = 1; i < coordinates.size() - 1; i++) {
            waypoints.append(coordinates.get(i).latitude).append(",").append(coordinates.get(i).longitude).append("|");
        }

        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin +
                "&destination=" + destination +
                "&waypoints=" + waypoints.toString() +
                "&key=" + context.getString(R.string.AKPI);

        new FetchRouteTask().execute(url);

        // Add markers for each coordinate
        for (LatLng coordinate : coordinates) {
            Marker marker = map.addMarker(new MarkerOptions().position(coordinate));
            markers.add(marker);
        }
    }

    private class FetchRouteTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                reader.close();

                return stringBuilder.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray routes = jsonObject.getJSONArray("routes");
                JSONObject route = routes.getJSONObject(0);
                JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                String points = overviewPolyline.getString("points");
                List<LatLng> latLngList = PolyUtil.decode(points);

                PolylineOptions polylineOptions = new PolylineOptions().addAll(latLngList).color(0xFF0000FF).width(10);
                currentPolyline = map.addPolyline(polylineOptions);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
