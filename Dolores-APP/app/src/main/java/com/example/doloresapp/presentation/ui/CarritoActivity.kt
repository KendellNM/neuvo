package com.example.doloresapp.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doloresapp.R
import com.example.doloresapp.data.local.database.AppDatabase
import com.example.doloresapp.data.local.entity.CarritoItem
import com.example.doloresapp.presentation.adapter.CarritoAdapter
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CarritoActivity : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var tvEmpty: TextView
    private lateinit var btnCheckout: Button
    private lateinit var adapter: CarritoAdapter
    private lateinit var database: AppDatabase
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)
        
        database = AppDatabase.getDatabase(this)
        
        // Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Mi Carrito"
        
        // Views
        recyclerView = findViewById(R.id.recyclerCarrito)
        tvTotal = findViewById(R.id.tvTotal)
        tvEmpty = findViewById(R.id.tvEmpty)
        btnCheckout = findViewById(R.id.btnCheckout)
        
        // Adapter
        adapter = CarritoAdapter(
            onUpdateCantidad = { item, nuevaCantidad ->
                updateCantidad(item, nuevaCantidad)
            },
            onEliminar = { item ->
                eliminarItem(item)
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Observar carrito
        lifecycleScope.launch {
            database.carritoDao().getAllItems().collectLatest { items ->
                adapter.submitList(items)
                updateUI(items)
            }
        }
        
        // Observar total
        lifecycleScope.launch {
            database.carritoDao().getTotal().collectLatest { total ->
                tvTotal.text = "Total: S/ %.2f".format(total ?: 0.0)
            }
        }
        
        // Checkout
        btnCheckout.setOnClickListener {
            lifecycleScope.launch {
                val items = database.carritoDao().getAllItemsList()
                if (items.isEmpty()) {
                    Toast.makeText(this@CarritoActivity, "El carrito está vacío", Toast.LENGTH_SHORT).show()
                } else {
                    // Ir a checkout
                    startActivity(Intent(this@CarritoActivity, CheckoutActivity::class.java))
                }
            }
        }
    }
    
    private fun updateUI(items: List<CarritoItem>) {
        if (items.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            btnCheckout.isEnabled = false
        } else {
            tvEmpty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            btnCheckout.isEnabled = true
        }
    }
    
    private fun updateCantidad(item: CarritoItem, nuevaCantidad: Int) {
        lifecycleScope.launch {
            if (nuevaCantidad <= 0) {
                database.carritoDao().deleteItem(item)
            } else {
                database.carritoDao().updateItem(item.copy(cantidad = nuevaCantidad))
            }
        }
    }
    
    private fun eliminarItem(item: CarritoItem) {
        lifecycleScope.launch {
            database.carritoDao().deleteItem(item)
            Toast.makeText(this@CarritoActivity, "Producto eliminado", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
