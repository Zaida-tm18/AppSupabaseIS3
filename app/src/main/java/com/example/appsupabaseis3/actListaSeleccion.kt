package com.example.appsupabaseis3

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.appsupabaseis3.data.SupabaseManager
import com.example.appsupabaseis3.models.Materia
import com.example.appsupabaseis3.utils.SupabaseErrorHandler
import io.github.jan.supabase.exceptions.RestException
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.launch

class actListaSeleccion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_act_lista_seleccion)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val actNiveles = findViewById<AutoCompleteTextView>(R.id.ListaNiveles)
        actNiveles.setOnItemClickListener { parent, view, position, id ->
            val nivelSeleccionado = parent.getItemAtPosition(position).toString()
            Toast.makeText( this,
                 "Nivel seleccionado: $nivelSeleccionado",
                 Toast.LENGTH_LONG).show()

            llenarMaterias(position+1)
            llenarAlumnos(position+1)
        }
    }

    fun llenarMaterias(nivel: Int)
    {
        val actMaterias = findViewById<AutoCompleteTextView>(R.id.ListaMaterias)
        val lstMaterias= ArrayList<String>()
        lifecycleScope.launch {
            try {
                val listaMaterias = ArrayList(
                    SupabaseManager.client
                        .from("materias")
                        .select {
                            filter { eq("nivel", nivel) }
                            order("nombre", Order.ASCENDING)
                        }
                        .decodeList<Materia>()
                )
                for (materia in listaMaterias) {
                    lstMaterias.add(materia.nombre ?: "")
                }
            }
            catch (e: RestException) {
                SupabaseErrorHandler.show(this@actListaSeleccion, e)
                lstMaterias.clear()
            } finally {
                val adapter = ArrayAdapter(
                    this@actListaSeleccion,
                    android.R.layout.simple_spinner_dropdown_item,
                    lstMaterias
                )
                actMaterias.setAdapter(adapter)
            }
        }
    }

    fun llenarAlumnos(nivel: Int) {
        val actAlumnos = findViewById<AutoCompleteTextView>(R.id.ListaMaterias)
        val lstAlumnos = ArrayList<String>()
        lifecycleScope.launch {
            try {
                val listaAlumnos = ArrayList(
                    SupabaseManager.client
                        .from("alumnos")
                        .select {
                            filter { eq("nivel", nivel) }
                            order("nombre", Order.ASCENDING)
                        }
                        .decodeList<Materia>()
                )
                for (materia in listaAlumnos) {
                    lstAlumnos.add(materia.nombre ?: "")
                }
            } catch (e: RestException) {
                SupabaseErrorHandler.show(this@actListaSeleccion, e)
                lstAlumnos.clear()
            } finally {
                val adapter = ArrayAdapter(
                    this@actListaSeleccion,
                    android.R.layout.simple_spinner_dropdown_item,
                    lstAlumnos
                )
                actAlumnos.setAdapter(adapter)
            }
        }
    }
}