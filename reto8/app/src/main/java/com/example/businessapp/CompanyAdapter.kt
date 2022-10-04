package com.example.businessapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CompanyAdapter(val mContext: Context, val companyList: List<Business>) :
    RecyclerView.Adapter<CompanyAdapter.CompanyHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CompanyHolder(layoutInflater.inflate(R.layout.company_item, parent, false))
    }

    override fun onBindViewHolder(holder: CompanyHolder, position: Int) {
        holder.render(companyList[position], mContext)
    }

    override fun getItemCount(): Int {
        return companyList.size
    }

    class CompanyHolder(val view: View) : RecyclerView.ViewHolder(view) {
        fun render(company: Business, mContext: Context) {
            val nameCompany: TextView = view.findViewById(R.id.textNameItem)
            val emailCompany: TextView = view.findViewById(R.id.textEmailItem)
            val btypeCompany: TextView = view.findViewById(R.id.textBTypeItem)
            val phoneCompany: TextView = view.findViewById(R.id.textPhoneItem)
            val urlCompany: TextView = view.findViewById(R.id.textURLItem)

            var editButton: ImageButton = view.findViewById(R.id.editButton)
            var deleteBurron: ImageButton = view.findViewById(R.id.deleteButton)

            nameCompany.text = company.name
            emailCompany.text = company.email
            btypeCompany.text = company.bType
            phoneCompany.text = company.telephone
            urlCompany.text = company.URL

            editButton.setOnClickListener {

                (mContext as MainActivity).editCompany(company)

            }

            deleteBurron.setOnClickListener {
                (mContext as MainActivity).deleteCompany(company)
            }


        }
    }


}