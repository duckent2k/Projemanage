package com.example.projemanage.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.projemanage.R
import com.example.projemanage.models.Board
import de.hdodenhof.circleimageview.CircleImageView

open class BoardItemsAdapter(private val context: Context,
                             private val list: ArrayList<Board>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_board, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            Glide.with(context).load(model.image).centerCrop()
                .placeholder(R.drawable.ic_board_place_holder)
                .into(holder.itemView.findViewById<CircleImageView>(R.id.ivBoardImage))

            holder.itemView.findViewById<TextView>(R.id.tvName).text = model.name
            holder.itemView.findViewById<TextView>(R.id.tvCreateBy).text =
                "Create by: ${model.createBy}"

            holder.itemView.setOnClickListener {
                if (onClickListener != null){
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }


    interface OnClickListener {
        fun onClick(position: Int, model: Board)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener

    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

}