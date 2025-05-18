package com.example.my_track_fit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.my_track_fit.R
import com.example.my_track_fit.model.Block
import com.example.my_track_fit.MainActivity
import com.example.my_track_fit.BlockDetailFragment

class BlockAdapter(
    private var blocks: List<Block>,
    private val routineIndex: Int,
    private val weekIndex: Int
) : RecyclerView.Adapter<BlockAdapter.BlockViewHolder>() {

    class BlockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvBlockName: TextView = itemView.findViewById(R.id.tvBlockName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_block, parent, false)
        return BlockViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlockViewHolder, position: Int) {
        holder.tvBlockName.text = blocks[position].getName()
        holder.itemView.setOnClickListener {
            (holder.itemView.context as? MainActivity)?.supportFragmentManager?.beginTransaction()
                ?.replace(
                    R.id.fragment_container,
                    BlockDetailFragment.newInstance(routineIndex, weekIndex, position)
                )
                ?.addToBackStack(null)
                ?.commit()
        }
    }

    override fun getItemCount(): Int = blocks.size

    fun updateBlocks(newBlocks: List<Block>) {
        blocks = newBlocks
        notifyDataSetChanged()
    }
}