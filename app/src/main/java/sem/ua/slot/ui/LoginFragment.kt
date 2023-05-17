package sem.ua.slot.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sem.ua.slot.R
import sem.ua.slot.adapter.UserAdapter
import sem.ua.slot.data.AppDatabase
import sem.ua.slot.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val appDatabase = AppDatabase.getInstance(requireContext())
        val userDao = appDatabase.userDao()

        viewLifecycleOwner.lifecycleScope.launch {
            val users = withContext(Dispatchers.IO) {
                userDao.getAllUsers()
            }
            Log.d("usres",users.toString());

            recyclerView = view.findViewById(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(activity)
            recyclerView.setHasFixedSize(true)
            adapter = UserAdapter(users)
            recyclerView.adapter = adapter

            binding.btnStartGame.setOnClickListener {
                val inputValue = binding.editTextName.text.toString()
                if (inputValue.isNotEmpty()) {
                    val action =
                        LoginFragmentDirections.actionLoginFragmentToSpinFragment(inputValue)
                    findNavController().navigate(action)
                } else {
                    Toast.makeText(context, "Input value cannot be empty", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}
