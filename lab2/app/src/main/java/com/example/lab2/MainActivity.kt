package com.example.lab2

import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView

class Question(val question: String, val answers: List<String>, val avatar: Drawable)

class CustomRecyclerAdapter(private val values: List<Question>) :
    RecyclerView.Adapter<CustomRecyclerAdapter.MyViewHolder>() {

    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val question = values[position]
        holder.questionText?.text = question.question
        holder.firstAnswerText?.text = question.answers[0]
        holder.answersCountText?.text = question.answers.size.toString()
        holder.avatarView?.setImageDrawable(question.avatar)
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var questionText: TextView? = null
        var firstAnswerText: TextView? = null
        var answersCountText: TextView? = null
        var avatarView: ImageView? = null

        init {
            questionText = itemView.findViewById(R.id.questionText)
            firstAnswerText = itemView.findViewById(R.id.firstAnswerText)
            answersCountText = itemView.findViewById(R.id.answersCountText)
            avatarView = itemView.findViewById(R.id.avatarView)
        }
    }
}

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = CustomRecyclerAdapter(fillList())
    }

    private fun fillList(): List<Question> {
        val data = mutableListOf<Question>()
        val res: Resources = resources
        val questions = res.getStringArray(R.array.questions)
        val size = questions.size
        for (i in 0 until size){
            val answersName = "answer${(i+1).toString().padStart(2, '0')}"
            val avatarName = "avatar${(i+1).toString().padStart(2, '0')}"
            val answers: List<String> = res.getStringArray(
                            res.getIdentifier(answersName, "array", packageName)
            ).asList()
            data.add(
                    Question(
                            questions[i],
                            answers,
                            res.getDrawable(
                                    res.getIdentifier(avatarName, "drawable", packageName), theme
                            )
                    )
            )
        }
        return data
    }
}