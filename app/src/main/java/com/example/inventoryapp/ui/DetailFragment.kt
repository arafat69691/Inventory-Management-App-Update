package com.example.inventoryapp.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.inventoryapp.R
import com.example.inventoryapp.databinding.FragmentItemDetailBinding
import com.example.inventoryapp.viewmodel.InventoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.getValue

class ItemDetailFragment : Fragment() {
    private var _binding: FragmentItemDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InventoryViewModel by activityViewModels()
    private val args: ItemDetailFragmentArgs by navArgs()
    private lateinit var historyAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHistoryRecyclerView()
        observeItem()
        setupButtons()
    }

    private fun setupHistoryRecyclerView() {
        historyAdapter = HistoryAdapter()
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
    }

    private fun observeItem() {
        viewModel.getItemById(args.itemId).observe(viewLifecycleOwner) { item ->
            item?.let {
                displayItemDetails(it)
                loadHistory(it.id)
            } ?: run {
                Toast.makeText(requireContext(), "Item not found", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun displayItemDetails(item: com.example.inventoryapp.data.InventoryItem) {
        binding.apply {
            tvItemName.text = item.name
            tvCategory.text = item.category
            tvQuantity.text = item.quantity.toString()
            tvPrice.text = String.format("$%.2f", item.price)
            tvTotalValue.text = String.format("$%.2f", item.quantity * item.price)
            tvDescription.text = item.description.ifEmpty { "No description" }
            tvThreshold.text = item.lowStockThreshold.toString()

            val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            tvCreatedAt.text = "Created: ${dateFormat.format(Date(item.createdAt))}"
            tvUpdatedAt.text = "Updated: ${dateFormat.format(Date(item.updatedAt))}"

            // Low stock indicator
            val isLowStock = item.quantity <= item.lowStockThreshold
            if (isLowStock) {
                cardLowStock.visibility = View.VISIBLE
                tvQuantity.setTextColor(ContextCompat.getColor(requireContext(), R.color.error))
            } else {
                cardLowStock.visibility = View.GONE
                tvQuantity.setTextColor(ContextCompat.getColor(requireContext(), R.color.success))
            }
        }
    }

    private fun loadHistory(itemId: Int) {
        viewModel.getItemHistory(itemId).observe(viewLifecycleOwner) { history ->
            historyAdapter.submitList(history)
            binding.tvHistoryEmpty.visibility = if (history.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun setupButtons() {
        val item = viewModel.getItemById(args.itemId).value ?: return

        binding.btnEdit.setOnClickListener {
            val action = ItemDetailFragmentDirections.actionItemDetailToEditItem(args.itemId)
            findNavController().navigate(action)
        }

        binding.btnAddStock.setOnClickListener {
            showQuantityDialog(item, true)
        }

        binding.btnRemoveStock.setOnClickListener {
            showQuantityDialog(item, false)
        }

        binding.btnDelete.setOnClickListener {
            showDeleteConfirmation(item)
        }
    }

    private fun showQuantityDialog(
        item: com.example.inventoryapp.data.InventoryItem,
        isAddition: Boolean
    ) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_quantity, null)
        val etQuantity = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(
            R.id.et_quantity_change
        )

        AlertDialog.Builder(requireContext())
            .setTitle(if (isAddition) "Add Stock" else "Remove Stock")
            .setView(dialogView)
            .setPositiveButton("Confirm") { _, _ ->
                val quantity = etQuantity.text.toString().toIntOrNull()
                if (quantity != null && quantity > 0) {
                    viewModel.updateQuantity(item, quantity, isAddition)
                    Toast.makeText(
                        requireContext(),
                        if (isAddition) "Stock added" else "Stock removed",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Invalid quantity",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirmation(item: com.example.inventoryapp.data.InventoryItem) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Item")
            .setMessage("Are you sure you want to delete '${item.name}'? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.delete(item)
                Toast.makeText(requireContext(), "Item deleted", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
