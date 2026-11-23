package com.example.weatherapp_pa.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationProvider(context: Context) {

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {

        val cancellationTokenSource =
            CancellationTokenSource()

        return suspendCancellableCoroutine { continuation ->

            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            )
                .addOnSuccessListener { location ->

                    if (continuation.isActive) {
                        continuation.resume(location)
                    }
                }
                .addOnFailureListener { exception ->

                    if (continuation.isActive) {
                        continuation.resumeWithException(exception)
                    }
                }

            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        }
    }
}
