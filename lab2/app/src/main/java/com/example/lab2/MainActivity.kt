package com.example.lab2

import android.app.Fragment
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit


class MainActivity : AppCompatActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        supportFragmentManager.commit {
            setReorderingAllowed(false)
            add(R.id.fragment_container_view, DataFragment())
        }

    }

    override fun onAttachFragment(fragment: Fragment) {
        super.onAttachFragment(fragment)
        if (fragment.id == R.id.fragment_data) {
            fragment.arguments = intent.extras
        }
    }

}