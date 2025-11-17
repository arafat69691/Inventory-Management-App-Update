package com.example.inventoryapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventoryapp.R

import com.example.inventoryapp.databinding.FragmentInventoryListBinding
import com.example.inventoryapp.viewmodel.InventoryViewModel

class InventoryListFragment : Fragment() {

    private var _binding: FragmentInventoryListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InventoryViewModel by activityViewModels()
    private lateinit var adapter: InventoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInventoryListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeData()

        // Navigate to AddItemFragment when FAB is clicked
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_inventoryList_to_addItem)
        }
        binding.editbtn.setOnClickListener {
            findNavController().navigate(R.id.action_inventoryListFragment_to_editItemFragment)
        }
    }

    private fun setupRecyclerView() {
        adapter = InventoryAdapter(
            onItemClick = { /* Handle item click if needed */ },
            onDeleteClick = { item -> viewModel.delete(item) }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun observeData() {
        viewModel.allItems.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            // Show empty view if no items
            binding.emptyView.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerView.visibility = if (items.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
