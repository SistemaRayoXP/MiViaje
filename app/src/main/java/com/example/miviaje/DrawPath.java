package com.example.miviaje;
import android.graphics.Color;
import android.os.AsyncTask;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DrawPath {

    private GoogleMap mMap;
    private List<LatLng> mCoordinates;
    private LatLng mDestination;

    public DrawPath(GoogleMap map, List<LatLng> coordinates, LatLng destination) {
        mMap = map;
        mCoordinates = coordinates;
        mDestination = destination;
    }

    public void drawPath() {
        if (mCoordinates.size() < 2) {
            // No se puede trazar la ruta con menos de 2 puntos
            return;
        }

        // Construir la URL para la solicitud de la API de direcciones de Google
        String url = getDirectionsUrl(mCoordinates, mDestination);

        // Lanzar la solicitud a la API de direcciones de Google en un hilo separado
        new FetchDirectionsTask().execute(url);
    }


    private String getDirectionsUrl(List<LatLng> coordinates, LatLng destination) {
        String apiKey = "TuAPI";

        // Obtener el origen y el destino
        LatLng origin = coordinates.get(0);
        String strOrigin = "origin=" + origin.latitude + "," + origin.longitude; // Punto de origen
        String strDest = "destination=" + destination.latitude + "," + destination.longitude; // Punto de destino

        // Construir la URL para la solicitud de la API de direcciones de Google
        String parameters = strOrigin + "&" + strDest + "&key=" + apiKey;
        String url = "https://maps.googleapis.com/maps/api/directions/json?" + parameters;
        return url;
    }

    private class FetchDirectionsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String data = "";
            try {
                data = downloadUrl(urls[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            data = stringBuilder.toString();
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class ParserTask extends AsyncTask<String, Integer, List<LatLng>> {

        @Override
        protected List<LatLng> doInBackground(String... jsonData) {
            JSONObject jsonObject;
            List<LatLng> points = new ArrayList<>();
            try {
                jsonObject = new JSONObject(jsonData[0]);
                JSONArray routes = jsonObject.getJSONArray("routes");
                for (int i = 0; i < routes.length(); i++) {
                    JSONArray legs = ((JSONObject) routes.get(i)).getJSONArray("legs");
                    for (int j = 0; j < legs.length(); j++) {
                        JSONArray steps = ((JSONObject) legs.get(j)).getJSONArray("steps");
                        for (int k = 0; k < steps.length(); k++) {
                            String polyline = "";
                            polyline = (String) ((JSONObject) ((JSONObject) steps.get(k)).get("polyline")).get("points");
                            points.addAll(decodePoly(polyline));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return points;
        }

        @Override
        protected void onPostExecute(List<LatLng> result) {
            super.onPostExecute(result);
            PolylineOptions lineOptions = new PolylineOptions();
            lineOptions.addAll(result);
            lineOptions.width(12);
            lineOptions.color(Color.BLUE);
            mMap.addPolyline(lineOptions);
        }

        private List<LatLng> decodePoly(String encoded) {
            List<LatLng> poly = new ArrayList<>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;
            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;
                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;
                LatLng p = new LatLng((((double) lat / 1E5)), (((double) lng / 1E5)));
                poly.add(p);
            }
            return poly;
        }
    }
}
