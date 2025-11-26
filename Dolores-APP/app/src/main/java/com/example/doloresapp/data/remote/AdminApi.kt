package com.example.doloresapp.data.remote

import retrofit2.http.*

// DTOs para usuarios
data class UsuarioAdmin(
    val idUsuarios: Long?,
    val usuario: String?,
    val correo: String?,
    val contrasena: String? = null,
    val estado: String?,
    val fecha_creacion: String? = null
)

data class RolResponse(
    val idRoles: Long,
    val nombre: String?,
    val descripcion: String?,
    val estado: String?
)

data class UsuarioRolRequest(
    val usuarios: UsuarioRef,
    val roles: RolRef,
    val estado: String = "ACTIVO"
)

data class UsuarioRef(val idUsuarios: Long)
data class RolRef(val idRoles: Long)

data class UsuarioRolResponse(
    val idUsuarioRol: Long?,
    val usuarios: UsuarioAdmin?,
    val roles: RolResponse?,
    val estado: String?
)

data class CrearUsuarioRequest(
    val usuario: String,
    val correo: String,
    val contrasena: String,
    val estado: String = "ACTIVO"
)

interface AdminApi {
    
    // Usuarios
    @GET("api/usuarios")
    suspend fun getAllUsuarios(): List<UsuarioAdmin>
    
    @GET("api/usuarios/{id}")
    suspend fun getUsuarioById(@Path("id") id: Long): UsuarioAdmin
    
    @POST("api/usuarios")
    suspend fun crearUsuario(@Body usuario: CrearUsuarioRequest): UsuarioAdmin
    
    @PUT("api/usuarios/{id}")
    suspend fun actualizarUsuario(@Path("id") id: Long, @Body usuario: UsuarioAdmin): UsuarioAdmin
    
    @DELETE("api/usuarios/{id}")
    suspend fun eliminarUsuario(@Path("id") id: Long)
    
    // Roles
    @GET("api/roles")
    suspend fun getAllRoles(): List<RolResponse>
    
    // Usuario-Rol
    @GET("api/usuariorol")
    suspend fun getAllUsuarioRoles(): List<UsuarioRolResponse>
    
    @POST("api/usuariorol")
    suspend fun asignarRol(@Body usuarioRol: UsuarioRolRequest): UsuarioRolResponse
    
    @DELETE("api/usuariorol/{id}")
    suspend fun eliminarUsuarioRol(@Path("id") id: Long)
}
