package com.example.applicationtravo.ui.home

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.applicationtravo.R
import com.example.applicationtravo.databinding.ActivityHomeBinding
import com.example.applicationtravo.models.ServicoListagemResponse
import com.example.applicationtravo.retrofit.RetrofitService
import com.example.applicationtravo.ui.detalhesLocal.DetalhesLocal
import com.example.applicationtravo.ui.listaCupons.ListaCupons
import com.example.applicationtravo.ui.configuracoes.Configuracoes
import com.example.applicationtravo.ui.TesteHomeActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null
    private var allServices = listOf<ServicoListagemResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_descontos -> {
                    startActivity(Intent(this, ListaCupons::class.java))
                    true
                }
                R.id.nav_teste_home -> {
                    startActivity(Intent(this, TesteHomeActivity::class.java))
                    true
                }
                R.id.nav_config -> {
                    startActivity(Intent(this, Configuracoes::class.java))
                    true
                }
                else -> false
            }
        }

        setupFilters()
        fetchServices()
        setupLinkToListaServicos()
        setupSearchBar()
    }

    override fun onMapReady(gMap: GoogleMap) {
        googleMap = gMap
        googleMap?.uiSettings?.isZoomControlsEnabled = true

        val fortaleza = LatLng(-3.71722, -38.54306)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(fortaleza, 13f))

        googleMap?.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoWindow(marker: com.google.android.gms.maps.model.Marker): android.view.View? = null
            override fun getInfoContents(marker: com.google.android.gms.maps.model.Marker): android.view.View {
                val view = layoutInflater.inflate(R.layout.custom_info_window, null)
                val imageView = view.findViewById<ImageView>(R.id.infoImage)
                val titleView = view.findViewById<TextView>(R.id.infoTitle)
                val addressView = view.findViewById<TextView>(R.id.infoAddress)

                val tag = marker.tag as? ServicoListagemResponse
                titleView.text = tag?.nome ?: "Sem nome"
                addressView.text = tag?.endereco ?: "Endereço não informado"

                Glide.with(this@HomeActivity)
                    .load(tag?.imagemCapaUrl)
                    .into(imageView)

                return view
            }
        })

        googleMap?.setOnInfoWindowClickListener { marker ->
            val servico = marker.tag as? ServicoListagemResponse
            servico?.let {
                val intent = Intent(this@HomeActivity, DetalhesLocal::class.java)
                intent.putExtra("SERVICO_ID", it.id)
                startActivity(intent)
            }
        }

        updateCardsAndMap("Todos")
    }

    private fun setupFilters() {
        val chipGroup = binding.chipGroup
        val categories = listOf("Todos", "Restaurantes", "Lojas", "Parques")

        categories.forEachIndexed { index, category ->
            val chip = Chip(this).apply {
                text = category
                isCheckable = true
                isChecked = index == 0
            }
            chipGroup.addView(chip)
        }

        chipGroup.setOnCheckedChangeListener { _, checkedId ->
            val chip = chipGroup.findViewById<Chip>(checkedId)
            val selected = chip?.text?.toString() ?: "Todos"
            updateCardsAndMap(selected)

            // Recentrar o mapa em Fortaleza sempre que o filtro mudar
            val fortaleza = LatLng(-3.71722, -38.54306)
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(fortaleza, 13f))
        }
    }

    private fun fetchServices() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val sharedPref = getSharedPreferences("TravoApp", Context.MODE_PRIVATE)
                val token = sharedPref.getString("token", null)

                val api = if (!token.isNullOrEmpty())
                    RetrofitService.getTravoServiceAPIWithToken(token)
                else
                    RetrofitService.getTravoServiceAPI()

                val response = api.listarServicos()

                if (response.isSuccessful) {
                    allServices = response.body() ?: emptyList()
                    Log.d("HomeActivity", "Serviços carregados: ${allServices.size}")
                } else {
                    Log.e("HomeActivity", "Erro listarServicos(): ${response.code()}")
                    allServices = emptyList()
                }

                withContext(Dispatchers.Main) { updateCardsAndMap("Todos") }

            } catch (e: Exception) {
                Log.e("HomeActivity", "Erro ao buscar serviços: ${e.message}")
                allServices = emptyList()
                withContext(Dispatchers.Main) { updateCardsAndMap("Todos") }
            }
        }
    }

    private fun updateCardsAndMap(selectedType: String) {
        val container = binding.cardContainer
        container.removeAllViews()
        googleMap?.clear()

        val typeMap = mapOf(
            "Todos" to null,
            "Restaurantes" to "restaurant",
            "Lojas" to "store",
            "Shoppings" to "shopping",
            "Parques" to "park"
        )

        val dbValue = typeMap[selectedType]

        val filtered = if (dbValue == null) {
            allServices
        } else {
            allServices.filter { it.tipo?.equals(dbValue, ignoreCase = true) == true }
        }

        for (service in filtered) {
            val cardView = LayoutInflater.from(this)
                .inflate(R.layout.card_item_layout, container, false)

            val title = cardView.findViewById<TextView>(R.id.cardTitle)
            val address = cardView.findViewById<TextView>(R.id.cardAddress)
            val image = cardView.findViewById<ImageView>(R.id.cardImage)
            val likeButton = cardView.findViewById<ImageButton>(R.id.likeButton)
            val tvRating = cardView.findViewById<TextView>(R.id.tvRating)
            val tvTotalRatings = cardView.findViewById<TextView>(R.id.tvTotalRatings)

            tvRating.text = String.format("%.1f", service.mediaAvaliacao ?: 0.0)
            tvTotalRatings.text = "(${service.totalAvaliacoes ?: 0})"
            title.text = service.nome
            address.text = service.endereco ?: "Endereço não informado"

            Glide.with(this)
                .load(service.imagemCapaUrl)
                .into(image)

            var liked = false
            likeButton.setImageResource(R.drawable.ic_heart_border)
            likeButton.setOnClickListener {
                liked = !liked
                likeButton.setImageResource(
                    if (liked) R.drawable.ic_heart_filled else R.drawable.ic_heart_border
                )
            }

            cardView.setOnClickListener {
                val intent = Intent(this@HomeActivity, DetalhesLocal::class.java)
                intent.putExtra("SERVICO_ID", service.id)
                startActivity(intent)
            }

            title.setOnClickListener {
                val intent = Intent(this@HomeActivity, DetalhesLocal::class.java)
                intent.putExtra("SERVICO_ID", service.id)
                startActivity(intent)
            }

            container.addView(cardView)

            service.lat?.let { lat ->
                service.lng?.let { lng ->
                    val marker = googleMap?.addMarker(
                        MarkerOptions().position(LatLng(lat, lng)).title(service.nome)
                    )
                    marker?.tag = service
                }
            }
        }
    }

    private fun updateCardsAndMapByService(service: ServicoListagemResponse) {
        val container = binding.cardContainer
        container.removeAllViews()
        googleMap?.clear()

        val cardView = LayoutInflater.from(this)
            .inflate(R.layout.card_item_layout, container, false)

        val title = cardView.findViewById<TextView>(R.id.cardTitle)
        val address = cardView.findViewById<TextView>(R.id.cardAddress)
        val image = cardView.findViewById<ImageView>(R.id.cardImage)
        val likeButton = cardView.findViewById<ImageButton>(R.id.likeButton)
        val tvRating = cardView.findViewById<TextView>(R.id.tvRating)
        val tvTotalRatings = cardView.findViewById<TextView>(R.id.tvTotalRatings)

        tvRating.text = String.format("%.1f", service.mediaAvaliacao ?: 0.0)
        tvTotalRatings.text = "(${service.totalAvaliacoes ?: 0})"
        title.text = service.nome
        address.text = service.endereco ?: "Endereço não informado"

        Glide.with(this)
            .load(service.imagemCapaUrl)
            .into(image)

        var liked = false
        likeButton.setImageResource(R.drawable.ic_heart_border)
        likeButton.setOnClickListener {
            liked = !liked
            likeButton.setImageResource(
                if (liked) R.drawable.ic_heart_filled else R.drawable.ic_heart_border
            )
        }

        cardView.setOnClickListener {
            val intent = Intent(this@HomeActivity, DetalhesLocal::class.java)
            intent.putExtra("SERVICO_ID", service.id)
            startActivity(intent)
        }

        title.setOnClickListener {
            val intent = Intent(this@HomeActivity, DetalhesLocal::class.java)
            intent.putExtra("SERVICO_ID", service.id)
            startActivity(intent)
        }

        container.addView(cardView)

        service.lat?.let { lat ->
            service.lng?.let { lng ->
                val position = LatLng(lat, lng)
                val marker = googleMap?.addMarker(MarkerOptions().position(position).title(service.nome))
                marker?.tag = service
            }
        }
    }

    private fun setupLinkToListaServicos() {
        val tvLink = findViewById<TextView>(R.id.tvLink)
        tvLink.paintFlags = tvLink.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        tvLink.setTextColor(getColor(R.color.teal_700))
        tvLink.setOnClickListener {
            startActivity(Intent(this, com.example.applicationtravo.ui.listaServicos.ListaServicos::class.java))
        }
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) {
            updateCardsAndMap("Todos")
            return
        }

        val filtered = allServices.filter { it.nome.contains(query, ignoreCase = true) }

        if (filtered.isEmpty()) {
            binding.cardContainer.removeAllViews()
            googleMap?.clear()
            return
        }

        val first = filtered.first()
        first.lat?.let { lat ->
            first.lng?.let { lng ->
                googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 14f))
            }
        }

        val container = binding.cardContainer
        container.removeAllViews()
        googleMap?.clear()

        for (service in filtered) {
            val cardView = LayoutInflater.from(this)
                .inflate(R.layout.card_item_layout, container, false)

            val title = cardView.findViewById<TextView>(R.id.cardTitle)
            val address = cardView.findViewById<TextView>(R.id.cardAddress)
            val image = cardView.findViewById<ImageView>(R.id.cardImage)
            val likeButton = cardView.findViewById<ImageButton>(R.id.likeButton)
            val tvRating = cardView.findViewById<TextView>(R.id.tvRating)
            val tvTotalRatings = cardView.findViewById<TextView>(R.id.tvTotalRatings)

            tvRating.text = String.format("%.1f", service.mediaAvaliacao ?: 0.0)
            tvTotalRatings.text = "(${service.totalAvaliacoes ?: 0})"
            title.text = service.nome
            address.text = service.endereco ?: "Endereço não informado"

            Glide.with(this)
                .load(service.imagemCapaUrl)
                .into(image)

            var liked = false
            likeButton.setImageResource(R.drawable.ic_heart_border)
            likeButton.setOnClickListener {
                liked = !liked
                likeButton.setImageResource(
                    if (liked) R.drawable.ic_heart_filled else R.drawable.ic_heart_border
                )
            }

            container.addView(cardView)

            service.lat?.let { lat ->
                service.lng?.let { lng ->
                    val position = LatLng(lat, lng)
                    val marker = googleMap?.addMarker(
                        MarkerOptions().position(position).title(service.nome)
                    )
                    marker?.tag = service
                }
            }
        }
    }

    private fun setupSearchBar() {
        val searchEdit = binding.etSearch
        val popup = ListPopupWindow(this)
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mutableListOf())
        popup.setAdapter(adapter)
        popup.anchorView = searchEdit
        popup.isModal = false

        // Quando os serviços carregarem, adiciona ao adapter
        lifecycleScope.launch {
            val services = allServices
            adapter.clear()
            adapter.addAll(services.mapNotNull { it.nome })
            adapter.notifyDataSetChanged()
        }

        searchEdit.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isEmpty()) {
                    popup.dismiss()
                    return
                }

                val filtered = allServices.mapNotNull { it.nome }
                    .filter { it.contains(query, ignoreCase = true) }
                adapter.clear()
                adapter.addAll(filtered)
                adapter.notifyDataSetChanged()

                if (!popup.isShowing) popup.show()
                searchEdit.requestFocus()
            }
        })

        popup.setOnItemClickListener { _, _, position, _ ->
            val selectedName = adapter.getItem(position)
            val service = allServices.find { it.nome == selectedName }
            if (service != null) {
                searchEdit.setText(service.nome)
                searchEdit.clearFocus()
                popup.dismiss()

                service.lat?.let { lat ->
                    service.lng?.let { lng ->
                        val pos = LatLng(lat, lng)
                        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 17f))
                    }
                }
                updateCardsAndMapByService(service)
            }
        }

        searchEdit.setOnEditorActionListener { _, _, _ ->
            val query = searchEdit.text.toString().trim()
            popup.dismiss()
            performSearch(query)
            true
        }
    }

    override fun onResume() { super.onResume(); mapView.onResume() }
    override fun onPause() { super.onPause(); mapView.onPause() }
    override fun onDestroy() { super.onDestroy(); mapView.onDestroy() }
    override fun onLowMemory() { super.onLowMemory(); mapView.onLowMemory() }
}
