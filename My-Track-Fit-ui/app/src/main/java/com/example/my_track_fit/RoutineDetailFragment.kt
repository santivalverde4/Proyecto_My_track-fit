package com.example.my_track_fit

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.my_track_fit.model.Routine
import android.widget.Toast
import android.widget.Spinner
import android.widget.ArrayAdapter
import com.example.my_track_fit.BlockAdapter

class RoutineDetailFragment : Fragment() {
    companion object {
        private const val ARG_ROUTINE_INDEX = "routine_index"
        fun newInstance(routineIndex: Int): RoutineDetailFragment {
            val fragment = RoutineDetailFragment()
            val args = Bundle()
            args.putInt(ARG_ROUTINE_INDEX, routineIndex)
            fragment.arguments = args
            return fragment
        }
    }

    private var routine: Routine? = null
    private lateinit var blockAdapter: BlockAdapter
    private var routineIndex: Int = -1
    private lateinit var spinnerWeeks: Spinner
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnAddWeek: Button
    private lateinit var btnAddBlock: Button

    // Haz que el callback sea propiedad de la clase
    private val onBlockLongClick: (com.example.my_track_fit.model.Block, Int) -> Unit = { block, position ->
        val selectedWeekIndex = spinnerWeeks.selectedItemPosition
        val week = routine?.getWeeks()?.getOrNull(selectedWeekIndex)
        val context = requireContext()
        val options = arrayOf("Cambiar nombre", "Eliminar bloque")
        AlertDialog.Builder(context)
            .setTitle("Opciones de bloque")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        val editText = EditText(context)
                        editText.setText(block.getName())
                        AlertDialog.Builder(context)
                            .setTitle("Cambiar nombre del bloque")
                            .setView(editText)
                            .setPositiveButton("Aceptar") { _, _ ->
                                val newName = editText.text.toString().trim()
                                if (newName.isNotEmpty()) {
                                    block.setName(newName)
                                    updateBlocksForSelectedWeek()
                                } else {
                                    Toast.makeText(context, "Debe escribir al menos un caracter", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .setNegativeButton("Cancelar", null)
                            .show()
                    }
                    1 -> {
                        AlertDialog.Builder(context)
                            .setTitle("Eliminar bloque")
                            .setMessage("¿Realmente quieres borrar el bloque \"${block.getName()}\"?")
                            .setPositiveButton("Aceptar") { _, _ ->
                                week?.deleteBlock(block)
                                val weeks = routine?.getWeeks()
                                if (week != null && week.getBlockList().isEmpty()) {
                                    if (weeks != null && weeks.size > 1) {
                                        weeks.removeAt(selectedWeekIndex)
                                        Toast.makeText(context, "Semana eliminada porque no tiene bloques", Toast.LENGTH_SHORT).show()
                                        updateWeeksSpinnerAndButton((selectedWeekIndex - 1).coerceAtLeast(0))
                                    } else {
                                        // Si es la única semana, solo actualiza la vista
                                        updateBlocksForSelectedWeek()
                                    }
                                } else {
                                    updateBlocksForSelectedWeek()
                                }
                                // Si solo queda una semana y está vacía, oculta el botón "+ semana"
                                if (weeks != null && weeks.size == 1 && weeks[0].getBlockList().isEmpty()) {
                                    btnAddWeek.visibility = View.GONE
                                } else {
                                    btnAddWeek.visibility = View.VISIBLE
                                }
                            }
                            .setNegativeButton("Cancelar", null)
                            .show()
                    }
                }
            }
            .show()
    }

    private fun updateBlocksForSelectedWeek() {
        val selectedWeekIndex = spinnerWeeks.selectedItemPosition
        val week = routine?.getWeeks()?.getOrNull(selectedWeekIndex)
        blockAdapter = BlockAdapter(
            week?.getBlockList() ?: listOf(),
            routineIndex,
            selectedWeekIndex,
            onBlockLongClick
        )
        recyclerView.adapter = blockAdapter
    }

    private fun updateWeeksSpinnerAndButton(selectedIndex: Int = spinnerWeeks.selectedItemPosition) {
        val weeks = routine?.getWeeks() ?: mutableListOf()
        val weekNames = weeks.mapIndexed { index, _ -> "Semana ${index + 1}" }
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, weekNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerWeeks.adapter = adapter

        btnAddWeek.visibility = if (
            weeks.size == 1 && weeks[0].getBlockList().isEmpty()
        ) View.GONE else View.VISIBLE

        // Selecciona la semana indicada (o la última si el índice es inválido)
        if (weeks.isNotEmpty()) {
            spinnerWeeks.setSelection(selectedIndex.coerceAtMost(weeks.size - 1))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_routine_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        routineIndex = arguments?.getInt(ARG_ROUTINE_INDEX) ?: -1
        val workout = (activity as? MainActivity)?.workout
        routine = workout?.getRoutines()?.getOrNull(routineIndex)

        val tvRoutineName = view.findViewById<TextView>(R.id.tvRoutineName)
        recyclerView = view.findViewById(R.id.blocksRecyclerView)
        spinnerWeeks = view.findViewById(R.id.spinnerWeeks)
        btnAddWeek = view.findViewById(R.id.btnAddWeek)
        btnAddBlock = view.findViewById(R.id.btnAddBlock)

        tvRoutineName.text = routine?.getName() ?: ""

        // Inicializar el RecyclerView y el adapter con los bloques de la primera semana (o lista vacía)
        val initialWeekIndex = 0
        val initialBlocks = routine?.getWeeks()?.firstOrNull()?.getBlockList() ?: listOf()
        blockAdapter = BlockAdapter(
            initialBlocks,
            routineIndex,
            initialWeekIndex
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = blockAdapter

        updateWeeksSpinnerAndButton()

        spinnerWeeks.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>, view: View?, position: Int, id: Long) {
                updateBlocksForSelectedWeek()
            }
            override fun onNothingSelected(parent: android.widget.AdapterView<*>) {}
        })

        btnAddWeek.setOnClickListener {
            val options = arrayOf("Copiar semana completa", "Copiar ejercicios de la semana")
            AlertDialog.Builder(requireContext())
                .setTitle("Agregar semana")
                .setItems(options) { dialog, which ->
                    val selectedWeekIndex = spinnerWeeks.selectedItemPosition
                    val selectedWeek = routine?.getWeeks()?.getOrNull(selectedWeekIndex)
                    if (selectedWeek != null) {
                        val newWeek = when (which) {
                            0 -> routine?.copyWeekData(selectedWeek)
                            1 -> routine?.copyWeekNoData(selectedWeek)
                            else -> null
                        }
                        if (newWeek != null) {
                            routine?.getWeeks()?.add(newWeek)
                            updateWeeksSpinnerAndButton((routine?.getWeeks()?.size ?: 1) - 1)
                            spinnerWeeks.setSelection((routine?.getWeeks()?.size ?: 1) - 1)
                        }
                    }
                }
                .setNegativeButton("Cancelar") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        btnAddBlock.setOnClickListener {
            val dialogView = layoutInflater.inflate(android.R.layout.simple_list_item_1, null)
            val editText = EditText(requireContext())
            editText.hint = "Nombre del bloque"

            AlertDialog.Builder(requireContext())
                .setTitle("Agregar bloque")
                .setView(editText)
                .setPositiveButton("Aceptar") { dialog, _ ->
                    val blockName = editText.text.toString().trim()
                    val selectedWeekIndex = spinnerWeeks.selectedItemPosition
                    val week = routine?.getWeeks()?.getOrNull(selectedWeekIndex)
                    if (blockName.isNotEmpty() && week != null) {
                        week.addBlock(blockName)
                        updateBlocksForSelectedWeek() //actualizar el recycler view
                        updateWeeksSpinnerAndButton(spinnerWeeks.selectedItemPosition) //mostrar una el botón para añadir semanas
                        Toast.makeText(requireContext(), "Bloque agregado", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Debes ingresar un nombre", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }
    }
}