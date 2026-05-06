package com.mobile.app_database.database

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.studentdirectory.R
import com.example.studentdirectory.adapter.StudentAdapter
import com.example.studentdirectory.database.AppDatabase
import com.example.studentdirectory.database.entity.StudentEntity
import com.example.studentdirectory.ui.addedit.AddEditStudentActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var tvEmpty: TextView
    private lateinit var adapter: StudentAdapter
    private lateinit var studentDao: com.example.studentdirectory.database.dao.StudentDao

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        fabAdd       = view.findViewById(R.id.fabAdd)
        tvEmpty      = view.findViewById(R.id.tvEmpty)

        studentDao = AppDatabase.getInstance(requireContext()).studentDao()

        setupRecyclerView()
        setupFab()
        setupSwipeToDelete()
        observeStudents()
        insertSampleDataIfEmpty()
    }

    private fun setupRecyclerView() {
        adapter = StudentAdapter(
            onEditClick = { student ->
                val intent = Intent(requireContext(), AddEditStudentActivity::class.java).apply {
                    putExtra(AddEditStudentActivity.EXTRA_STUDENT_ID, student.id)
                }
                startActivity(intent)
            },
            onDeleteClick = { student -> showDeleteDialog(student) }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupFab() {
        fabAdd.setOnClickListener {
            startActivity(Intent(requireContext(), AddEditStudentActivity::class.java))
        }
    }

    // ── BONUS: Swipe to Delete ──────────────────────────────────────────────
    private fun setupSwipeToDelete() {
        val swipeCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            private val background = ColorDrawable(Color.parseColor("#E53935"))
            private val deleteIcon = ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_menu_delete)!!

            override fun onMove(rv: RecyclerView, vh: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val student = adapter.getItemAt(position)
                showDeleteDialog(student)
                // Kembalikan item (dialog yang akan handle delete sungguhan)
                adapter.notifyItemChanged(position)
            }

            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2

                if (dX > 0) { // swipe kanan
                    background.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
                    deleteIcon.setBounds(
                        itemView.left + iconMargin, itemView.top + iconMargin,
                        itemView.left + iconMargin + deleteIcon.intrinsicWidth, itemView.bottom - iconMargin
                    )
                } else { // swipe kiri
                    background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    deleteIcon.setBounds(
                        itemView.right - iconMargin - deleteIcon.intrinsicWidth, itemView.top + iconMargin,
                        itemView.right - iconMargin, itemView.bottom - iconMargin
                    )
                }
                background.draw(c)
                deleteIcon.draw(c)
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        ItemTouchHelper(swipeCallback).attachToRecyclerView(recyclerView)
    }

    // ── Observe Flow dari Room ──────────────────────────────────────────────
    private fun observeStudents() {
        viewLifecycleOwner.lifecycleScope.launch {
            studentDao.getAllStudents().collectLatest { list ->
                adapter.submitList(list)
                tvEmpty.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    // ── Insert sample data jika DB kosong ──────────────────────────────────
    private fun insertSampleDataIfEmpty() {
        viewLifecycleOwner.lifecycleScope.launch {
            if (studentDao.getStudentCount() == 0) {
                val samples = listOf(
                    StudentEntity(name = "Ahmad Fauzi",  nim = "2024001", prodi = "Informatika",        email = "ahmad@example.com",  semester = 2),
                    StudentEntity(name = "Budi Santoso", nim = "2024002", prodi = "Sistem Informasi",   email = "budi@example.com",   semester = 2),
                    StudentEntity(name = "Clara Wijaya", nim = "2024003", prodi = "Teknik Elektro",     email = "clara@example.com",  semester = 4)
                )
                studentDao.insertAll(samples)
            }
        }
    }

    // ── Dialog konfirmasi hapus ─────────────────────────────────────────────
    private fun showDeleteDialog(student: StudentEntity) {
        AlertDialog.Builder(requireContext())
            .setTitle("Hapus Data?")
            .setMessage("Hapus \"${student.name}\"? Tindakan ini tidak dapat dibatalkan.")
            .setNegativeButton("Batal", null)
            .setPositiveButton("Hapus") { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    studentDao.deleteById(student.id)
                    Snackbar.make(requireView(), "${student.name} dihapus", Snackbar.LENGTH_SHORT).show()
                }
            }
            .show()
    }
}