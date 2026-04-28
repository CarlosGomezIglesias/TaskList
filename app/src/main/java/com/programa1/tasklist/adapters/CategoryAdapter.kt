package com.programa1.tasklist.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


    class CategoryAdapter(var items: List<Game>, val onClick: (Int) -> Unit) : RecyclerView.Adapter<GameViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemGameBinding.inflate(layoutInflater, parent, false)
            return GameViewHolder(binding)
        }

        override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
            val game = items[position]
            holder.render(game)
            holder.itemView. {
                onClick(position)
            }
        }

        override fun getItemCount(): Int = items.size

        fun updateData(dataSet: List<Game>){
            items = dataSet
            notifyDataSetChanged()
        }
    }

    class CategoryViewHolder(val binding: ItemGameBinding): RecyclerView.ViewHolder(binding.root){
        fun render(game: Game) {
            binding.titleTextView.text = game.title
            binding.genreChip.text = game.genre
            binding.shortDescription.text = game.shortDescription
            Picasso.get().load(game.image).into(binding.thumbnailImageView)
        }

    }
