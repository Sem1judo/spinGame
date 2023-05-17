package sem.ua.slot.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sem.ua.slot.R
import sem.ua.slot.model.User

class UserAdapter(
    private val userList: List<User>
) : RecyclerView.Adapter<UserAdapter.ItemViewHolder>() {

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.user_name)
        val userBalance: TextView = view.findViewById(R.id.user_balance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_user, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentUser = userList[position]
        holder.userName.text = currentUser.name
        holder.userBalance.text =
            holder.itemView.context.getString(R.string.name_balance, currentUser.balance)
    }

    override fun getItemCount() = userList.size
}