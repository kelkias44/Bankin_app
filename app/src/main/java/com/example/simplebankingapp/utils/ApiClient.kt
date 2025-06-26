import android.content.Context
import android.content.Intent
import com.example.simplebankingapp.presentation.activities.auth.LoginActivity
import com.example.simplebankingapp.data.model.RefreshTokenRequest
import com.example.simplebankingapp.data.model.Result
import com.example.simplebankingapp.data.remote.IAuthService
import com.example.simplebankingapp.domain.entity.AuthResponse
import com.example.simplebankingapp.domain.entity.RefreshTokenResponse
import com.example.simplebankingapp.utils.SharedPreferencesManager
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    @Volatile
    private var retrofit: Retrofit? = null
    private const val BASE_URL = ""

    fun getRetrofitInstance(context: Context): Retrofit {
        return retrofit ?: synchronized(this) {
            retrofit ?: buildRetrofit(context).also { retrofit = it }
        }
    }

    private fun buildRetrofit(context: Context): Retrofit {
        val tokenAuthenticator = TokenAuthenticator(context)
        val tokenInterceptor = TokenInterceptor(context)

        val httpClient = OkHttpClient.Builder()
            .connectTimeout(50, TimeUnit.SECONDS)
            .readTimeout(50, TimeUnit.SECONDS)
            .writeTimeout(50, TimeUnit.SECONDS)
            .addInterceptor(tokenInterceptor)
            .authenticator(tokenAuthenticator)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

class TokenInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val token = SharedPreferencesManager(context).getToken()

        val newRequest = if (token != null) {
            request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }

        return chain.proceed(newRequest)
    }
}

class TokenAuthenticator(private val context: Context) : Authenticator {

    private val tokenRefreshMutex = Mutex()

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code != 401) {
            return null
        }

        return runBlocking {
            tokenRefreshMutex.withLock {
                val latestAccessToken = SharedPreferencesManager(context).getToken()
                if (latestAccessToken != null && response.request.header("Authorization") == "Bearer $latestAccessToken") {

                    clearAndRedirectToLogin(context)
                    return@runBlocking null // Authentication failed definitively
                }

                val refreshToken = SharedPreferencesManager(context).getRefreshToken()
                if (refreshToken == null) {
                    clearAndRedirectToLogin(context)
                    return@runBlocking null // Authentication failed
                }

                val newAuthResponse = refreshAccessToken(refreshToken)

                if (newAuthResponse != null) {
                    // Successfully refreshed, save the new access token.
                    SharedPreferencesManager(context).onRefreshToken(newAuthResponse)
                    // Retry the original request with the newly obtained access token.
                    return@runBlocking response.request.newBuilder()
                        .header("Authorization", "Bearer ${newAuthResponse.accessToken}")
                        .build()
                } else {
                    // Token refresh failed (e.g., refresh token itself expired or is invalid).
                    clearAndRedirectToLogin(context)
                    return@runBlocking null // Authentication failed
                }
            }
        }
    }

    // Helper method to perform the actual access token refresh API call
    private fun refreshAccessToken(refreshToken: String): RefreshTokenResponse? {
        val retrofit = ApiClient.getRetrofitInstance(context)
        val service = retrofit.create(IAuthService::class.java)

        return runBlocking { // Keep runBlocking as the authenticator is synchronous
            try {
                val response = service.refreshToken(RefreshTokenRequest(refreshToken))
                if (response.isSuccessful) {
                    response.body()?.toRefreshTokenResponse() // Return the parsed RefreshTokenResponse
                } else {
                    println("Refresh token failed with code: ${response.code()}, message: ${response.errorBody()?.string()}")
                    null
                }
            } catch (e: Exception) {
                // Log any exceptions during the refresh call (e.g., network issues)
                e.printStackTrace()
                null
            }
        }
    }

    // Helper method to clear tokens and redirect to login activity
    private fun clearAndRedirectToLogin(context: Context) {
        SharedPreferencesManager(context).onLogout()
        val intent = Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)
    }
}