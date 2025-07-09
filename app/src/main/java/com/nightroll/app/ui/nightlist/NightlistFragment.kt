package com.nightroll.app.ui.nightlist

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.nightroll.app.databinding.FragmentNightlistBinding
import com.nightroll.app.ui.adapters.BarCardAdapter
import com.nightroll.app.ui.adapters.HangoutListAdapter
import com.nightroll.app.ui.adapters.LocationHistoryAdapter
import com.nightroll.app.ui.bar.BarDetailsActivity

class NightlistFragment : Fragment() {

    private var _binding: FragmentNightlistBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var nightlistViewModel: NightlistViewModel
    private lateinit var hangoutListAdapter: HangoutListAdapter
    private lateinit var locationHistoryAdapter: LocationHistoryAdapter
    private lateinit var favouriteBarsAdapter: BarCardAdapter
    private lateinit var starredBarsAdapter: BarCardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        nightlistViewModel = ViewModelProvider(this)[NightlistViewModel::class.java]
        _binding = FragmentNightlistBinding.inflate(inflater, container, false)
        
        setupViews()
        setupObservers()
        
        return binding.root
    }
    
    private fun setupViews() {
        // Setup hangout lists
        hangoutListAdapter = HangoutListAdapter { listType ->
            nightlistViewModel.navigateToHangoutList(listType)
            showHangoutListDetails(listType)
        }
        binding.recyclerHangoutLists.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = hangoutListAdapter
        }
        
        // Setup location history
        locationHistoryAdapter = LocationHistoryAdapter { history ->
            nightlistViewModel.navigateToBar(history.barId)
        }
        binding.recyclerLocationHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = locationHistoryAdapter
        }
        
        // Setup favourite bars
        favouriteBarsAdapter = BarCardAdapter { bar ->
            val intent = Intent(requireContext(), BarDetailsActivity::class.java)
            intent.putExtra("bar", bar)
            startActivity(intent)
        }
        binding.recyclerFavouriteBars.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = favouriteBarsAdapter
        }
        
        // Setup starred bars
        starredBarsAdapter = BarCardAdapter { bar ->
            val intent = Intent(requireContext(), BarDetailsActivity::class.java)
            intent.putExtra("bar", bar)
            startActivity(intent)
        }
        binding.recyclerStarredBars.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = starredBarsAdapter
        }
        
        // Setup filter buttons
        binding.apply {
            btnYesterday.setOnClickListener { 
                nightlistViewModel.filterHistory("yesterday")
                updateFilterButtons("yesterday")
            }
            btnPlaces.setOnClickListener { 
                nightlistViewModel.filterHistory("places")
                updateFilterButtons("places")
            }
            btnJuly.setOnClickListener { 
                nightlistViewModel.filterHistory("month")
                updateFilterButtons("month")
            }
            btnCities.setOnClickListener { 
                nightlistViewModel.filterHistory("cities")
                updateFilterButtons("cities")
            }
        }
        
        // Setup section toggles
        binding.btnToggleFavourites.setOnClickListener {
            toggleSection(binding.sectionFavourites)
        }
        
        binding.btnToggleStarred.setOnClickListener {
            toggleSection(binding.sectionStarred)
        }
        
        // Setup add buttons
        binding.btnAddToFavourites.setOnClickListener {
            nightlistViewModel.showAddToListDialog("Favourites")
        }
        
        binding.btnAddToStarred.setOnClickListener {
            nightlistViewModel.showAddToListDialog("Starred Places")
        }
        
        // Setup stats
        updateStats()
    }
    
    private fun setupObservers() {
        nightlistViewModel.hangoutLists.observe(viewLifecycleOwner) { lists ->
            hangoutListAdapter.submitList(lists)
        }
        
        nightlistViewModel.locationHistory.observe(viewLifecycleOwner) { history ->
            locationHistoryAdapter.submitList(history)
        }
        
        nightlistViewModel.favouriteBars.observe(viewLifecycleOwner) { bars ->
            favouriteBarsAdapter.submitList(bars)
            binding.textFavouritesCount.text = "${bars.size} places"
        }
        
        nightlistViewModel.starredBars.observe(viewLifecycleOwner) { bars ->
            starredBarsAdapter.submitList(bars)
            binding.textStarredCount.text = "${bars.size} places"
        }
        
        nightlistViewModel.filteredHistory.observe(viewLifecycleOwner) { history ->
            locationHistoryAdapter.submitList(history)
        }
    }
    
    private fun showHangoutListDetails(listType: String) {
        when (listType) {
            "Favourites" -> {
                binding.sectionFavourites.visibility = 
                    if (binding.sectionFavourites.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }
            "Starred Places" -> {
                binding.sectionStarred.visibility = 
                    if (binding.sectionStarred.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }
            "Next Outings" -> {
                // Show next outings section
                nightlistViewModel.loadNextOutings()
            }
            "Labelled" -> {
                // Show labelled places
                nightlistViewModel.loadLabelledPlaces()
            }
        }
    }
    
    private fun toggleSection(section: View) {
        section.visibility = if (section.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }
    
    private fun updateFilterButtons(selected: String) {
        val primaryColor = resources.getColor(com.nightroll.app.R.color.button_primary_background, null)
        val transparentColor = resources.getColor(android.R.color.transparent, null)
        val whiteColor = resources.getColor(android.R.color.white, null)
        val blackColor = resources.getColor(android.R.color.black, null)
        
        // Reset all buttons
        listOf(binding.btnYesterday, binding.btnPlaces, binding.btnJuly, binding.btnCities).forEach { button ->
            button.setBackgroundColor(transparentColor)
            button.setTextColor(blackColor)
        }
        
        // Highlight selected button
        when (selected) {
            "yesterday" -> {
                binding.btnYesterday.setBackgroundColor(primaryColor)
                binding.btnYesterday.setTextColor(whiteColor)
            }
            "places" -> {
                binding.btnPlaces.setBackgroundColor(primaryColor)
                binding.btnPlaces.setTextColor(whiteColor)
            }
            "month" -> {
                binding.btnJuly.setBackgroundColor(primaryColor)
                binding.btnJuly.setTextColor(whiteColor)
            }
            "cities" -> {
                binding.btnCities.setBackgroundColor(primaryColor)
                binding.btnCities.setTextColor(whiteColor)
            }
        }
    }
    
    private fun updateStats() {
        // Mock stats data
        binding.apply {
            textTotalVisits.text = "47 visits this month"
            textFavoriteSpot.text = "Most visited: The Rooftop Lounge"
            textNewDiscoveries.text = "3 new places discovered"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}