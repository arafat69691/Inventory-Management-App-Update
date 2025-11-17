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
import com.example.inventoryapp.databinding.FragmentDashboardBinding
import com.example.inventoryapp.viewmodel.InventoryViewModel
import java.text.NumberFormat
import java.util.Locale

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InventoryViewModel by activityViewModels()
    private lateinit var adapter: InventoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup RecyclerView for low stock items
        adapter = InventoryAdapter(onItemClick = {}, onDeleteClick = {})
        binding.rvLowStock.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLowStock.adapter = adapter

        // Observe LiveData from ViewModel
        observeData()

        // Navigate to InventoryListFragment when inventory card is clicked
//        binding.cardInventory.setOnClickListener {
//            findNavController().navigate(R.id.action_dashboard_to_inventoryList)
//        }
//        val action =
//            DashboardFragmentDirections.action_dashboard_to_inventoryList()
//        findNavController().navigate(action)

    }

    private fun observeData() {
        val formatter = NumberFormat.getCurrencyInstance(Locale.US)

        // Total items and total value
        viewModel.allItems.observe(viewLifecycleOwner) { items ->
            binding.tvTotalItems.text = items.size.toString()
            val totalValue = items.sumOf { it.price * it.quantity }
            binding.tvTotalValue.text = formatter.format(totalValue)
        }

        // Low stock items
        viewModel.lowStockItems.observe(viewLifecycleOwner) { lowStockItems ->
            binding.tvLowStockCount.text = lowStockItems.size.toString()
            adapter.submitList(lowStockItems.take(5))
        }

        // Categories
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            binding.tvCategories.text = categories.size.toString()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
