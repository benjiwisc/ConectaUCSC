package com.ucsc.conectaucsc.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ucsc.conectaucsc.R
import com.ucsc.conectaucsc.databinding.FragmentHomeBinding
import com.ucsc.conectaucsc.utils.SessionManager

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())
        binding.tvWelcome.text = "Bienvenido, ${sessionManager.getUserName()}"

        binding.btnLogout.setOnClickListener {
            sessionManager.clearSession()
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }

        // Navegación a módulos (se completará después)
        binding.btnMaterias.setOnClickListener {
            // findNavController().navigate(R.id.action_homeFragment_to_materiasFragment)
        }

        binding.btnSesiones.setOnClickListener {
            // findNavController().navigate(R.id.action_homeFragment_to_sesionesFragment)
        }

        binding.btnPerfil.setOnClickListener {
            // findNavController().navigate(R.id.action_homeFragment_to_userFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}