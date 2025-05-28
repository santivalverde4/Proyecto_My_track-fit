package com.example.my_track_fit.adapters

import com.google.gson.* // Importa las clases necesarias de Gson para serialización y deserialización
import java.lang.reflect.Type // Importa la clase Type para manejo de tipos genéricos
import java.time.LocalDate // Importa la clase LocalDate para fechas sin tiempo

// Adaptador personalizado para convertir LocalDate a JSON y viceversa usando Gson
class LocalDateAdapter : JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
    // Método para serializar un LocalDate a un JsonElement (usado al convertir a JSON)
    override fun serialize(src: LocalDate?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src.toString()) // Convierte el LocalDate a String y lo envuelve como JsonPrimitive
    }

    // Método para deserializar un JsonElement a un LocalDate (usado al leer desde JSON)
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalDate {
        return LocalDate.parse(json?.asString) // Parsea el string del JSON a un objeto LocalDate
    }
}