package com.example.doloresapp.utils

object ApiConstants {
    // Base URL - Cambiar según entorno
    const val BASE_URL = "http://10.0.2.2:8090/" // Android Emulator
    // const val BASE_URL = "http://localhost:8090/" // Para testing local
    // const val BASE_URL = "https://api.farmaciadolores.com/" // Producción
    
    // WebSocket
    const val WEBSOCKET_URL = "ws://10.0.2.2:8090/ws-delivery"
    
    // Endpoints
    object Auth {
        const val LOGIN = "api/auth/login"
        const val REGISTER = "api/auth/register"
    }
    
    object Productos {
        const val GET_ALL = "api/productos"
        const val GET_BY_ID = "api/productos/{id}"
        const val GET_BY_QR = "api/productos/{id}/mobile"
        const val GET_BY_CATEGORIA = "api/productos/categoria/{categoriaId}"
    }
    
    object Recetas {
        const val PROCESAR = "api/recetas-digitales/procesar"
        const val GET_BY_CLIENTE = "api/recetas-digitales/cliente/{clienteId}"
        const val GET_BY_ID = "api/recetas-digitales/{id}"
        const val VALIDAR = "api/recetas-digitales/{id}/validar"
    }
    
    object Fidelizacion {
        const val CREAR = "api/fidelizacion/crear"
        const val GET_PUNTOS = "api/fidelizacion/cliente/{clienteId}"
        const val CANJEAR = "api/fidelizacion/canjear"
        const val HISTORIAL = "api/fidelizacion/historial/{clienteId}"
    }
    
    object Pedidos {
        const val GET_ALL = "api/pedidos"
        const val GET_BY_ID = "api/pedidos/{id}"
        const val GET_BY_CLIENTE = "api/pedidos/cliente/{clienteId}"
        const val CREATE = "api/pedidos"
        const val UPDATE_ESTADO = "api/pedidos/{id}/estado"
    }
    
    object Delivery {
        const val UPDATE_LOCATION = "api/delivery/location"
        const val UPDATE_STATUS = "api/delivery/status"
        const val GET_UBICACION = "api/delivery/pedido/{pedidoId}/ubicacion"
    }
    
    object Upload {
        const val PERFIL = "api/upload/perfil"
        const val PRODUCTO = "api/upload/producto"
        const val RECETA = "api/upload/receta"
    }
    
    // SharedPreferences Keys
    object Prefs {
        const val NAME = "dolores_app_prefs"
        const val TOKEN = "auth_token"
        const val USER_ID = "user_id"
        const val USER_EMAIL = "user_email"
        const val USER_DNI = "user_dni"
        const val IS_LOGGED_IN = "is_logged_in"
    }
    
    // Request Codes
    object RequestCodes {
        const val QR_SCANNER = 1001
        const val IMAGE_PICKER = 1002
        const val CAMERA = 1003
        const val LOCATION_PERMISSION = 1004
    }
    
    // Estados de Pedido
    object EstadoPedido {
        const val PENDIENTE = "PENDIENTE"
        const val PREPARANDO = "PREPARANDO"
        const val LISTO = "LISTO"
        const val EN_CAMINO = "EN_CAMINO"
        const val ENTREGADO = "ENTREGADO"
        const val CANCELADO = "CANCELADO"
    }
    
    // Estados de Receta
    object EstadoReceta {
        const val PENDIENTE = "PENDIENTE"
        const val PROCESADA = "PROCESADA"
        const val VALIDADA = "VALIDADA"
        const val RECHAZADA = "RECHAZADA"
    }
    
    // Niveles de Membresía
    object NivelMembresia {
        const val BRONCE = "BRONCE"
        const val PLATA = "PLATA"
        const val ORO = "ORO"
        const val PLATINO = "PLATINO"
    }
}
