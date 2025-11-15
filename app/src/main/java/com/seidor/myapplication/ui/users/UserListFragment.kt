package com.seidor.myapplication.ui.users

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.seidor.myapplication.databinding.FragmentUserListBinding
import com.seidor.myapplication.ui.users.compose.UserComposeList
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserListFragment : Fragment() {

    private var _binding: FragmentUserListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: UserViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.composeContainer.apply {
            setContent {
                MaterialTheme {
                    UserComposeList(viewModel) { userId ->
                        findNavController().navigate(
                            UserListFragmentDirections.actionUserListFragmentToUserDetailsFragment(userId)
                        )
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}