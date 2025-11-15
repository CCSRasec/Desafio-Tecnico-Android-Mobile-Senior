package com.seidor.myapplication.ui.users.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.seidor.myapplication.databinding.FragmentUserDetailsBinding
import com.seidor.myapplication.ui.users.details.compose.UserDetailsScreen
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserDetailsFragment : Fragment() {

    private var _binding: FragmentUserDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: UserDetailsFragmentArgs by navArgs()
    private val viewModel: UserDetailsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbarDetails) { view, insets ->
            view.setPadding(0, 0, 0, 0)
            WindowInsetsCompat.CONSUMED
        }

        viewModel.load(args.userId)

        binding.composeDetailsContainer.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                UserDetailsScreen(
                    user = viewModel.state.collectAsState().value,
                    onBack = { findNavController().popBackStack() }
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}