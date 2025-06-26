import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.simplebankingapp.data.repository.AuthRepositoryImpl
import com.example.simplebankingapp.domain.entity.AuthResponse
import com.example.simplebankingapp.domain.usecases.LoginUseCase
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    val context = application.applicationContext

    private val _uiState = MutableLiveData<UiState>()
    val uiState: LiveData<UiState> = _uiState
    private val authRepository = AuthRepositoryImpl(context)
    private val loginUseCase = LoginUseCase(authRepository)

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            val result = loginUseCase.invoke(username, password)

            _uiState.value = when {
                result.isSuccess -> {
                    val authResponse = result.getOrNull()!!
                    UiState.Success(authResponse)
                }
                else -> {
                    UiState.Error(result.exceptionOrNull()?.message ?: "Login failed")
                }
            }
        }
    }

    sealed class UiState {
        object Loading : UiState()
        data class Success(val authResponse: AuthResponse) : UiState()
        data class Error(val message: String) : UiState()
    }
}