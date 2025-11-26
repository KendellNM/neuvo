package com.example.doloresapp.utils

/**
 * Roles de usuario en la aplicación
 */
enum class UserRole {
    CLIENTE,
    REPARTIDOR,
    ADMIN,
    FARMACEUTICO
}

/**
 * Helper para gestionar roles de usuario
 */
object RoleManager {
    
    private const val KEY_USER_ROLE = "user_role"
    
    /**
     * Guarda el rol del usuario al hacer login
     */
    fun saveUserRole(context: android.content.Context, role: UserRole) {
        val prefs = context.getSharedPreferences(ApiConstants.Prefs.NAME, android.content.Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_USER_ROLE, role.name)
            .apply()
    }
    
    /**
     * Obtiene el rol del usuario actual
     */
    fun getUserRole(context: android.content.Context): UserRole {
        val prefs = context.getSharedPreferences(ApiConstants.Prefs.NAME, android.content.Context.MODE_PRIVATE)
        val roleName = prefs.getString(KEY_USER_ROLE, UserRole.CLIENTE.name)
        return try {
            UserRole.valueOf(roleName ?: UserRole.CLIENTE.name)
        } catch (e: Exception) {
            UserRole.CLIENTE
        }
    }
    
    /**
     * Verifica si el usuario es cliente
     */
    fun isCliente(context: android.content.Context): Boolean {
        return getUserRole(context) == UserRole.CLIENTE
    }
    
    /**
     * Verifica si el usuario es repartidor
     */
    fun isRepartidor(context: android.content.Context): Boolean {
        return getUserRole(context) == UserRole.REPARTIDOR
    }
    
    /**
     * Verifica si el usuario es admin
     */
    fun isAdmin(context: android.content.Context): Boolean {
        return getUserRole(context) == UserRole.ADMIN
    }
    
    /**
     * Limpia el rol al hacer logout
     */
    fun clearUserRole(context: android.content.Context) {
        val prefs = context.getSharedPreferences(ApiConstants.Prefs.NAME, android.content.Context.MODE_PRIVATE)
        prefs.edit()
            .remove(KEY_USER_ROLE)
            .apply()
    }
    
    /**
     * Obtiene el rol desde la respuesta del backend
     * El backend retorna roles como: ["ROLE_CLIENTE"], ["ROLE_REPARTIDOR"], etc.
     */
    fun parseRoleFromBackend(roles: List<String>): UserRole {
        return when {
            roles.any { it.contains("ADMIN", ignoreCase = true) } -> UserRole.ADMIN
            roles.any { it.contains("REPARTIDOR", ignoreCase = true) } -> UserRole.REPARTIDOR
            roles.any { it.contains("FARMACEUTICO", ignoreCase = true) } -> UserRole.FARMACEUTICO
            else -> UserRole.CLIENTE
        }
    }
}
