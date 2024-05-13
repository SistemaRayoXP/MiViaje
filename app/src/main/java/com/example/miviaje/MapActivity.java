package com.example.miviaje;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private PlacesClient placesClient;
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> adapter;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currentLocation;
    private  PathCalculator pathCalculator;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Places.initialize(getApplicationContext(),getString(R.string.AKPI));
        placesClient = Places.createClient(this);
        pathCalculator = new PathCalculator(placesClient);

        autoCompleteTextView = findViewById(R.id.destino);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextView.setAdapter(adapter);

        // Configurar TextWatcher para el EditText
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Obtener sugerencias de direcciones mientras el usuario escribe
                if (s.length() > 0) {
                    obtenerSugerenciasDirecciones(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Configurar Listener para seleccionar una opción del autocompletado
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            // Aquí puedes ejecutar la función que deseas cuando se selecciona una opción del autocompletado
            String selectedLocation = (String) parent.getItemAtPosition(position);
            // Llamar a la función que desees con la ubicación seleccionada
            obtenerCoordenadas(selectedLocation);

        });

        // Configurar Listener para la acción de "Enter"
        autoCompleteTextView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE || (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                // Aquí puedes ejecutar la función que deseas cuando se presiona "Enter"
                String locationEntered = autoCompleteTextView.getText().toString();
                // Llamar a la función que desees con la ubicación ingresada
                obtenerCoordenadas(locationEntered);
                return true;
            }
            return false;
        });
    }

    private void obtenerSugerenciasDirecciones(String query) {
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(token)
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            List<String> suggestions = new ArrayList<>();
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                suggestions.add(prediction.getFullText(null).toString());
            }
            adapter.clear();
            adapter.addAll(suggestions);
            adapter.notifyDataSetChanged();
        }).addOnFailureListener((exception) -> {
        });
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Verifica y solicita permiso de ubicación si no está concedido
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Habilita la capa de ubicación
            mMap.setMyLocationEnabled(true);

            // Obtiene la ubicación actual del usuario y lo centra en el mapa
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
                }
            });
        } else {
            // Solicita permiso de ubicación al usuario
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso de ubicación concedido, intenta cargar el mapa nuevamente
                onMapReady(mMap);
            } else {
                // Permiso de ubicación denegado, muestra un mensaje al usuario
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void obtenerCoordenadas(String direccion) {
        pathCalculator.obtenerCoordenadas(direccion).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                LatLng coordenadas = task.getResult();
                if (coordenadas != null) {
                    // Crear una lista de coordenadas que incluya el punto de origen y el punto de destino
                    List<LatLng> listaCoordenadas = new ArrayList<>();
                    listaCoordenadas.add(coordenadas); // Punto de destino
                    listaCoordenadas.add(currentLocation); // Punto de origen

                    // Dibujar la ruta en el mapa
                    drawPathOnMap(listaCoordenadas);
                } else {
                    Log.e("Coordenadas", "No se pudieron obtener las coordenadas");
                }
            } else {
                Exception exception = task.getException();
                Log.e("Coordenadas", "Error al obtener las coordenadas", exception);
            }
        });
    }
    private void drawPathOnMap(List<LatLng> mCoordinates){
        List<LatLng> listaCoordenadas = new ArrayList<>();
        listaCoordenadas.add(currentLocation); // Punto de origen
        listaCoordenadas.addAll(mCoordinates); // Agregar puntos intermedios si los hay
        LatLng destino = mCoordinates.get(mCoordinates.size() - 1); // Obtener el punto de destino

        DrawPath drawPath = new DrawPath(mMap, listaCoordenadas, destino);
        drawPath.drawPath();

        // Ajustar la cámara para mostrar toda la ruta trazada
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point : listaCoordenadas) {
            builder.include(point);
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 100));
    }
}
