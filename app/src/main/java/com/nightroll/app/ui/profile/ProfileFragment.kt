package com.nightroll.app.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.nightroll.app.databinding.FragmentProfileBinding
import com.nightroll.app.ui.adapters.ReelThumbnailAdapter

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var profileViewModel: ProfileViewModel
    private lateinit var vibeRecapAdapter: ReelThumbnailAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        
        setupViews()
        setupObservers()
        
        return binding.root
    }
    
    private fun setupViews() {
        binding.apply {
            // Setup action buttons
            btnShare.setOnClickListener { profileViewModel.shareProfile() }
            btnSettings.setOnClickListener { profileViewModel.navigateToSettings() }
            btnEditProfile.setOnClickListener { profileViewModel.editProfile() }
            
            // Setup segmented control
            btnNightlist.setOnClickListener { 
                profileViewModel.setSelectedTab("NIGHTLIST")
                updateTabSelection(true)
            }
            btnComments.setOnClickListener { 
                profileViewModel.setSelectedTab("COMMENTS")
                updateTabSelection(false)
            }
            
            // Setup vibe recap grid
            vibeRecapAdapter = ReelThumbnailAdapter { reel ->
                profileViewModel.playReel(reel.reelId)
            }
            recyclerVibeRecap.apply {
                layoutManager = GridLayoutManager(context, 4)
                adapter = vibeRecapAdapter
            }
        }
    }
    
    private fun setupObservers() {
        profileViewModel.user.observe(viewLifecycleOwner) { user ->
            binding.apply {
                Glide.with(requireContext())
                    .load(user.profilePictureUrl)
                    .circleCrop()
                    .into(imageProfile)
                
                textUsername.text = user.username
                textBio.text = user.bio
            }
        }
        
        profileViewModel.vibeRecapReels.observe(viewLifecycleOwner) { reels ->
            vibeRecapAdapter.submitList(reels)
        }
    }
    
    private fun updateTabSelection(nightlistSelected: Boolean) {
        binding.apply {
            if (nightlistSelected) {
                btnNightlist.setBackgroundColor(resources.getColor(android.R.color.black, null))
                btnNightlist.setTextColor(resources.getColor(android.R.color.white, null))
                btnComments.setBackgroundColor(resources.getColor(android.R.color.transparent, null))
                btnComments.setTextColor(resources.getColor(android.R.color.black, null))
            } else {
                btnComments.setBackgroundColor(resources.getColor(android.R.color.black, null))
                btnComments.setTextColor(resources.getColor(android.R.color.white, null))
                btnNightlist.setBackgroundColor(resources.getColor(android.R.color.transparent, null))
                btnNightlist.setTextColor(resources.getColor(android.R.color.black, null))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}