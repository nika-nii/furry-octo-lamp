package com.example.lab2

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.Drawable
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

class Question(val question: String, val answers: List<String>, val avatar: Drawable)

class CustomRecyclerAdapter(private val values: List<Question>, private val onClickCallback: (Int) -> Unit ) :
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
        holder.itemView.setOnClickListener{
            onClickCallback(position)
        }
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

class ListFragment : Fragment() {

    companion object {

        fun newInstance(): ListFragment {
            return ListFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_list, container, false)
        val recyclerView = rootView.findViewById<View>(R.id.recyclerView) as RecyclerView
        val activity = activity as Context
        val listener = activity as Listener
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = CustomRecyclerAdapter(fillList(), listener.onClickListener)
        return rootView
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context != null) {
            val resources = context.resources
            val question = resources.getStringArray(R.array.questions)[0]
        }
    }

    private fun fillList(): List<Question> {
        val data = mutableListOf<Question>()
        val res: Resources = resources
        val questions = res.getStringArray(R.array.questions)
        val size = questions.size
        Log.println(Log.DEBUG, "huy", "Filling list")
        for (i in 0 until size) {
            val answersName = "answer${(i + 1).toString().padStart(2, '0')}"
            val avatarName = "avatar${(i + 1).toString().padStart(2, '0')}"
            val answers: List<String> = res.getStringArray(
                    res.getIdentifier(answersName, "array", activity?.packageName)
            ).asList()
            data.add(
                    Question(
                            questions[i],
                            answers,
                            res.getDrawable(
                                    res.getIdentifier(avatarName, "drawable", activity?.packageName), activity?.theme
                            )
                    )
            )
        }
        return data
    }
}
