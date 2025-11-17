package com.example.inventoryapp.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.inventoryapp.R
import com.example.inventoryapp.data.InventoryItem
import com.example.inventoryapp.databinding.FragmentEditItemBinding
import com.example.inventoryapp.viewmodel.InventoryViewModel

class EditItemFragment : Fragment() {
    private var _binding: FragmentEditItemBinding? = null
    private val binding get() = _binding!!
    private val viewModel: InventoryViewModel by activityViewModels()
    private val args: EditItemFragmentArgs by navArgs()
    private var currentItem: InventoryItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategorySpinner()
        observeItem()
        setupButtons()
    }

    private fun setupCategorySpinner() {
        val categories = arrayOf(
            "Electronics", "Furniture", "Clothing",
            "Food", "Books", "Tools", "Other"
        )
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )
        binding.spinnerCategory.adapter = adapter
    }

    private fun observeItem() {
        viewModel.getItemById(args.itemId).observe(viewLifecycleOwner) { item ->
            item?.let {
                currentItem = it
                populateFields(it)
            }
        }
    }

    private fun populateFields(item: InventoryItem) {
        binding.apply {
            etName.setText(item.name)
            etQuantity.setText(item.quantity.toString())
            etPrice.setText(item.price.toString())
            etDescription.setText(item.description)
            etThreshold.setText(item.lowStockThreshold.toString())

            // Set spinner selection
            val categories = (spinnerCategory.adapter as ArrayAdapter<String>)
            val position = (0 until categories.count).find {
                categories.getItem(it) == item.category
            } ?: 0
            spinnerCategory.setSelection(position)
        }
    }

    private fun setupButtons() {
        binding.btnSave.setOnClickListener {
            saveChanges()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun saveChanges() {
        val oldItem = currentItem ?: return

        val name = binding.etName.text.toString().trim()
        val category = binding.spinnerCategory.selectedItem.toString()
        val quantityStr = binding.etQuantity.text.toString().trim()
        val priceStr = binding.etPrice.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val thresholdStr = binding.etThreshold.text.toString().trim()

        // Validation
        if (name.isEmpty() || quantityStr.isEmpty() ||
            priceStr.isEmpty() || thresholdStr.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Please fill all required fields",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val newItem = oldItem.copy(
            name = name,
            category = category,
            quantity = quantityStr.toIntOrNull() ?: oldItem.quantity,
            price = priceStr.toDoubleOrNull() ?: oldItem.price,
            description = description,
            lowStockThreshold = thresholdStr.toIntOrNull() ?: oldItem.lowStockThreshold
        )

        viewModel.update(oldItem, newItem)
        Toast.makeText(requireContext(), "Item updated", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
