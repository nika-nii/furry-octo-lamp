package com.example.lab2

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

interface Listener {
    val onClickListener: (Int) -> Unit
}

class MainActivity : AppCompatActivity(R.layout.activity_main) , Listener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container_view, ListFragment.newInstance(), "questionsList")
                .commit()
        }
    }

    override val onClickListener = { questionId: Int ->
        val fragment: Fragment = DataFragment.newInstance(questionId.toString())
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container_view, fragment, "questionData")
            .addToBackStack(null)
            .commit()
        Unit
    }
}