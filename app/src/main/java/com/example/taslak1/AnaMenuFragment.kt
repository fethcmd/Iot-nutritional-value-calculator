package com.example.taslak1
import BesinlerClass
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.round
class AnaMenuFragment : Fragment(),DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener {
    var gun=0
    var ay=0
    var yil=0
    var kayitligun=0
    var kayitliay=0
    var kayitliyil=0
    var gunlukKalori=0f
    var gunlukKarbonhidrat=0f
    var gunlukProtein=0f
    var gunlukYag=0f
    var besinidArrayList = ArrayList<String>()
    var listeElemaniNumarasi=0
    var secilenTarih=""
    var secilenBesin=""
    var hedefKalori="2200"
    var hedefKarbonhidrat="300"
    var hedefProtein="60"
    var hedefYag="70"
    var secilenBesinIndex:Int = 0
    var girilenGramDegeri = 0
    var internetGramDegeri = 0
    var tabakKutle = 0
    var besinBilgiGorunum=""
    var eklenenlerListesi=ArrayList<String>()
    private val tarihFormat= SimpleDateFormat("d.M.yyyy")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_ana_menu, container, false)
    }
    override fun onViewCreated( view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tabakKutleTextView=view.findViewById<TextView>(R.id.tabakKutleTextView)
        val tabloMevcutGram=view.findViewById<TextView>(R.id.tabloMevcutGram)
        val editText=view.findViewById<EditText>(R.id.editTextNumber)
        val besinGramDegeriAnaSayfa=view.findViewById<TextView>(R.id.besinGramDegeriAnaSayfa)
        val butonYemekSec=view.findViewById<Button>(R.id.buttonAna)
        val butonBesiniKaydet=view.findViewById<Button>(R.id.buttonBesiniKaydet)
        val butonHedefEkle=view.findViewById<Button>(R.id.buttonHedefEkle)
        val butonHedefKaydet=view.findViewById<Button>(R.id.buttonHedefKaydet)
        val butonListeyiTemizle=view.findViewById<Button>(R.id.buttonListeyiTemizle)
        val database = FirebaseDatabase.getInstance().reference
        val tarihTextView=view?.findViewById<TextView>(R.id.TarihTextView)
        val hedefEkleCardView = view?.findViewById<CardView>(R.id.HedefEkleCard)
        val editTextHedefKal=view.findViewById<EditText>(R.id.editTextHedefKal)
        val editTextHedefKarb=view.findViewById<EditText>(R.id.editTextHedefKarb)
        val editTextHedefPro=view.findViewById<EditText>(R.id.editTextHedefPro)
        val editTextHedefYag=view.findViewById<EditText>(R.id.editTextHedefYag)


        tarihTextView?.text=tarihFormat.format(Date())
        secilenTarih=tarihFormat.format(Date())
        eklenenlerListesi.clear()
        pickDate()
        sqlVeriCek()
        sqlHedefCek()


        val layoutManager = LinearLayoutManager(this.context)
        val recyclerView = view.findViewById<RecyclerView>(R.id.RecyclerViewIst)
        recyclerView.layoutManager=layoutManager
        val adapter = RecyclerViewAdapterIstatistik(eklenenlerListesi,besinidArrayList,eklenenlerListesi)
        recyclerView.adapter=adapter

        view.findViewById<Switch>(R.id.switch1).setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                tabakKutle=internetGramDegeri
                girilenGramDegeri=internetGramDegeri-tabakKutle
                besinGramDegeriAnaSayfa.text="${girilenGramDegeri} gram"
                tabloMevcutGram.text = "${girilenGramDegeri} Gram"
                tabakKutleTextView.text="Tabak: ${tabakKutle}g"
                besinDegeriAtaMevcutGram()
            }else{
                tabakKutle=0
                girilenGramDegeri=internetGramDegeri-tabakKutle
                besinGramDegeriAnaSayfa.text="${girilenGramDegeri} gram"
                tabloMevcutGram.text = "${girilenGramDegeri} Gram"
                tabakKutleTextView.text="Tabak Yok"
                besinDegeriAtaMevcutGram()
            }
        }
        val getdata= object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                val InternetVerisi = snapshot.child("iotGram").getValue()
                editText.setText(InternetVerisi.toString())
                internetGramDegeri=InternetVerisi.toString().toInt()
                girilenGramDegeri=internetGramDegeri-tabakKutle
                besinGramDegeriAnaSayfa.text="${girilenGramDegeri} gram"
                tabloMevcutGram.text = "${girilenGramDegeri} Gram"
                besinDegeriAtaMevcutGram()

            }
        }
        database.addValueEventListener(getdata)
        database.addListenerForSingleValueEvent(getdata)
        arguments?.let {
            secilenBesin= AnaMenuFragmentArgs.fromBundle(it).secilenBesinIsmi
        }
        if (secilenBesin!="Besin"){
            secilenBesinIndex=BesinlerClass().listeBesinString.indexOf(secilenBesin)
            besinDegeriAtaMevcutGram()
        }
        view.findViewById<TextView>(R.id.tabloBesinAdi).text = secilenBesin
        editText.setOnClickListener {
            girilenGramDegeri = (editText.text).toString().toInt()
            tabloMevcutGram.text = "${girilenGramDegeri} Gram"
            besinDegeriAtaMevcutGram()
        }
        butonYemekSec.setOnClickListener {
            val action = AnaMenuFragmentDirections.actionAnaMenuFragmentToBesinlerListesiFragment()
            Navigation.findNavController(it).navigate(action)
        }
        butonBesiniKaydet.setOnClickListener {
            if (secilenBesin!="Besin"){
                if (girilenGramDegeri>0){
                    sqlKaydet()
                    eklenenlerListesi.clear()
                    sqlVeriCek()
                    adapter.notifyDataSetChanged()
                    sqlHedefCek()
                    Toast.makeText(this.context,"${girilenGramDegeri} gram ${secilenBesin} listeye eklendi.",Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this.context,"Gram değeri 0'dan büyük olmalı!",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this.context,"Önce yiyecek seçmelisin!",Toast.LENGTH_SHORT).show()
            }
        }
        butonListeyiTemizle.setOnClickListener {
            if (eklenenlerListesi.size!=0){
                val uyari = AlertDialog.Builder(this.context)
                uyari.setTitle("UYARI")
                uyari.setMessage("Besin listen temizlensin mi?")
                uyari.setPositiveButton("Evet"){dialog, which ->
                    sqlVeriTemizle()
                    eklenenlerListesi.clear()
                    adapter.notifyDataSetChanged()
                    sqlHedefCek()
                }
                uyari.setNegativeButton("Hayır"){dialog, which ->

                }
                uyari.show()
            }else{
                Toast.makeText(this.context,"Listen zaten boş!",Toast.LENGTH_SHORT).show()
            }

        }
        butonHedefEkle.setOnClickListener {
            hedefEkleCardView.visibility=View.VISIBLE
            butonHedefKaydet.visibility=View.VISIBLE
        }
        butonHedefKaydet.setOnClickListener {
            if (editTextHedefKal.text.isNotEmpty()){
                hedefKalori=editTextHedefKal.text.toString()
                editTextHedefKal.text.clear()
            }
            if (editTextHedefKarb.text.isNotEmpty()){
                hedefKarbonhidrat=editTextHedefKarb.text.toString()
                editTextHedefKarb.text.clear()
            }
            if (editTextHedefPro.text.isNotEmpty()){
                hedefProtein=editTextHedefPro.text.toString()
                editTextHedefPro.text.clear()
            }
            if (editTextHedefYag.text.isNotEmpty()){
                hedefYag=editTextHedefYag.text.toString()
                editTextHedefYag.text.clear()
            }
            sqlHedefKaydet()
            sqlHedefCek()
            hedefEkleCardView.visibility=View.INVISIBLE
            butonHedefKaydet.visibility=View.INVISIBLE

        }
        besinDegeriAta100gram()

    }
    fun besinDegeriAta100gram(){
        if (secilenBesin!="Besin") {
            view?.findViewById<TextView>(R.id.tabloKarbonhidrat100gram)?.text = BesinlerClass().listeBesin[secilenBesinIndex].Karbonhidrat.toString()
            view?.findViewById<TextView>(R.id.tabloProtein100gram)?.text = BesinlerClass().listeBesin[secilenBesinIndex].Protein.toString()
            view?.findViewById<TextView>(R.id.tabloYag100gram)?.text = BesinlerClass().listeBesin[secilenBesinIndex].Yag.toString()
            view?.findViewById<TextView>(R.id.tabloLif100gram)?.text = BesinlerClass().listeBesin[secilenBesinIndex].Lif.toString()
            view?.findViewById<TextView>(R.id.tabloKolestrol100gram)?.text = BesinlerClass().listeBesin[secilenBesinIndex].Kolestrol.toString()
            view?.findViewById<TextView>(R.id.tabloSodyum100gram)?.text = BesinlerClass().listeBesin[secilenBesinIndex].Sodyum.toString()
            view?.findViewById<TextView>(R.id.tabloPotasyum100gram)?.text = BesinlerClass().listeBesin[secilenBesinIndex].Potasyum.toString()
            view?.findViewById<TextView>(R.id.tabloKalsiyum100gram)?.text = BesinlerClass().listeBesin[secilenBesinIndex].Kalsiyum.toString()
            view?.findViewById<TextView>(R.id.tabloVitaminA100gram)?.text = BesinlerClass().listeBesin[secilenBesinIndex].VitaminA.toString()
            view?.findViewById<TextView>(R.id.tabloVitaminC100gram)?.text = BesinlerClass().listeBesin[secilenBesinIndex].VitaminC.toString()
            view?.findViewById<TextView>(R.id.tabloDemir100gram)?.text = BesinlerClass().listeBesin[secilenBesinIndex].Demir.toString()
        }
    }
    fun besinDegeriAtaMevcutGram(){
        if (secilenBesin!="Besin") {
            view?.findViewById<TextView>(R.id.tabloKarbonhidratMevcutGram)?.text=(round(BesinlerClass().listeBesin[secilenBesinIndex].Karbonhidrat.div(100).times(girilenGramDegeri)*100) / 100).toString()
            view?.findViewById<TextView>(R.id.tabloProteinMevcutGram)?.text=(round(BesinlerClass().listeBesin[secilenBesinIndex].Protein.div(100).times(girilenGramDegeri)*100)/100).toString()
            view?.findViewById<TextView>(R.id.tabloYagMevcutGram)?.text=(round(BesinlerClass().listeBesin[secilenBesinIndex].Yag.div(100).times(girilenGramDegeri) * 100) / 100).toString()
            view?.findViewById<TextView>(R.id.tabloLifMevcutGram)?.text=(round(BesinlerClass().listeBesin[secilenBesinIndex].Lif.div(100).times(girilenGramDegeri) * 100) / 100).toString()
            view?.findViewById<TextView>(R.id.tabloKolestrolMevcutGram)?.text=(round(BesinlerClass().listeBesin[secilenBesinIndex].Kolestrol.div(100).times(girilenGramDegeri) * 100) / 100).toString()
            view?.findViewById<TextView>(R.id.tabloSodyumMevcutGram)?.text=(round(BesinlerClass().listeBesin[secilenBesinIndex].Sodyum.div(100).times(girilenGramDegeri) * 100) / 100).toString()
            view?.findViewById<TextView>(R.id.tabloPotasyumMevcutGram)?.text=(round(BesinlerClass().listeBesin[secilenBesinIndex].Potasyum.div(100).times(girilenGramDegeri) * 100) / 100).toString()
            view?.findViewById<TextView>(R.id.tabloKalsiyumMevcutGram)?.text=(round(BesinlerClass().listeBesin[secilenBesinIndex].Kalsiyum.div(100).times(girilenGramDegeri) * 100) / 100).toString()
            view?.findViewById<TextView>(R.id.tabloVitaminAMevcutGram)?.text=(round(BesinlerClass().listeBesin[secilenBesinIndex].VitaminA.div(100).times(girilenGramDegeri) * 100) / 100).toString()
            view?.findViewById<TextView>(R.id.tabloVitaminCMevcutGram)?.text=(round(BesinlerClass().listeBesin[secilenBesinIndex].VitaminC.div(100).times(girilenGramDegeri) * 100) / 100).toString()
            view?.findViewById<TextView>(R.id.tabloDemirMevcutGram)?.text=(round(BesinlerClass().listeBesin[secilenBesinIndex].Demir.div(100).times(girilenGramDegeri) * 100) / 100).toString()
            view?.findViewById<TextView>(R.id.kaloriTextView)?.text="${(round(BesinlerClass().listeBesin[secilenBesinIndex].Kalori.div(100).times(girilenGramDegeri) * 100) / 100).toString()} kcal"
        }
    }
    fun gunlukVerileriYaz(gunlukKalori:Float,gunlukKarbonhidrat:Float,gunlukProtein:Float,gunlukYag:Float){
        view?.findViewById<TextView>(R.id.gunlukKaloriTextView)?.text= "${gunlukKalori} kcal"
        view?.findViewById<TextView>(R.id.gunlukKarbonhidratTextView)?.text= "${gunlukKarbonhidrat} g"
        view?.findViewById<TextView>(R.id.gunlukProteinTextView)?.text= "${gunlukProtein} g"
        view?.findViewById<TextView>(R.id.gunlukYagTextView)?.text= "${gunlukYag} g"
    }
    fun sqlKaydet(){
        try {
            val myDatabase = this.context?.openOrCreateDatabase("TabloBesinKayit",Context.MODE_PRIVATE,null)
            myDatabase?.
            execSQL("CREATE TABLE IF NOT EXISTS tablobesinkayit(id INTEGER PRIMARY KEY,besinismi VARCHAR,besingram VARCHAR,besintarih VARCHAR)")
            val sqlString = "INSERT INTO tablobesinkayit(besinismi,besingram,besintarih) VALUES(?,?,?)"
            val statement = myDatabase?.compileStatement(sqlString)
            statement?.bindString(1,secilenBesin)
            statement?.bindString(2,girilenGramDegeri.toString())
            statement?.bindString(3,secilenTarih)
            statement?.execute()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
    fun sqlHedefKaydet(){
        try {
            val myDatabase = this.context?.openOrCreateDatabase("TabloHedefler",Context.MODE_PRIVATE,null)
            //myDatabase?.execSQL("DELETE FROM tablohedefler ")
            myDatabase?.execSQL("CREATE TABLE IF NOT EXISTS tablohedefler(kalorihedef VARCHAR,karbonhidrathedef VARCHAR, proteinhedef VARCHAR,yaghedef VARCHAR)")
            val sqlString = "INSERT INTO tablohedefler(kalorihedef,karbonhidrathedef,proteinhedef,yaghedef) VALUES(?,?,?,?)"
            val statement = myDatabase?.compileStatement(sqlString)
            statement?.bindString(1,hedefKalori)
            statement?.bindString(2,hedefKarbonhidrat)
            statement?.bindString(3,hedefProtein)
            statement?.bindString(4,hedefYag)
            statement?.execute()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
    fun sqlHedefCek(){
        try {
            val myDatabase = this.context?.openOrCreateDatabase("TabloHedefler",Context.MODE_PRIVATE,null)
            val cursor = myDatabase!!.rawQuery("SELECT * FROM tablohedefler",null)
            val hedefKaloriIx=cursor!!.getColumnIndex("kalorihedef")
            val hedefKarbonhidratIx=cursor!!.getColumnIndex("karbonhidrathedef")
            val hedefProteinIx=cursor!!.getColumnIndex("proteinhedef")
            val hedefYagIx=cursor!!.getColumnIndex("yaghedef")
            while (cursor!!.moveToNext()){
                hedefKalori=cursor.getString(hedefKaloriIx)
                hedefKarbonhidrat=cursor.getString(hedefKarbonhidratIx)
                hedefProtein=cursor.getString(hedefProteinIx)
                hedefYag=cursor.getString(hedefYagIx)
            }

            cursor.close()
        }catch (e: Exception) {
            e.printStackTrace()
        }
        view?.findViewById<TextView>(R.id.hedefKalori)?.text="${hedefKalori} kcal"
        view?.findViewById<TextView>(R.id.hedefKarbonhidrat)?.text="${hedefKarbonhidrat} g"
        view?.findViewById<TextView>(R.id.hedefProtein)?.text="${hedefProtein} g"
        view?.findViewById<TextView>(R.id.hedefYag)?.text="${hedefYag} g"

        view?.findViewById<TextView>(R.id.hedefKaloriYuzde)?.text="%${((gunlukKalori / hedefKalori.toFloat())*100).toInt()}"
        view?.findViewById<TextView>(R.id.hedefKarbonhidratYuzde)?.text="%${((gunlukKarbonhidrat / hedefKarbonhidrat.toFloat())*100).toInt()}"
        view?.findViewById<TextView>(R.id.hedefProteinYuzde)?.text="%${((gunlukProtein / hedefProtein.toFloat())*100).toInt()}"
        view?.findViewById<TextView>(R.id.hedefYagYuzde)?.text="%${((gunlukYag / hedefYag.toFloat())*100).toInt()}"

        if(((gunlukKalori / hedefKalori.toFloat())*100).toInt()>=100){
            view?.findViewById<TextView>(R.id.hedefKaloriYuzde)?.setTextColor(Color.GREEN)
        }else{
            view?.findViewById<TextView>(R.id.hedefKaloriYuzde)?.setTextColor(Color.GRAY)
        }
        if(((gunlukKarbonhidrat / hedefKarbonhidrat.toFloat())*100).toInt()>=100){
            view?.findViewById<TextView>(R.id.hedefKarbonhidratYuzde)?.setTextColor(Color.GREEN)
        }else{
            view?.findViewById<TextView>(R.id.hedefKarbonhidratYuzde)?.setTextColor(Color.GRAY)
        }
        if(((gunlukProtein / hedefProtein.toFloat())*100).toInt()>=100){
            view?.findViewById<TextView>(R.id.hedefProteinYuzde)?.setTextColor(Color.GREEN)
        }else{
            view?.findViewById<TextView>(R.id.hedefProteinYuzde)?.setTextColor(Color.GRAY)
        }
        if(((gunlukYag / hedefYag.toFloat())*100).toInt()>=100){
            view?.findViewById<TextView>(R.id.hedefYagYuzde)?.setTextColor(Color.GREEN)
        }else{
            view?.findViewById<TextView>(R.id.hedefYagYuzde)?.setTextColor(Color.GRAY)
        }



    }



    fun sqlVeriCek(){
        try {

            val myDatabase = this.context?.openOrCreateDatabase("TabloBesinKayit",Context.MODE_PRIVATE,null)
            val cursor = myDatabase!!.rawQuery("SELECT * FROM tablobesinkayit WHERE besintarih=?", arrayOf(secilenTarih))
            val besinismiIx=cursor!!.getColumnIndex("besinismi")
            val besingramIx=cursor!!.getColumnIndex("besingram")
            val idIx=cursor!!.getColumnIndex("id")

            gunlukKalori=0f
            gunlukKarbonhidrat=0f
            gunlukProtein=0f
            gunlukYag=0f

            besinidArrayList.clear()
            while (cursor!!.moveToNext()){
                val besinismi=cursor.getString(besinismiIx)
                val besingram=cursor.getString(besingramIx)
                val besinid=cursor.getString(idIx)

                besinidArrayList.add(besinid)
                listeElemaniNumarasi++
                besinBilgiGorunum=listeElemaniNumarasi.toString()+"."+besinismi+" "+besingram+"g"
                eklenenlerListesi.add(besinBilgiGorunum)

                gunlukKalori += round(BesinlerClass().listeBesin[BesinlerClass().listeBesinString.indexOf(besinismi)].Kalori.div(100).times(besingram.toIntOrNull()!!)*100)/100
                gunlukKarbonhidrat += round(BesinlerClass().listeBesin[BesinlerClass().listeBesinString.indexOf(besinismi)].Karbonhidrat.div(100).times(besingram.toIntOrNull()!!)*100)/100
                gunlukProtein += round(BesinlerClass().listeBesin[BesinlerClass().listeBesinString.indexOf(besinismi)].Protein.div(100).times(besingram.toIntOrNull()!!)*100)/100
                gunlukYag += round(BesinlerClass().listeBesin[BesinlerClass().listeBesinString.indexOf(besinismi)].Yag.div(100).times(besingram.toIntOrNull()!!)*100)/100
            }
            listeElemaniNumarasi=0
            gunlukKalori=round(gunlukKalori)
            gunlukKarbonhidrat=round(gunlukKarbonhidrat)
            gunlukProtein=round(gunlukProtein)
            gunlukYag=round(gunlukYag)
            gunlukVerileriYaz(gunlukKalori,gunlukKarbonhidrat,gunlukProtein,gunlukYag)
            cursor.close()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }
    fun sqlVeriTemizle(){
        val myDatabase = this.context?.openOrCreateDatabase("TabloBesinKayit",Context.MODE_PRIVATE,null)
        myDatabase?.execSQL("CREATE TABLE IF NOT EXISTS tablobesinkayit(besinismi VARCHAR,besingram VARCHAR)")
        myDatabase?.execSQL("DELETE FROM tablobesinkayit WHERE besintarih=?",arrayOf(secilenTarih))
        gunlukKalori=0f
        gunlukKarbonhidrat=0f
        gunlukProtein=0f
        gunlukYag=0f
        gunlukVerileriYaz(gunlukKalori,gunlukKarbonhidrat,gunlukProtein,gunlukYag)
    }
    private fun getDataTimeCalendar(){
        val cal:Calendar= Calendar.getInstance()
        gun=cal.get(Calendar.DAY_OF_MONTH)
        ay=cal.get(Calendar.MONTH)
        yil=cal.get(Calendar.YEAR)
    }
    private fun pickDate(){
        view?.findViewById<Button>(R.id.buttonTarihSec)?.setOnClickListener {
            getDataTimeCalendar()
            DatePickerDialog(this.requireContext(),this,yil,ay,gun).show()
        }
    }
    fun tarihiYaz(){
        view?.findViewById<TextView>(R.id.TarihTextView)?.text=secilenTarih

    }
    fun adapterDataYenile(){
        view?.findViewById<RecyclerView>(R.id.RecyclerViewIst)?.adapter?.notifyDataSetChanged()
    }
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        kayitligun=dayOfMonth
        kayitliay=month+1
        kayitliyil=year
        getDataTimeCalendar()
        secilenTarih="${kayitligun}.${kayitliay}.${kayitliyil}"
        tarihiYaz()
        eklenenlerListesi.clear()
        sqlVeriCek()
        adapterDataYenile()
        sqlHedefCek()
    }
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
    }
}
