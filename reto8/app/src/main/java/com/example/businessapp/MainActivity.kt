package com.example.businessapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Database
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var rvCompaniesList: RecyclerView

    private lateinit var textOption: TextView

    private lateinit var textName: TextView
    private lateinit var textURL: TextView
    private lateinit var textPhone: TextView
    private lateinit var textEmail: TextView
    private lateinit var textProdServ: TextView

    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button

    private lateinit var spinner: Spinner
    private var listBType = listOf("-","Consultancy", "Development")

    private var idCompany: Int? = null


    private var listCompanies = emptyList<Business>()

    private lateinit var dataBase: AppDataBase



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Get database
        dataBase = AppDataBase.getDataBase(this)

        //Get objects from layout
        rvCompaniesList = findViewById(R.id.recycleCompany)

        textOption = findViewById(R.id.textOption)
        textName = findViewById(R.id.textName)
        textURL = findViewById(R.id.textURL)
        textPhone = findViewById(R.id.textPhone)
        textEmail = findViewById(R.id.textEmail)
        textProdServ = findViewById(R.id.textProdServ)

        saveButton = findViewById(R.id.buttonSave)
        cancelButton = findViewById(R.id.buttonCancel)

        spinner = findViewById(R.id.spinnerBtype)

        val adapterSpinner = ArrayAdapter(this, R.layout.my_selected_item, listBType)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapterSpinner


        //Create linear layout
        rvCompaniesList.layoutManager = LinearLayoutManager(this)

        dataBase.business().getAll().observe(this, Observer {
            listCompanies = it

            val adapter = CompanyAdapter(this, listCompanies)
            rvCompaniesList.adapter = adapter

        })

        saveButton.setOnClickListener {
            val name = textName.text.toString()
            val URL = textURL.text.toString()
            val phone = textPhone.text.toString()
            val email = textEmail.text.toString()
            val prodServ = textProdServ.text.toString()
            val bType = spinner.selectedItem.toString()

            if (name != "" && URL != "" && phone != "" && email != "" && prodServ != "" && bType != "-") {
                val business = Business(name, URL, phone, email, prodServ, bType)
                if (idCompany != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        business.idBusiness = idCompany as Int
                        dataBase.business().updateBusiness(business)
                        idCompany = null
                    }
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        dataBase.business().insertAll(business)
                    }
                }
                emptyTextViews()
            }
        }

        cancelButton.setOnClickListener {
            emptyTextViews()

        }
    }


    fun deleteCompany(company: Business) {

        val wrapper =
            ContextThemeWrapper(this, com.google.android.material.R.style.AlertDialog_AppCompat)
        //Create dialog
        val builder = AlertDialog.Builder(wrapper)

        //Title
        val textView = TextView(this)
        textView.text = "Delete the company"
        textView.setPadding(0, 60, 0, 20)
        textView.textSize = 20f
        textView.gravity = Gravity.CENTER_HORIZONTAL

        builder.setCustomTitle(textView)
        builder.setMessage("Do you want to delete the company?")
        builder.setPositiveButton("Yes") { _, _ ->

            CoroutineScope(Dispatchers.IO).launch {
                CoroutineScope(Dispatchers.IO).launch {
                    dataBase.business().deleteBusiness(company)
                    idCompany = null
                }
            }
            emptyTextViews()
        }
        builder.setNegativeButton("No") { _, _ ->
        }

        val mDialog = builder.create()
        mDialog.show()


    }

    fun editCompany(company: Business) {
        textOption.text = getString(R.string.editCompany)
        //Update textviews
        textName.text = company.name
        textURL.text = company.URL
        textPhone.text = company.telephone
        textEmail.text = company.email
        textProdServ.text = company.prodServ

        spinner.setSelection(listBType.indexOf(company.bType))

        //Id company
        idCompany = company.idBusiness

    }

    fun emptyTextViews() {
        //Empty the textViews
        textName.text = ""
        textURL.text = ""
        textPhone.text = ""
        textEmail.text = ""
        textProdServ.text = ""

        spinner.setSelection(0)

        textOption.text = getString(R.string.addCompany)
    }
}