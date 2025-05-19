package com.example.my_track_fit

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.my_track_fit.databinding.ActivityBodyweightBinding
import com.example.my_track_fit.network.*
import com.example.my_track_fit.UserSession

class BodyweightActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBodyweightBinding
    private lateinit var adapter: BodyweightAdapter

    // Obtener el userId usando UserSession
    private val userId: Int by lazy { UserSession.getUserId(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBodyweightBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Botón volver
        binding.btnBack.setOnClickListener {
            finish()
        }

        // Botón agregar
        binding.btnAdd.setOnClickListener {
            showAddBodyweightDialog()
        }

        // Configura RecyclerView
        adapter = BodyweightAdapter { bodyweight: Bodyweight ->
            showBodyweightOptions(bodyweight)
        }
        binding.recyclerBodyweights.layoutManager = LinearLayoutManager(this)
        binding.recyclerBodyweights.adapter = adapter

        // Carga los bodyweights del usuario
        loadBodyweights()
    }

    private fun loadBodyweights() {
        if (userId == -1) {
            Toast.makeText(this, "Usuario no válido", Toast.LENGTH_SHORT).show()
            return
        }
        RetrofitClient.instance.getBodyweights(userId).enqueue(object : Callback<List<Bodyweight>> {
            override fun onResponse(call: Call<List<Bodyweight>>, response: Response<List<Bodyweight>>) {
                if (response.isSuccessful) {
                    adapter.submitList(response.body() ?: emptyList())
                } else {
                    Toast.makeText(this@BodyweightActivity, "Error al cargar datos", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<List<Bodyweight>>, t: Throwable) {
                Toast.makeText(this@BodyweightActivity, "Error de red", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showAddBodyweightDialog() {
        val input = EditText(this)
        input.hint = "Peso (kg)"
        AlertDialog.Builder(this)
            .setTitle("Agregar bodyweight")
            .setView(input)
            .setPositiveButton("Agregar") { _, _ ->
                val pesoStr = input.text.toString()
                val peso = pesoStr.toIntOrNull()
                if (peso != null) {
                    createBodyweight(peso)
                } else {
                    Toast.makeText(this, "Peso inválido", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showBodyweightOptions(bodyweight: Bodyweight) {
        val options = arrayOf("Editar", "Borrar")
        AlertDialog.Builder(this)
            .setTitle("Opciones")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> editBodyweight(bodyweight)
                    1 -> deleteBodyweight(bodyweight)
                }
            }
            .show()
    }

    private fun createBodyweight(peso: Int) {
        if (userId == -1) {
            Toast.makeText(this, "Usuario no válido", Toast.LENGTH_SHORT).show()
            return
        }
        val req = CreateBodyweightRequest(userId, peso)
        RetrofitClient.instance.createBodyweight(req).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    loadBodyweights()
                } else {
                    Toast.makeText(this@BodyweightActivity, "Error al agregar", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@BodyweightActivity, "Error de red", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun editBodyweight(bodyweight: Bodyweight) {
        val input = EditText(this)
        input.setText(bodyweight.peso.toString())
        input.hint = "Peso (kg)"
        AlertDialog.Builder(this)
            .setTitle("Editar bodyweight")
            .setView(input)
            .setPositiveButton("Guardar") { _, _ ->
                val pesoStr = input.text.toString()
                val peso = pesoStr.toIntOrNull()
                if (peso != null) {
                    updateBodyweight(bodyweight.id, peso)
                } else {
                    Toast.makeText(this, "Peso inválido", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun updateBodyweight(id: Int, peso: Int) {
        if (userId == -1) {
            Toast.makeText(this, "Usuario no válido", Toast.LENGTH_SHORT).show()
            return
        }
        val req = UpdateBodyweightRequest(userId, peso)
        RetrofitClient.instance.updateBodyweight(id, req).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    loadBodyweights()
                } else {
                    Toast.makeText(this@BodyweightActivity, "Error al actualizar", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@BodyweightActivity, "Error de red", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteBodyweight(bodyweight: Bodyweight) {
        if (userId == -1) {
            Toast.makeText(this, "Usuario no válido", Toast.LENGTH_SHORT).show()
            return
        }
        val req = DeleteBodyweightRequest(userId)
        RetrofitClient.instance.deleteBodyweight(bodyweight.id, req).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    loadBodyweights()
                } else {
                    Toast.makeText(this@BodyweightActivity, "Error al borrar", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@BodyweightActivity, "Error de red", Toast.LENGTH_SHORT).show()
            }
        })
    }
}