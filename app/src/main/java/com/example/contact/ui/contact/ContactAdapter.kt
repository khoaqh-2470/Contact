package com.example.contact.ui.contact

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.contact.R
import com.example.contact.model.Contacts
import com.example.contact.utils.OnItemClickListener
import kotlinx.android.synthetic.main.adapter_contact.view.*

class ContactAdapter(
    private val mList: List<Contacts>
) : RecyclerView.Adapter<ContactAdapter.ItemViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_contact, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        var str = mList[position].name.toCharArray()[0].toString().toUpperCase()
        holder.onBind(mList[position])
    }

    override fun getItemCount() = mList.size

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun onBind(contacts: Contacts) {
            itemView.apply {
                mTextView.text = contacts.name
                mPhoneNumber.text = contacts.phoneNumber
                mImgPerson.setImageResource(R.drawable.ic_baseline_person_24)
            }
        }
    }

}

