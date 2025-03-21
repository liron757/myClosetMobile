package com.example.mycloset.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mycloset.R
import com.example.mycloset.api.models.post.Post
import com.squareup.picasso.Picasso

class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var outfitImg: ImageView = itemView.findViewById(R.id.ivOutfitImage)
    var username: TextView = itemView.findViewById(R.id.tvOutfitName)

    fun bind(post: Post, listener: UserRecyclerAdapter.OnItemClickListener) {
        if (post.outfitUrl.trim() != "") {
            Picasso.get().load(post.outfitUrl).into(outfitImg)
        }
        username.text = post.outfitName
        outfitImg.setOnClickListener {
            listener.onItemClick(
                adapterPosition
            )
        }
    }
}

class UserRecyclerAdapter(var data: List<Post>?, var inflater: LayoutInflater) :
    RecyclerView.Adapter<UserViewHolder>() {
    lateinit var listener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(adapterPosition: Int)
    }

    fun setItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = inflater.inflate(R.layout.user_post_list_row, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val post = data!![position]
        holder.bind(post, listener)
    }

    override fun getItemCount(): Int {
        if (data == null) return 0
        return data!!.size
    }
}
