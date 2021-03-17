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


private const val ARG_COUNTRY_ALPHA = "countryAlpha"

class HolidayItem(
    val date: String,
    val localName: String,
    val name: String,
    val countryCode: String,
    val fixed: Boolean,
    val global: Boolean,
    val counties: List<String>?,
    val launchYear: Int?,
    val type: String
)

class CountryInfo(
    val commonName: String,
    val officialName: String,
    val countryCode: String,
    val region: String,
    val borders: List<CountryInfo>?
)

class HolidaysRecyclerAdapter(private val values: List<HolidayItem>) :
    RecyclerView.Adapter<HolidaysRecyclerAdapter.HolidayViewHolder>() {

    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolidayViewHolder {
        val rootView =
            LayoutInflater.from(parent.context).inflate(R.layout.holiday_item, parent, false)
        return HolidayViewHolder(rootView)
    }

    override fun onBindViewHolder(holder: HolidayViewHolder, position: Int) {
        val holiday = values[position]
        holder.holidayNameText?.text = holiday.name
        holder.localNameText?.text = holiday.localName
        holder.holidayDateText?.text = holiday.date
    }

    class HolidayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var holidayNameText: TextView? = null
        var localNameText: TextView? = null
        var holidayDateText: TextView? = null

        init {
            holidayNameText = itemView.findViewById(R.id.holiday_name)
            localNameText = itemView.findViewById(R.id.holiday_local_name)
            holidayDateText = itemView.findViewById(R.id.holiday_date)
        }
    }
}


class Country : Fragment() {
    private var countryAlpha: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            countryAlpha = it.getString(ARG_COUNTRY_ALPHA)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.country, container, false)

        getCountryInfo()
        getHolidayList()

        val recyclerView = rootView.findViewById<View>(R.id.recycler_holidays) as RecyclerView
        val activity = activity as Context
        recyclerView.layoutManager = LinearLayoutManager(activity)
        holidaysListAdapter = HolidaysRecyclerAdapter(holidays)
        recyclerView.adapter = holidaysListAdapter
        return rootView
    }


    private var holidaysListAdapter: HolidaysRecyclerAdapter? = null
    private val client = OkHttpClient()
    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()
    private val holidayItemListType =
        Types.newParameterizedType(List::class.java, HolidayItem::class.java)
    private val holidayItemListJsonAdapter: JsonAdapter<List<HolidayItem>> =
        moshi.adapter(holidayItemListType)

    private var holidays: MutableList<HolidayItem> = mutableListOf()

    private fun getHolidayList() {
        val year = Date().year.toString()
        val request = Request.Builder()
            .url("https://date.nager.at/Api/v2/PublicHolidays/$year/$countryAlpha")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    Log.println(Log.DEBUG, "HTTP", "Holidays success")

                    holidays.clear()
                    holidayItemListJsonAdapter.fromJson(it.body!!.source())?.forEach { holiday -> holidays.add(holiday) }

                    Log.println(Log.DEBUG, "HTTP", holidays.toString())

                    activity?.runOnUiThread{
                        holidaysListAdapter!!.notifyDataSetChanged()
                    }
                }
            }
        })
    }

    private val countryInfoJsonAdapter = moshi.adapter(CountryInfo::class.java)

    private fun getCountryInfo() {
        val year = Date().year.toString()
        val request = Request.Builder()
            .url("https://date.nager.at/Api/v2/CountryInfo?countryCode=$countryAlpha")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    Log.println(Log.DEBUG, "HTTP", "Country info")

                    val info: CountryInfo = countryInfoJsonAdapter.fromJson(it.body!!.source())!!

                    Log.println(Log.DEBUG, "HTTP", info.toString())

                    activity?.runOnUiThread{
                        val countryNameText: TextView = view?.findViewById(R.id.country_name)!!
                        val countryOfficialNameText: TextView = view?.findViewById(R.id.country_official)!!
                        val countryRegion: TextView = view?.findViewById(R.id.country_region)!!
                        val countryFlag: ImageView = view?.findViewById(R.id.country_flag_view)!!
                        countryNameText.text = info.commonName
                        countryOfficialNameText.text = info.officialName
                        countryRegion.text = info.region

                        val flagUrl: String = "https://www.countryflags.io/${info.countryCode.toLowerCase(Locale.ROOT)}/flat/64.png"
                        Picasso.get().load(flagUrl)
                            .into(countryFlag)
                    }
                }
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(countryAlpha: String) =
            Country().apply {
                arguments = Bundle().apply {
                    putString(ARG_COUNTRY_ALPHA, countryAlpha)
                }
            }
    }
}