package com.example.my_track_fit

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

class UserSettingsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_user_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = requireActivity().getSharedPreferences("MyTrackFitPrefs", android.content.Context.MODE_PRIVATE)
        val userEmail = sharedPref.getString("userEmail", "Correo no disponible")
        val tvUserEmail = view.findViewById<TextView>(R.id.tvUserEmail)
        tvUserEmail.text = userEmail

        val btnLogout = view.findViewById<Button>(R.id.btn_logout)
        btnLogout.setOnClickListener {
            // Borrar datos de sesión
            with(sharedPref.edit()) {
                clear()
                apply()
            }
            // Ir a LoginActivity y cerrar la actividad actual
            val intent = Intent(requireActivity(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }
}