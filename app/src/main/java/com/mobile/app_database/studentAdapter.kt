package com.mobile.app_database.database

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.studentdirectory.R
import com.example.studentdirectory.database.entity.StudentEntity

class StudentAdapter(
    private val onEditClick: (StudentEntity) -> Unit,
    private val onDeleteClick: (StudentEntity) -> Unit
) : ListAdapter<StudentEntity, StudentAdapter.StudentViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StudentEntity>() {
            override fun areItemsTheSame(old: StudentEntity, new: StudentEntity) =
                old.id == new.id

            override fun areContentsTheSame(old: StudentEntity, new: StudentEntity) =
                old == new
        }
    }

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvAvatar: TextView    = itemView.findViewById(R.id.tvAvatar)
        private val tvName: TextView      = itemView.findViewById(R.id.tvName)
        private val tvNim: TextView       = itemView.findViewById(R.id.tvNim)
        private val btnEdit: ImageButton  = itemView.findViewById(R.id.btnEdit)
        private val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)

        fun bind(student: StudentEntity) {
            // Ambil 2 huruf pertama dari nama untuk avatar
            val initials = student.name
                .trim()
                .split(" ")
                .filter { it.isNotEmpty() }
                .take(2)
                .joinToString("") { it.first().uppercaseChar().toString() }
            tvAvatar.text = initials
            tvName.text   = student.name
            tvNim.text    = student.nim

            btnEdit.setOnClickListener   { onEditClick(student) }
            btnDelete.setOnClickListener { onDeleteClick(student) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    // Expose item untuk SwipeToDelete
    fun getItemAt(position: Int): StudentEntity = getItem(position)
}