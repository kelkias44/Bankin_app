package com.example.simplebankingapp.utils


import android.content.Context
import android.content.SharedPreferences
import com.example.simplebankingapp.domain.entity.AuthResponse
import com.example.simplebankingapp.domain.entity.RefreshTokenResponse
import com.example.simplebankingapp.domain.entity.User

class SharedPreferencesManager(context: Context) {
    private val mContext = context
    private val SHARED_PREFERENCE_NAME = "eli_shared_preference"

    fun login(authResponse: AuthResponse?){
        val sharedPreferences: SharedPreferences = mContext.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        if(authResponse != null){

            editor.putString(SharedPreferenceKeys.KEY_TOKEN, authResponse.accessToken)
            editor.putString(
                SharedPreferenceKeys.KEY_REFRESH_TOKEN,
                authResponse.refreshToken
            )

            editor.putString(SharedPreferenceKeys.KEY_USER_NAME, authResponse.username)
            editor.putInt(SharedPreferenceKeys.KEY_USER_ID, authResponse.userId)

            editor.putBoolean(SharedPreferenceKeys.KEY_IS_LOGGED_IN, true)
        }

        editor.apply()
    }

    fun userUpdate(user: User){
        val sharedPreferences: SharedPreferences = mContext.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        if(user != null){
            editor.putString(SharedPreferenceKeys.KEY_USER_NAME, user.username)
            editor.putString(SharedPreferenceKeys.KEY_FIRST_NAME, user.firstName)
            editor.putString(SharedPreferenceKeys.KEY_LAST_NAME, user.lastName)
            editor.putString(SharedPreferenceKeys.KEY_USER_PHONE, user.phoneNumber)


            editor.putBoolean(SharedPreferenceKeys.KEY_IS_LOGGED_IN, true)
        }

        editor.apply()
    }

    fun onRefreshToken(response: RefreshTokenResponse?){
        val sharedPreferences: SharedPreferences = mContext.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        if(response != null){

            editor.putString(SharedPreferenceKeys.KEY_TOKEN, response.accessToken)
            editor.putString(
                SharedPreferenceKeys.KEY_REFRESH_TOKEN,
                response.refreshToken
            )
        }

        editor.apply()
    }
    fun isLoggedIn():Boolean{
        val sharedPreferences: SharedPreferences = mContext.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(SharedPreferenceKeys.KEY_IS_LOGGED_IN,false)
    }

    fun getToken(): String?{
        val sharedPreferences: SharedPreferences = mContext.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(SharedPreferenceKeys.KEY_TOKEN,null)
    }

    fun getUserId(): Int {
        val sharedPreferences: SharedPreferences = mContext.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getInt(SharedPreferenceKeys.KEY_USER_ID, 0)
    }

    fun getUserName(): String{
        val sharedPreferences: SharedPreferences = mContext.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(SharedPreferenceKeys.KEY_USER_NAME,"")!!
    }

    fun getFirstName(): String{
        val sharedPreferences: SharedPreferences = mContext.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(SharedPreferenceKeys.KEY_FIRST_NAME,"")!!
    }

    fun getLastName(): String{
        val sharedPreferences: SharedPreferences = mContext.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(SharedPreferenceKeys.KEY_LAST_NAME,"")!!
    }

    fun getEmail(): String{
        val sharedPreferences: SharedPreferences = mContext.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(SharedPreferenceKeys.KEY_USER_EMAIL,"")!!
    }

    fun getUserPhone(): String{
        val sharedPreferences: SharedPreferences = mContext.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(SharedPreferenceKeys.KEY_USER_PHONE,"")!!
    }

    fun getPhotoID(): String{
        val sharedPreferences: SharedPreferences = mContext.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(SharedPreferenceKeys.KEY_USER_PHOTO_ID,"")!!
    }

    fun getPhotoPath(): String{
        val sharedPreferences: SharedPreferences = mContext.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(SharedPreferenceKeys.KEY_USER_PHOTO_PATH,"")!!
    }

    fun getRefreshToken(): String?{
        val sharedPreferences: SharedPreferences = mContext.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(SharedPreferenceKeys.KEY_REFRESH_TOKEN,null)
    }

    fun onLogout(){
        val sharedPreferences: SharedPreferences = mContext.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        editor.clear()
        editor.apply()
    }

}

class SharedPreferenceKeys{
    companion object {
        val KEY_IS_LOGGED_IN: String = "is_logged_in"

        val KEY_TOKEN: String = "access_token"
        val KEY_REFRESH_TOKEN: String = "refresh_token"
        val KEY_USER_ID = "user_id"
        val KEY_USER_EMAIL = "user_email"
        val KEY_USER_NAME = "user_name"
        val KEY_FIRST_NAME = "first_name"
        val KEY_LAST_NAME = "user_father_name"
        val KEY_USER_PHOTO_ID = "user_photo_id"
        val KEY_USER_PHOTO_PATH = "user_photo_path"
        val KEY_USER_PHONE = "user_phone"
    }
}