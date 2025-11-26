package com.example.doloresapp.data.remote.websocket

import android.util.Log
import com.example.doloresapp.data.remote.dto.UbicacionDeliveryRequest
import com.google.gson.Gson
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent

class DeliveryWebSocketClient(private val baseUrl: String) {
    
    private var stompClient: StompClient? = null
    private val compositeDisposable = CompositeDisposable()
    private val gson = Gson()
    
    fun connect(onConnected: () -> Unit, onError: (Throwable) -> Unit) {
        val url = "$baseUrl/ws-delivery"
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url)
        
        val lifecycleDisposable = stompClient?.lifecycle()?.subscribe { lifecycleEvent ->
            when (lifecycleEvent.type) {
                LifecycleEvent.Type.OPENED -> {
                    Log.d("WebSocket", "Conectado")
                    onConnected()
                }
                LifecycleEvent.Type.ERROR -> {
                    Log.e("WebSocket", "Error: ${lifecycleEvent.exception}")
                    onError(lifecycleEvent.exception)
                }
                LifecycleEvent.Type.CLOSED -> {
                    Log.d("WebSocket", "Desconectado")
                }
                else -> {}
            }
        }
        
        lifecycleDisposable?.let { compositeDisposable.add(it) }
        stompClient?.connect()
    }
    
    fun subscribeToDelivery(
        pedidoId: Long,
        onUbicacionUpdate: (UbicacionUpdate) -> Unit
    ): Disposable? {
        return stompClient?.topic("/topic/delivery/$pedidoId")?.subscribe { message ->
            try {
                val update = gson.fromJson(message.payload, UbicacionUpdate::class.java)
                onUbicacionUpdate(update)
            } catch (e: Exception) {
                Log.e("WebSocket", "Error parsing message: ${e.message}")
            }
        }?.also { compositeDisposable.add(it) }
    }
    
    fun sendLocation(pedidoId: Long, latitud: Double, longitud: Double) {
        val request = UbicacionDeliveryRequest(pedidoId, latitud, longitud)
        val json = gson.toJson(request)
        
        stompClient?.send("/app/delivery/location", json)?.subscribe(
            {
                Log.d("WebSocket", "Ubicación enviada")
            },
            { error ->
                Log.e("WebSocket", "Error enviando ubicación: ${error.message}")
            }
        )?.let { compositeDisposable.add(it) }
    }
    
    fun disconnect() {
        compositeDisposable.clear()
        stompClient?.disconnect()
        stompClient = null
    }
    
    data class UbicacionUpdate(
        val tipo: String?,
        val pedidoId: Long,
        val latitud: Double,
        val longitud: Double,
        val timestamp: String?,
        val estado: String?,
        val mensaje: String?
    )
}
