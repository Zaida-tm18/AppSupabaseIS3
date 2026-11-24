package com.example.appsupabaseis3.utils

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder
object SupabaseErrorHandler {
    fun show(context: Context, error: Throwable) {
        val detail = error.localizedMessage
            ?: "No se pudo completar la consulta."

        MaterialAlertDialogBuilder(context)
            .setTitle("Error de conexión")
            .setMessage(detail)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton("Aceptar", null)
            .show()
    }
}
