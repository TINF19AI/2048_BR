package com.dhbw.br2048.presentation

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

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        val s = scores[position]
        holder.b.tvPosition.text = s.position.toString()
        holder.b.tvName.text = s.username
        holder.b.tvScore.text = s.score.toString()
    }


    override fun getItemCount(): Int {
        return scores.size
    }
}