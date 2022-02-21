package com.dhbw.br2048.presentation

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhbw.br2048.data.User
import com.dhbw.br2048.databinding.ItemUserBinding

class UserAdapter(var users: MutableList<User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
    inner class UserViewHolder(val b: ItemUserBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val b: ItemUserBinding =
            ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(b)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val u = users[position]
        holder.b.tvUsername.text = u.username
    }

    override fun getItemCount(): Int {
        return users.size
    }
}