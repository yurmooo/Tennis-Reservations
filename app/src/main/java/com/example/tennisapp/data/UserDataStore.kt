package com.example.tennisapp.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("user_prefs")

object UserDataStore {
    private val CLIENT_ID = intPreferencesKey("client_id")

    fun getClientId(context: Context): Flow<Int?> {
        return context.dataStore.data.map { prefs ->
            prefs[CLIENT_ID]
        }
    }

    suspend fun saveClientId(context: Context, id: Int) {
        context.dataStore.edit { prefs ->
            prefs[CLIENT_ID] = id
        }
    }

    suspend fun clearClientId(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.remove(CLIENT_ID)
        }
    }
}

