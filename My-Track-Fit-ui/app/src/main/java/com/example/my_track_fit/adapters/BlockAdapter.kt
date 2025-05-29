package com.example.my_track_fit.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.my_track_fit.R
import com.example.my_track_fit.model.Block
import com.example.my_track_fit.MainActivity
import com.example.my_track_fit.fragments.BlockDetailFragment

// Adaptador para mostrar una lista de bloques en un RecyclerView
class BlockAdapter(
    private var blocks: List<Block>, // Lista de bloques a mostrar
    private val routineIndex: Int,   // Índice de la rutina a la que pertenece el bloque
    private val weekIndex: Int,      // Índice de la semana a la que pertenece el bloque
    private val onBlockLongClick: ((Block, Int) -> Unit)? = null // Callback para long click en un bloque
) : RecyclerView.Adapter<BlockAdapter.BlockViewHolder>() {

    // ViewHolder que contiene la vista de cada bloque
    class BlockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBlockName: TextView = itemView.findViewById(R.id.tvBlockName) // TextView para el nombre del bloque
    }

    // Crea una nueva vista para un bloque (inflando el layout)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_block, parent, false) // Infla el layout de un bloque
        return BlockViewHolder(view)
    }

    // Asigna los datos de un bloque a la vista correspondiente
    override fun onBindViewHolder(holder: BlockViewHolder, position: Int) {
        holder.tvBlockName.text = blocks[position].getName() // Muestra el nombre del bloque

        // Al hacer click, abre el detalle del bloque en un fragmento
        holder.itemView.setOnClickListener {
            (holder.itemView.context as? MainActivity)?.supportFragmentManager?.beginTransaction()
                ?.replace(
                    R.id.fragment_container,
                    BlockDetailFragment.newInstance(routineIndex, weekIndex, position)
                )
                ?.addToBackStack(null)
                ?.commit()
        }

        // Al hacer long click, ejecuta el callback si existe
        holder.itemView.setOnLongClickListener {
            onBlockLongClick?.invoke(blocks[position], position)
            true
        }
    }

    // Devuelve la cantidad de bloques en la lista
    override fun getItemCount(): Int = blocks.size

    // Actualiza la lista de bloques y refresca el RecyclerView
    fun updateBlocks(newBlocks: List<Block>) {
        blocks = newBlocks
        notifyDataSetChanged()
    }
}