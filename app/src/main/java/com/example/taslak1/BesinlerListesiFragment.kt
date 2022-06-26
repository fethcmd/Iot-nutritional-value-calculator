package com.example.taslak1
import BesinlerClass
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SearchView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class BesinlerListesiFragment : Fragment() {
    var listem = BesinlerClass().listeBesinString
    var gorunenListe=ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_besinler_listesi, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gorunenListe.addAll(listem)
        var searchView = view.findViewById<SearchView>(R.id.searchView)

        val layoutManager = LinearLayoutManager(this.context)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager=layoutManager
        val adapter = RecyclerViewAdapter(gorunenListe)
        recyclerView.adapter=adapter

        searchView.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isNotEmpty()){
                    gorunenListe.clear()
                    val search = newText.toLowerCase(Locale.getDefault())
                    listem.forEach {
                        if (it.toLowerCase(Locale.getDefault()).contains(search)){
                            gorunenListe.add(it)
                        }
                    }
                    adapter.notifyDataSetChanged()
                }else{
                    gorunenListe.clear()
                    gorunenListe.addAll(listem)
                    adapter.notifyDataSetChanged()
                }
                return false
            }
        })
    }
}
