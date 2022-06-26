package com.example.taslak1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter(var listem:ArrayList<String>) : RecyclerView.Adapter<RecyclerViewAdapter.AdapterVH>(){
    class AdapterVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_eleman,parent,false)
        return AdapterVH(itemView)
    }
    override fun onBindViewHolder(holder: AdapterVH, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.textRecyclerView).text = listem.get(position)
        holder.itemView.setOnClickListener {
            val action = BesinlerListesiFragmentDirections.actionBesinlerListesiFragmentToAnaMenuFragment(listem.get(position))
            Navigation.findNavController(it).navigate(action)
        }
    }
    override fun getItemCount(): Int {
        return listem.size
    }
}

