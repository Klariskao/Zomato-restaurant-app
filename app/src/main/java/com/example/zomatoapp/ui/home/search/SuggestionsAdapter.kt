package com.example.zomatoapp.ui.home.search

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.beust.klaxon.JsonObject
import com.beust.klaxon.lookup
import com.example.zomatoapp.R
import com.example.zomatoapp.databinding.SuggestionViewBinding

/* Adapter for location suggestions */

class SuggestionsAdapter(private val suggestionsList: JsonObject,
                         private val suggestionClickListener: SuggestionClickListener,
                         private val context: Context): RecyclerView.Adapter<SuggestionsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SuggestionViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = if (suggestionsList.lookup<Int>("totalResultsCount")[0] < 5)
        suggestionsList.lookup<Int>("totalResultsCount")[0]
        else 5

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position, suggestionsList, suggestionClickListener, context)
    }

    class ViewHolder(private val binding: SuggestionViewBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int, suggestionsList: JsonObject, suggestionClickListener: SuggestionClickListener, context: Context) {
            binding.suggestionTextView.text = context.resources.getString(R.string.three_strings,
                suggestionsList.lookup<String>("geonames.toponymName")[position],
                suggestionsList.lookup<String>("geonames.adminName1")[position],
                suggestionsList.lookup<String>("geonames.countryName")[position]
            )

            binding.suggestionCardView.setOnClickListener {
                suggestionClickListener.onSuggestionClickClickAction(position)
            }
        }
    }
}