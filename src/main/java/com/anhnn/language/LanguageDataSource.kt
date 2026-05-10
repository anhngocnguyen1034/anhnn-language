package com.anhnn.language

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.anhnnLanguageDataStore: DataStore<Preferences> by preferencesDataStore(name = "anhnn_language_settings")

class LanguageDataSource(context: Context) {
    private val dataStore = context.anhnnLanguageDataStore

    companion object {
        private val LANGUAGE_CODE_KEY = stringPreferencesKey("language_code")
    }

    val languageCode: Flow<String> = dataStore.data.map { preferences ->
        preferences[LANGUAGE_CODE_KEY] ?: "en"
    }

    suspend fun setLanguageCode(code: String) {
        dataStore.edit { preferences ->
            preferences[LANGUAGE_CODE_KEY] = code
        }
    }
}
