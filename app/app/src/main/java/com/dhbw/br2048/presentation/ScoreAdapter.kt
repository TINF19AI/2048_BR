package com.dhbw.br2048.presentation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhbw.br2048.R
import com.dhbw.br2048.data.Score
import com.dhbw.br2048.databinding.ItemScoreBinding


class ScoreAdapter(private var scores: MutableList<Score>) :
    RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder>() {
    inner class ScoreViewHolder(val b: ItemScoreBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val b: ItemScoreBinding =
            ItemScoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ScoreViewHolder(b)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val s = scores[position]
        holder.b.tvName.text = s.username
        holder.b.tvScore.text = s.score.toString()

        when (s.position) {
            1 -> holder.b.tvPosition.text =
                holder.itemView.context.getString(R.string.emoji_medal_first)

            2 -> holder.b.tvPosition.text =
                holder.itemView.context.getString(R.string.emoji_medal_second)

            3 -> holder.b.tvPosition.text =
                holder.itemView.context.getString(R.string.emoji_medal_third)

            else -> holder.b.tvPosition.text = s.position.toString()
        }

        if (!s.alive)
            holder.b.tvPosition.text =
                holder.b.tvPosition.text.toString() + holder.itemView.context.getString(R.string.emoji_tombstone)
    }

    override fun getItemCount(): Int {
        return scores.size
    }
}