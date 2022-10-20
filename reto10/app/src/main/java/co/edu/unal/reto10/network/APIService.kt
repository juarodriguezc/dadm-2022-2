package co.edu.unal.reto10.network

import co.edu.unal.reto10.model.BancoSangre
import co.edu.unal.reto10.network.APIConstants.BASE_URL
import co.edu.unal.reto10.network.APIConstants.ENDPOINT
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {
    @GET(ENDPOINT)
    suspend fun getBancosSangre(): List<BancoSangre>

    @GET(ENDPOINT)
    suspend fun getBancosSangreByDepartment(
        @Query(value = "departamento") department: String
    ): List<BancoSangre>

    companion object{
        var apiService: APIService? = null
        fun getInstance() : APIService {
            if(apiService == null){
                apiService = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build().create(APIService::class.java)
            }
            return apiService!!
        }
    }
}