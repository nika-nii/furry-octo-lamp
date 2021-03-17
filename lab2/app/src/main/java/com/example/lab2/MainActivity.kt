package com.example.lab2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

interface Listener {
    val onClickListener: (String) -> Int
}

class MainActivity : AppCompatActivity(R.layout.activity_main) , Listener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container_view, CountryList.newInstance(), "countryList")
                .commit()
        }
    }

    override val onClickListener = { countryAlpha: String ->
        val fragment: Fragment = Country.newInstance(countryAlpha)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container_view, fragment, "countryData")
            .addToBackStack(null)
            .commit()
    }
}