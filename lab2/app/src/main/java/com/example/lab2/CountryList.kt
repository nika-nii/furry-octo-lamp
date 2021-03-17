package com.example.lab2

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.picasso.Picasso
import okhttp3.*
import okio.IOException
import java.util.*

class CountryItem(val key: String, val value: String)

class CountryRecyclerAdapter(
    private val values: List<CountryItem>,
    private val onClickCallback: (String) -> Int
) :
    RecyclerView.Adapter<CountryRecyclerAdapter.CountryViewHolder>() {

    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.country_item, parent, false)
        return CountryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        val country: CountryItem = values[position]
        holder.countryName?.text = country.value
        val flagUrl: String = "https://www.countryflags.io/${country.key.toLowerCase(Locale.ROOT)}/flat/64.png"
        Picasso.get().load(flagUrl)
            .into(holder.countryFlag)
        holder.itemView.setOnClickListener {
            onClickCallback(country.key)
        }
    }

    class CountryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var countryName: TextView? = null
        var countryFlag: ImageView? = null

        init {
            countryName = itemView.findViewById(R.id.country_name_list)
            countryFlag = itemView.findViewById(R.id.country_flag_view_list)
        }
    }
}

class CountryList : Fragment() {

    companion object {
        fun newInstance(): CountryList {
            return CountryList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.country_list, container, false)
        val recyclerView = rootView.findViewById<View>(R.id.recycler_countries) as RecyclerView
        val listener = activity as Listener
        val activity = activity as Context
        recyclerView.layoutManager = LinearLayoutManager(activity)
        countryListAdapter = CountryRecyclerAdapter(countries, listener.onClickListener)
        recyclerView.adapter = countryListAdapter
        getCountryList()
        return rootView
    }

    private var countryListAdapter: CountryRecyclerAdapter? = null
    private val client = OkHttpClient()
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    private val countryItemListType =
        Types.newParameterizedType(List::class.java, CountryItem::class.java)
    private val countryItemListJsonAdapter: JsonAdapter<List<CountryItem>> =
        moshi.adapter(countryItemListType)

    private var countries: MutableList<CountryItem> = mutableListOf()

    private fun getCountryList() {
        val request = Request.Builder()
            .url("https://date.nager.at/Api/v2/AvailableCountries")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    Log.println(Log.DEBUG, "HTTP", "Success")

                    countries.clear()
                    countryItemListJsonAdapter.fromJson(it.body!!.source())?.forEach { country -> countries.add(country) }

                    Log.println(Log.DEBUG, "HTTP", countries.toString())

                    activity?.runOnUiThread{
                        countryListAdapter!!.notifyDataSetChanged()
                    }
                }
            }
        })
    }
}
