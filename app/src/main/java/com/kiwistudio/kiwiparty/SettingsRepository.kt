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

    private val preferences = context.getSharedPreferences("settings_prefs", Context.MODE_PRIVATE)

    // Obtener la lista de strings desde SharedPreferences
    fun getStringList(): List<String> {
        val serializedList = preferences.getString("string_list", null)
        return serializedList?.split(",") ?: emptyList()
    }

    // Guardar la lista de strings en SharedPreferences
    fun saveStringList(list: List<String>) {
        val serializedList = list.joinToString(",") // Convierte la lista a una sola cadena
        preferences.edit().putString("string_list", serializedList).apply()
    }
}
