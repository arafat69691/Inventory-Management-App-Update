package com.example.inventoryapp.ui

import com.example.inventoryapp.R

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.inventoryapp.data.InventoryItem
import com.example.inventoryapp.databinding.FragmentAddItemBinding
import com.example.inventoryapp.viewmodel.InventoryViewModel


class AddItemFragment : Fragment() {
    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InventoryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategorySpinner()
        setupButtons()
    }

    private fun setupCategorySpinner() {
        // Option 1: If you have categories in strings.xml
        val categories = if (resources.getIdentifier("categories", "array", requireContext().packageName) != 0) {
            resources.getStringArray(R.array.categories)
        } else {
            // Option 2: Hardcoded categories as fallback
            arrayOf(
                "Electronics",
                "Furniture",
                "Clothing",
                "Food",
                "Books",
                "Tools",
                "Sports",
                "Toys",
                "Health",
                "Beauty",
                "Automotive",
                "Other"
            )
        }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,  // ← Android system layout
            categories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
    }

    private fun setupButtons() {
        binding.btnSave.setOnClickListener {
            saveItem()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun saveItem() {
        val name = binding.etName.text.toString().trim()
        val category = binding.spinnerCategory.selectedItem.toString()
        val quantityStr = binding.etQuantity.text.toString().trim()
        val priceStr = binding.etPrice.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val thresholdStr = binding.etThreshold.text.toString().trim()

        // Validation
        if (name.isEmpty()) {
            binding.etName.error = "Required"
            return
        }

        if (quantityStr.isEmpty()) {
            binding.etQuantity.error = "Required"
            return
        }

        if (priceStr.isEmpty()) {
            binding.etPrice.error = "Required"
            return
        }

        if (thresholdStr.isEmpty()) {
            binding.etThreshold.error = "Required"
            return
        }

        val quantity = quantityStr.toIntOrNull()
        val price = priceStr.toDoubleOrNull()
        val threshold = thresholdStr.toIntOrNull()

        if (quantity == null || quantity < 0) {
            binding.etQuantity.error = "Invalid quantity"
            return
        }

        if (price == null || price < 0) {
            binding.etPrice.error = "Invalid price"
            return
        }

        if (threshold == null || threshold < 0) {
            binding.etThreshold.error = "Invalid threshold"
            return
        }

        val item = InventoryItem(
            name = name,
            category = category,
            quantity = quantity,
            price = price,
            description = description,
            lowStockThreshold = threshold
        )

        viewModel.insert(item)
        Toast.makeText(requireContext(), "Item added successfully", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}