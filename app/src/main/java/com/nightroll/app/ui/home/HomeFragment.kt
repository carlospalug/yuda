package com.nightroll.app.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nightroll.app.data.models.Bar
import com.nightroll.app.data.models.Reel
import com.nightroll.app.databinding.FragmentHomeBinding
import com.nightroll.app.ui.adapters.BarCardAdapter
import com.nightroll.app.ui.adapters.ReelThumbnailAdapter
import com.nightroll.app.ui.bar.BarDetailsActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var barCardAdapter: BarCardAdapter
    private lateinit var reelAdapter: ReelThumbnailAdapter
    private lateinit var nearbyBarsAdapter: BarCardAdapter
    private lateinit var trendingBarsAdapter: BarCardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        
        setupViews()
        setupObservers()
        
        return binding.root
    }
    
    private fun setupViews() {
        // Setup rolling button with enhanced functionality
        binding.btnRollingTonight.setOnClickListener {
            homeViewModel.setUserStatusActive()
            updateRollingButtonState(true)
        }
        
        // Setup active bars recycler view
        barCardAdapter = BarCardAdapter { bar ->
            openBarDetails(bar)
        }
        binding.recyclerActiveBars.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = barCardAdapter
        }
        
        // Setup nearby bars
        nearbyBarsAdapter = BarCardAdapter { bar ->
            openBarDetails(bar)
        }
        binding.recyclerNearbyBars.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = nearbyBarsAdapter
        }
        
        // Setup trending bars
        trendingBarsAdapter = BarCardAdapter { bar ->
            openBarDetails(bar)
        }
        binding.recyclerTrendingBars.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = trendingBarsAdapter
        }
        
        // Setup following reels recycler view
        reelAdapter = ReelThumbnailAdapter { reel ->
            homeViewModel.playReel(reel.reelId)
        }
        binding.recyclerFollowingReels.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = reelAdapter
        }
        
        // Setup search functionality
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { homeViewModel.searchBars(it) }
                return true
            }
            
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    homeViewModel.clearSearch()
                }
                return true
            }
        })
        
        // Setup filter buttons
        binding.btnFilterAll.setOnClickListener { homeViewModel.filterBars("all") }
        binding.btnFilterRooftop.setOnClickListener { homeViewModel.filterBars("rooftop") }
        binding.btnFilterClub.setOnClickListener { homeViewModel.filterBars("club") }
        binding.btnFilterLounge.setOnClickListener { homeViewModel.filterBars("lounge") }
        
        // Setup refresh
        binding.swipeRefresh.setOnRefreshListener {
            homeViewModel.refreshData()
        }
    }
    
    private fun setupObservers() {
        homeViewModel.activeBars.observe(viewLifecycleOwner) { bars ->
            barCardAdapter.submitList(bars)
            binding.swipeRefresh.isRefreshing = false
        }
        
        homeViewModel.nearbyBars.observe(viewLifecycleOwner) { bars ->
            nearbyBarsAdapter.submitList(bars)
        }
        
        homeViewModel.trendingBars.observe(viewLifecycleOwner) { bars ->
            trendingBarsAdapter.submitList(bars)
        }
        
        homeViewModel.followingReels.observe(viewLifecycleOwner) { reels ->
            reelAdapter.submitList(reels)
        }
        
        homeViewModel.userStatus.observe(viewLifecycleOwner) { isActive ->
            updateRollingButtonState(isActive)
        }
        
        homeViewModel.searchResults.observe(viewLifecycleOwner) { results ->
            if (results.isNotEmpty()) {
                barCardAdapter.submitList(results)
            }
        }
    }
    
    private fun updateRollingButtonState(isActive: Boolean) {
        binding.btnRollingTonight.apply {
            if (isActive) {
                text = "YOU'RE ROLLING! 🎉"
                setBackgroundColor(resources.getColor(android.R.color.holo_green_dark, null))
            } else {
                text = "I'M ROLLING TONIGHT"
                setBackgroundColor(resources.getColor(com.nightroll.app.R.color.button_primary_background, null))
            }
        }
    }
    
    private fun openBarDetails(bar: Bar) {
        val intent = Intent(requireContext(), BarDetailsActivity::class.java)
        intent.putExtra("bar", bar)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}