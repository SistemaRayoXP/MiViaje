package com.example.miviaje;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.gms.maps.model.LatLng;

import java.util.Collections;

public class PathCalculator {

    private PlacesClient placesClient;

    public PathCalculator(PlacesClient placesClient) {
        this.placesClient = placesClient;
    }

    public Task<LatLng> obtenerCoordenadas(String direccion) {
        // Crear una solicitud de predicciones de autocompletado de direcciones
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(token)
                .setQuery(direccion)
                .build();

        // Realizar la solicitud de forma asíncrona
        return placesClient.findAutocompletePredictions(request).continueWithTask(task -> {
            if (task.isSuccessful()) {
                FindAutocompletePredictionsResponse response = task.getResult();
                if (response != null && response.getAutocompletePredictions() != null && !response.getAutocompletePredictions().isEmpty()) {
                    // Obtener la primera predicción de la lista
                    AutocompletePrediction prediction = response.getAutocompletePredictions().get(0);
                    String placeId = prediction.getPlaceId();

                    // Crear la solicitud de obtener lugar (place)
                    FetchPlaceRequest placeRequest = FetchPlaceRequest.newInstance(placeId, Collections.singletonList(Place.Field.LAT_LNG));

                    // Realizar la solicitud para obtener el lugar (place) de forma asíncrona
                    return placesClient.fetchPlace(placeRequest).continueWith(fetchPlaceTask -> {
                        if (fetchPlaceTask.isSuccessful()) {
                            FetchPlaceResponse placeResponse = fetchPlaceTask.getResult();
                            Place place = placeResponse.getPlace();
                            return place.getLatLng();
                        } else {
                            throw fetchPlaceTask.getException();
                        }
                    });
                } else {
                    throw new Exception("No se encontraron predicciones de autocompletado");
                }
            } else {
                throw task.getException();
            }
        });
    }
}
