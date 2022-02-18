package com.dhbw.br2048.presentation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhbw.br2048.data.Lobby
import com.dhbw.br2048.databinding.ItemLobbyBinding

class LobbyAdapter(var lobbys: MutableList<Lobby>) : RecyclerView.Adapter<LobbyAdapter.LobbyViewHolder>(){
    inner class LobbyViewHolder(val b: ItemLobbyBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LobbyViewHolder {
        val b : ItemLobbyBinding = ItemLobbyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder : LobbyViewHolder = LobbyViewHolder(b)
        return holder
    }

    override fun onBindViewHolder(holder: LobbyViewHolder, position: Int) {
        val context = holder.itemView.context
        val l = lobbys[position]
        holder.b.lobbyID.text = l.id
        holder.b.lobbyOwner.text = l.owner
        @SuppressLint("SetTextI18n")
        holder.b.lobbyPlayerCount.text = "${l.currentUsers} / ${l.maxUsers}"
    }

    override fun getItemCount(): Int {
        return lobbys.size
    }
}