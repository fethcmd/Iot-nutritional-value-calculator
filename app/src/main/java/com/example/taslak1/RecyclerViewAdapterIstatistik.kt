package com.example.taslak1

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapterIstatistik (var Istlistem:ArrayList<String>,var besinidArrayList:ArrayList<String>,var eklenenlerListesi:ArrayList<String>) : RecyclerView.Adapter<RecyclerViewAdapterIstatistik.AdapterVHist>(){
    class AdapterVHist(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterVHist {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_ist_eleman,parent,false)
        return AdapterVHist(itemView)
    }
    override fun onBindViewHolder(holder: AdapterVHist, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.IstRecyclerTextView).text = Istlistem.get(position)
        holder.itemView.setOnLongClickListener {
            val uyari = AlertDialog.Builder(holder.itemView.context)
            uyari.setTitle("UYARI")
            uyari.setMessage("${Istlistem.get(position)} listeden silinsin mi?")
            uyari.setPositiveButton("Evet"){dialog, which ->
                val myDatabase = holder.itemView.context.openOrCreateDatabase("TabloBesinKayit", Context.MODE_PRIVATE,null)
                myDatabase?.execSQL("CREATE TABLE IF NOT EXISTS tablobesinkayit(id INTEGER PRIMARY KEY,besinismi VARCHAR,besingram VARCHAR,besintarih VARCHAR)")
                myDatabase?.execSQL("DELETE FROM tablobesinkayit WHERE id=?",arrayOf(besinidArrayList[position]))
                eklenenlerListesi.remove(eklenenlerListesi[position])
                notifyDataSetChanged()
            }
            uyari.setNegativeButton("Hayır"){dialog, which ->

            }
            uyari.show()

        true
        }
    }
    override fun getItemCount(): Int {
        return Istlistem.size
    }
}
