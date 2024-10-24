import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Crear la extensión del DataStore
val Context.dataStore by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    // Definir la clave para guardar la lista de strings
    private val STRING_LIST_KEY = stringPreferencesKey("string_list")

    // Guardar la lista de strings
    suspend fun saveStringList(stringList: List<String>) {
        context.dataStore.edit { preferences ->
            // Convertir la lista en un string con un delimitador (por ejemplo, usar JSON o una simple concatenación)
            preferences[STRING_LIST_KEY] = stringList.joinToString(",") // Almacena la lista separada por comas
        }
    }

    // Recuperar la lista de strings
    val stringListFlow: Flow<List<String>> = context.dataStore.data
        .map { preferences ->
            // Recuperar la cadena y dividirla de nuevo en una lista
            val storedString = preferences[STRING_LIST_KEY] ?: ""
            if (storedString.isNotEmpty()) {
                storedString.split(",") // Divide el string de vuelta en una lista
            } else {
                emptyList()
            }
        }
}
