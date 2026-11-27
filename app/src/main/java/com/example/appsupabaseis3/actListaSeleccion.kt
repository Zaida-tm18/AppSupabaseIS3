package com.example.appsupabaseis3

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.appsupabaseis3.data.SupabaseManager
import com.example.appsupabaseis3.models.Alumno
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

        val lvAlumnos = findViewById<ListView>(R.id.lvAlumnos)
        val header = layoutInflater.inflate(R.layout.ly_header, lvAlumnos, false)
        lvAlumnos.addHeaderView(header, null, false)

        lvAlumnos.setOnItemClickListener { parent, _, position, _ ->
            val alumno = parent.getItemAtPosition(position) as? Map<*, *>
                ?: return@setOnItemClickListener
            Toast.makeText(this,
                "Item seleccionado: ${alumno["nombre"]}",
                Toast.LENGTH_SHORT).show()
        }

        llenarAlumnos()

        val actNiveles = findViewById<AutoCompleteTextView>(R.id.ListaNiveles)
        actNiveles.setOnItemClickListener { parent, view, position, id ->
            val nivelSeleccionado = parent.getItemAtPosition(position).toString()
            Toast.makeText( this,
                 "Nivel seleccionado: $nivelSeleccionado",
                 Toast.LENGTH_LONG).show()

            llenarMaterias(position+1)
        }
    }

    fun llenarAlumnos() {
        val lvAlumnos = findViewById<ListView>(R.id.lvAlumnos)
        lifecycleScope.launch {
            try {
                val alumnos = SupabaseManager.client
                    .from("alumnos")
                    .select {
                        order("nombres", Order.ASCENDING)
                    }
                    .decodeList<Alumno>()
                val items = alumnos.map {
                    mapOf(
                        "foto" to (it.foto ?: ""),
                        "nombre" to (it.nombres ?: ""),
                        "correo" to "Correo: ${it.correo ?: "Sin registrar"}",
                        "telefono" to "Teléfono: ${it.telefono ?: "Sin registrar"}"
                    )
                }
                val adapter = SimpleAdapter(
                    this@actListaSeleccion,
                    items,
                    R.layout.item_alumno,
                    arrayOf("foto", "nombre", "correo", "telefono"),
                    intArrayOf(R.id.imgFoto, R.id.txtNombre, R.id.txtCorreo, R.id.txtTelefono)
                )

                // Solo la foto necesita trato especial; los textos los pone SimpleAdapter
                adapter.viewBinder = SimpleAdapter.ViewBinder { view, data, _ ->
                    if (view.id == R.id.imgFoto) {
                        Glide.with(view)
                            .load(data as? String)
                            .circleCrop()
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .into(view as ImageView)
                        true
                    } else {
                        false
                    }
                }

                lvAlumnos.adapter = adapter
            } catch (e: Exception) {
                SupabaseErrorHandler.show(this@actListaSeleccion, e)
            }
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

}