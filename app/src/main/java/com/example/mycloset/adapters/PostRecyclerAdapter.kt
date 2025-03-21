package com.example.mycloset.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mycloset.R
import com.example.mycloset.api.controllers.UserController
import com.example.mycloset.api.models.post.Post
import com.example.mycloset.api.models.user.User
import com.squareup.picasso.Picasso

class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val item = itemView.findViewById<View>(R.id.post_list_row)
    var userNameTv: TextView = itemView.findViewById(R.id.userName_plr)
    var outfitNameTv: TextView = itemView.findViewById(R.id.outfit_name_plr)
    var outfitImg: ImageView = itemView.findViewById(R.id.outfit_img_plr)
    var likeBtn: ImageButton = itemView.findViewById(R.id.btnPostLike)

    fun bind(
        user: User, post: Post, onImageClickListener: PostRecyclerAdapter.OnItemClickListener?,
        onUsernameClickListener: PostRecyclerAdapter.OnItemClickListener?
    ) {
        userNameTv.text = post.userName

        outfitNameTv.text = post.outfitName

        if (post.outfitUrl != "") {
            Picasso.get().load(post.outfitUrl).placeholder(R.drawable.outfit_blank)
                .into(outfitImg)
        } else {
            outfitImg.setImageResource(R.drawable.outfit_blank)
        }

        if (user.likedPosts.contains(post.id)) {
            likeBtn.setImageResource(R.drawable.heart)
        }

        likeBtn.setOnClickListener {
            val likedPostsList: MutableList<String> =
                ArrayList(user.likedPosts)
            if (!user.likedPosts.contains(post.id)) {
                likedPostsList.add(post.id)
                user.likedPosts = likedPostsList
                UserController.instance.updateLikedPosts(user)
                likeBtn.setImageResource(R.drawable.heart)
            } else {
                likedPostsList.remove(post.id)
                user.likedPosts = likedPostsList
                UserController.instance.updateLikedPosts(user)
                likeBtn.setImageResource(R.drawable.heart_outline)
            }
        }

        item.setOnClickListener {
            onImageClickListener?.onItemClick(
                adapterPosition
            )
        }

        userNameTv.setOnClickListener {
            onUsernameClickListener?.onItemClick(
                adapterPosition
            )
        }
    }
}

class PostRecyclerAdapter(
    user: User?,
    var inflater: LayoutInflater,
    var posts: List<Post>,
    var context: Context?
) : RecyclerView.Adapter<PostViewHolder>() {
    var user: User = user!!
    var listener: OnItemClickListener? = null
    var onImageClickListener: OnItemClickListener? = null
    var onUsernameClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(pos: Int)
    }

    fun updatePosts(data: List<Post>) {
        this.posts = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = inflater.inflate(R.layout.post_list_row, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.bind(user, post, onImageClickListener, onUsernameClickListener)
    }

    override fun getItemCount(): Int {
        return posts.size
    }
}

