package com.example.my_track_fit

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast

class WorkoutFragment : Fragment() {
    // adapter de routine
    private lateinit var adapter: RoutineAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_workout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Acceder al workout de MainActivity
        val workout = (activity as? MainActivity)?.workout
    
        //funcion para detectar el long click en una rutina
        val onRoutineLongClick: (com.example.my_track_fit.model.Routine, Int) -> Unit = { routine, position ->
            val options = arrayOf("Cambiar nombre", "Eliminar rutina")
            AlertDialog.Builder(requireContext())
                .setTitle("Opciones de rutina")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> { // Cambiar nombre
                            val renameView = LayoutInflater.from(requireContext())
                                .inflate(R.layout.dialog_rename_routine, null)
                            val etNewName = renameView.findViewById<EditText>(R.id.etRoutineName)
                            etNewName.setText(routine.getName())
                            AlertDialog.Builder(requireContext())
                                .setTitle("Cambiar nombre de rutina")
                                .setView(renameView)
                                .setPositiveButton("Aceptar") { _, _ ->
                                    val newName = etNewName.text.toString().trim()
                                    if (newName.isNotEmpty()) {
                                        routine.setName(newName)
                                        adapter.notifyItemChanged(position)
                                    } else {
                                        Toast.makeText(requireContext(), "Debe escribir al menos un caracter", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .setNegativeButton("Cancelar", null)
                                .show()
                        }
                        1 -> { // Eliminar rutina
                            AlertDialog.Builder(requireContext())
                                .setTitle("Eliminar rutina")
                                .setMessage("¿Realmente quieres borrar la rutina \"${routine.getName()}\"?")
                                .setPositiveButton("Aceptar") { _, _ ->
                                    workout?.deleteRoutine(routine)
                                    adapter.notifyDataSetChanged()
                                }
                                .setNegativeButton("Cancelar", null)
                                .show()
                        }
                    }
                }
                .show()
        }
        
        // Inicializar el adapter con la lista de rutinas
        val routinesRecycler = view.findViewById<RecyclerView>(R.id.routinesListView)
        adapter = RoutineAdapter(
            workout?.getRoutines() ?: listOf(),
            onRoutineLongClick = onRoutineLongClick,
            onRoutineClick = { routine, position ->
                // Navegar al fragmento de detalle
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, RoutineDetailFragment.newInstance(position))
                    .addToBackStack(null)
                    .commit()
            }
        )
        routinesRecycler.layoutManager = LinearLayoutManager(requireContext())
        routinesRecycler.adapter = adapter

        val addRoutineBtn = view.findViewById<View>(R.id.addRoutine)
        addRoutineBtn.setOnClickListener {
            val dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_routine, null)
            val etRoutineName = dialogView.findViewById<EditText>(R.id.etRoutineName)
            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create()

            dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
                dialog.dismiss()
            }
            dialogView.findViewById<Button>(R.id.btnAccept).setOnClickListener {
                //manejar el nombre ingresado de la rutina
                val nombre = etRoutineName.text.toString()
                if (nombre.isEmpty()) {
                    // Notifica al usuario que debe escribir al menos un caracter
                    Toast.makeText(requireContext(), "Debe de escribir al menos un carácter!", Toast.LENGTH_SHORT).show()
                }
                else {
                    workout?.addRoutine(nombre)
                    adapter.notifyDataSetChanged()
                    dialog.dismiss()
                }
            }
            dialog.show()
        }
    }
}