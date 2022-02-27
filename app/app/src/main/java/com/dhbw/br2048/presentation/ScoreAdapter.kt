package com.dhbw.br2048.presentation

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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

        if (s.alive) {
            when (s.position) {
                1 -> holder.b.tvPosition.text = "\uD83E\uDD47" // gold medal
                2 -> holder.b.tvPosition.text = "\uD83E\uDD48" // silver medal
                3 -> holder.b.tvPosition.text = "\uD83E\uDD49" // bronze medal
                else -> holder.b.tvPosition.text = s.position.toString()
            }
        } else {
            holder.b.tvPosition.text = "\uD83E\uDEA6" // tombstone
        }
    }

    override fun getItemCount(): Int {
        return scores.size
    }
}