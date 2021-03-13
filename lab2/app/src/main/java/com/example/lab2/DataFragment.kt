package com.example.lab2

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text


private const val ARG_QUESTION_ID = "question_id"


class AnswerRecyclerAdapter(private val values: List<String>) :
    RecyclerView.Adapter<AnswerRecyclerAdapter.AnswerViewHolder>() {

    override fun getItemCount() = values.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.answer_item, parent, false)
        return AnswerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        val answer = values[position]
        holder.answerText?.text = "Ответ номер $position: $answer"
    }

    class AnswerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var answerText: TextView? = null

        init {
            answerText = itemView.findViewById(R.id.data_answer_text)
        }
    }
}




class DataFragment : Fragment() {
    private var questionId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            questionId = it.getString(ARG_QUESTION_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_data, container, false)
        val textView = rootView.findViewById<TextView>(R.id.data_fragment_text)
        val imageView = rootView.findViewById<ImageView>(R.id.data_avatar_view)
        val questionText = rootView.findViewById<TextView>(R.id.data_question_text)
        val id : Int = questionId?.toInt() ?: 0
        textView.text = "Question ID = $questionId"
        val avatarName = "avatar${(id+1).toString().padStart(2, '0')}"
        val answersName = "answer${(id + 1).toString().padStart(2, '0')}"
        val res: Resources = resources
        val drawable = res.getDrawable(
            res.getIdentifier(avatarName, "drawable", activity?.packageName), activity?.theme
        )
        imageView.setImageDrawable(drawable)
        val questions = res.getStringArray(R.array.questions)
        questionText.text = questions[id]
        val answers: List<String> = res.getStringArray(
            res.getIdentifier(answersName, "array", activity?.packageName)
        ).asList()
        val recyclerView = rootView.findViewById<View>(R.id.data_answers) as RecyclerView
        val activity = activity as Context
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = AnswerRecyclerAdapter(answers)
        return rootView
    }

    companion object {
        @JvmStatic
        fun newInstance(questionId: String) =
                DataFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_QUESTION_ID, questionId)
                    }
                }
    }
}