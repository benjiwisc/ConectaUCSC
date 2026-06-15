package com.ucsc.conectaucsc.data.remote

import com.ucsc.conectaucsc.data.model.ArchivoEstudio
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ArchivosEstudioApiService {
    @GET("materias/{materiaId}/file-management")
    suspend fun getArchivos(@Path("materiaId") materiaId: Int): Response<List<ArchivoEstudio>>

    @Multipart
    @POST("materias/{materiaId}/file-management")
    suspend fun uploadArchivo(
        @Path("materiaId") materiaId: Int,
        @Part("titulo") titulo: RequestBody,
        @Part("descripcion") descripcion: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<ArchivoEstudio>

    @GET("file-management/{fileId}/descargar")
    @Streaming
    suspend fun descargarArchivo(@Path("fileId") fileId: Int): Response<ResponseBody>

    @DELETE("file-management/{fileId}")
    suspend fun eliminarArchivo(@Path("fileId") fileId: Int): Response<Unit>
}