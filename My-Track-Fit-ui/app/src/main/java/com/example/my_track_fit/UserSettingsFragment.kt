package com.example.my_track_fit

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import java.io.File

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

        val btnDownload = view.findViewById<Button>(R.id.btn_download_cloud)
        val btnUpload = view.findViewById<Button>(R.id.btn_upload_cloud)

        btnDownload.setOnClickListener {
            downloadFilesFromCloud()
        }

        btnUpload.setOnClickListener {
            uploadFilesToCloud()
        }
    }

    private fun downloadFilesFromCloud() {
        val storage = FirebaseStorage.getInstance()
        val files = listOf("bodyweight.json", "rutinas.json", "ejercicios.json")
        val localDir = requireContext().filesDir

        for (fileName in files) {
            val ref = storage.reference.child("users/${getUserId()}/$fileName")
            val localFile = File(localDir, fileName)
            ref.getFile(localFile).addOnSuccessListener {
                Toast.makeText(context, "$fileName descargado", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(context, "Error al descargar $fileName", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadFilesToCloud() {
        val storage = FirebaseStorage.getInstance()
        val files = listOf("bodyweight.json", "rutinas.json", "ejercicios.json")
        val localDir = requireContext().filesDir

        for (fileName in files) {
            val ref = storage.reference.child("users/${getUserId()}/$fileName")
            val localFile = File(localDir, fileName)
            if (localFile.exists()) {
                ref.putFile(android.net.Uri.fromFile(localFile)).addOnSuccessListener {
                    Toast.makeText(context, "$fileName subido", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(context, "Error al subir $fileName", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "$fileName no existe localmente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getUserId(): String {
        // Usa el correo como identificador, adaptando los caracteres para Firebase
        val sharedPref = requireActivity().getSharedPreferences("MyTrackFitPrefs", android.content.Context.MODE_PRIVATE)
        return sharedPref.getString("userEmail", "default")!!.replace("@", "_").replace(".", "_")
    }
}