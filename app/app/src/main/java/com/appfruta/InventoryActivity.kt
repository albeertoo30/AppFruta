package com.appfruta

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class InventoryActivity : AppCompatActivity() {

    private val repository = InventoryRepository()
    private lateinit var adapter: FruitItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) { finish(); return }
        val uid = currentUser.uid

        setContentView(R.layout.activity_inventory)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val rvInventory = findViewById<RecyclerView>(R.id.rvInventory)
        val tvEmptyState = findViewById<View>(R.id.tvEmptyState)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fabAddFruit)

        adapter = FruitItemAdapter { item ->
            AddFruitDialogFragment.forEdit(item)
                .show(supportFragmentManager, "edit_fruit")
        }
        rvInventory.layoutManager = LinearLayoutManager(this)
        rvInventory.adapter = adapter

        attachSwipeToDelete(rvInventory, uid)

        fabAdd.setOnClickListener {
            AddFruitDialogFragment.newInstance()
                .show(supportFragmentManager, "add_fruit")
        }

        lifecycleScope.launch {
            repository.observeItems(uid).collect { items ->
                adapter.submitList(items)
                tvEmptyState.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun attachSwipeToDelete(rv: RecyclerView, uid: String) {
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = adapter.currentList[viewHolder.bindingAdapterPosition]
                lifecycleScope.launch {
                    repository.delete(uid, item.id)
                    Snackbar.make(rv, R.string.item_deleted, Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo) {
                            lifecycleScope.launch { repository.add(uid, item) }
                        }.show()
                }
            }
        }).attachToRecyclerView(rv)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { finish(); return true }
        return super.onOptionsItemSelected(item)
    }
}
