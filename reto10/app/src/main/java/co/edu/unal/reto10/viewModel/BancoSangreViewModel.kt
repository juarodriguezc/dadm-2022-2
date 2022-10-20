package co.edu.unal.reto10.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.edu.unal.reto10.model.BancoSangre
import co.edu.unal.reto10.network.APIService
import kotlinx.coroutines.launch

class BancoSangreViewModel: ViewModel() {
    var bancoSangreListResponse:List<BancoSangre> by mutableStateOf(listOf())
    var errorMessage: String by mutableStateOf("")
    var selectedFilter by mutableStateOf("")

    fun getBancosSangreList(){
        viewModelScope.launch {
            val apiService = APIService.getInstance()
            try {
                val bancoSangreList = apiService.getBancosSangre()
                bancoSangreListResponse = bancoSangreList
            }
            catch (e:Exception){
                errorMessage = e.message.toString()
            }
        }
    }

    fun getBancosSangreListByDepartment(department: String){
        viewModelScope.launch {
            val apiService = APIService.getInstance()
            try {
                val bancoSangreList = apiService.getBancosSangreByDepartment(department)
                bancoSangreListResponse = bancoSangreList
            }
            catch (e:Exception){
                errorMessage = e.message.toString()
            }
        }
    }
}