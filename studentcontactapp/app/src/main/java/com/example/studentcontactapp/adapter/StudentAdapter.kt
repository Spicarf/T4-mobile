package com.example.studentcontactapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.studentcontactapp.database.entity.StudentEntity
import com.example.studentcontactapp.databinding.ItemStudentBinding

class StudentAdapter(
    private val onEditClick: ((StudentEntity) -> Unit)? = null,
    private val onDeleteClick: ((StudentEntity) -> Unit)? = null,
    private val onItemClick: ((StudentEntity) -> Unit)? = null
) : ListAdapter<StudentEntity, StudentAdapter.StudentViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class StudentViewHolder(private val binding: ItemStudentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(student: StudentEntity) {
            val initials = student.name.split(" ").take(2).map { it.first() }.joinToString("")
            binding.tvInitials.text = initials
            binding.tvName.text = student.name
            binding.tvNim.text = student.nim
            binding.tvProdi.text = student.prodi

            binding.btnEdit.visibility = if (onEditClick != null) View.VISIBLE else View.GONE
            binding.btnDelete.visibility = if (onDeleteClick != null) View.VISIBLE else View.GONE

            binding.btnEdit.setOnClickListener { onEditClick?.invoke(student) }
            binding.btnDelete.setOnClickListener { onDeleteClick?.invoke(student) }
            binding.root.setOnClickListener { onItemClick?.invoke(student) }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<StudentEntity>() {
        override fun areItemsTheSame(oldItem: StudentEntity, newItem: StudentEntity) =
            oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: StudentEntity, newItem: StudentEntity) =
            oldItem == newItem
    }
}