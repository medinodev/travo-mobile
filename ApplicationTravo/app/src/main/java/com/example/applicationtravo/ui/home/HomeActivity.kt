package com.example.applicationtravo.ui.home

import android.graphics.drawable.Drawable
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import android.graphics.Bitmap
import android.graphics.Canvas
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.TextView
import android.widget.ImageView

class HomeActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var mapView: MapView

    private val infoWindowImageCache = mutableMapOf<String, Bitmap>()

    private var googleMap: GoogleMap? = null
    private var allServices = listOf<ServicoListagemResponse>()
    private var selectedMarker: Marker? = null

    private val markerList = mutableListOf<Marker>()

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

            override fun getInfoWindow(marker: Marker): View? = null

            override fun getInfoContents(marker: Marker): View? {
                if (marker != selectedMarker) return null

                val tag = marker.tag as? ServicoListagemResponse
                val v = layoutInflater.inflate(R.layout.custom_info_window, null)

                val img = v.findViewById<ImageView>(R.id.infoImage)
                val title = v.findViewById<TextView>(R.id.infoTitle)
                val address = v.findViewById<TextView>(R.id.infoAddress)

                title.text = tag?.nome ?: ""
                address.text = tag?.endereco ?: ""

                // tenta fotoPerfilUrl → imagemCapaUrl
                val url = tag?.fotoPerfilUrl ?: tag?.imagemCapaUrl

                // se realmente não tem imagem
                if (url.isNullOrBlank()) {
                    img.setImageBitmap(null)
                    return v
                }

                // se está em cache → usa e retorna
                val cached = infoWindowImageCache[url]
                if (cached != null) {
                    img.setImageBitmap(cached)
                    return v
                }

                // carrega com Glide
                Glide.with(this@HomeActivity)
                    .asBitmap()
                    .load(url)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                            infoWindowImageCache[url] = bitmap

                            if (marker.isInfoWindowShown) {
                                marker.hideInfoWindow()
                                marker.showInfoWindow()
                            }
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })

                return v
            }


        })

        googleMap?.setOnMarkerClickListener { m ->
            selectedMarker = m
            m.showInfoWindow()
            false
        }

        googleMap?.setOnInfoWindowClickListener { m ->
            val s = m.tag as? ServicoListagemResponse
            if (s != null) {
                val i = Intent(this@HomeActivity, DetalhesLocal::class.java)
                i.putExtra("SERVICO_ID", s.id)
                startActivity(i)
            }
        }

        updateCardsAndMap("Todos")
    }

    private fun setupFilters() {
        val chipGroup = binding.chipGroup
        val categories = listOf("Todos", "Favoritos", "Restaurantes", "Shoppings", "Lojas", "Parques")

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
            googleMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(-3.71722, -38.54306), 13f
                )
            )
        }
    }

    private fun fetchServices() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val sharedPref = getSharedPreferences("TravoApp", Context.MODE_PRIVATE)
                val token = sharedPref.getString("token", null)

                val api = if (!token.isNullOrEmpty())
                    RetrofitService.getTravoServiceAPIWithToken(token)
                else RetrofitService.getTravoServiceAPI()

                val response = api.listarServicos()

                if (response.isSuccessful) {
                    allServices = response.body() ?: emptyList()
                    allServices.forEach { servico ->
                        try {
                            val anexoResponse = api.getProfilePic("servicos", servico.id)
                            if (anexoResponse.isSuccessful) {
                                servico.fotoPerfilUrl = anexoResponse.body()?.urlPublica
                            }
                        } catch (_: Exception) {}
                    }
                } else allServices = emptyList()

                withContext(Dispatchers.Main) { updateCardsAndMap("Todos") }

            } catch (e: Exception) {
                allServices = emptyList()
                withContext(Dispatchers.Main) { updateCardsAndMap("Todos") }
            }
        }
    }

    private fun updateCardsAndMap(selectedType: String) {
        val container = binding.cardContainer
        container.removeAllViews()
        googleMap?.clear()
        markerList.clear()
        selectedMarker = null

        val typeMap = mapOf(
            "Todos" to null,
            "Favoritos" to "favoritos",
            "Restaurantes" to "restaurant",
            "Lojas" to "store",
            "Shoppings" to "shopping",
            "Parques" to "park"
        )

        val dbValue = typeMap[selectedType]
        val filtered = when (dbValue) {
            null -> allServices
            "favoritos" -> allServices.filter { it.isFavorito }
            else -> allServices.filter {
                it.tipo?.equals(dbValue, ignoreCase = true) == true
            }
        }

        for (service in filtered) {
            val cardView = LayoutInflater.from(this)
                .inflate(R.layout.card_item_layout, container, false)

            val title = cardView.findViewById<TextView>(R.id.cardTitle)
            val address = cardView.findViewById<TextView>(R.id.cardAddress)
            val image = cardView.findViewById<ImageView>(R.id.cardImage)
            val likeButton = cardView.findViewById<ImageButton>(R.id.likeButton)
            val ratingContainer = cardView.findViewById<LinearLayout>(R.id.ratingContainer)
            val tvRating = cardView.findViewById<TextView>(R.id.tvRating)
            val tvTotalRatings = cardView.findViewById<TextView>(R.id.tvTotalRatings)

            tvRating.text = String.format("%.1f", service.mediaAvaliacao ?: 0.0)
            tvTotalRatings.text = "(${service.totalAvaliacoes ?: 0})"
            title.text = service.nome
            address.text = service.endereco ?: "Endereço não informado"

            Glide.with(this)
                .load(service.fotoPerfilUrl ?: service.imagemCapaUrl)
                .into(image)

            likeButton.setImageResource(
                if (service.isFavorito) R.drawable.ic_heart_filled else R.drawable.ic_heart_border
            )

            likeButton.setOnClickListener {
                service.isFavorito = !service.isFavorito
                likeButton.setImageResource(
                    if (service.isFavorito) R.drawable.ic_heart_filled else R.drawable.ic_heart_border
                )
            }

            // ---- CLICK NO CARD INTEIRO (exceto elementos ignorados) ----
            cardView.setOnClickListener {

                // Acha o marker correspondente
                val marker = markerList.find { m ->
                    val t = m.tag as? ServicoListagemResponse
                    t?.id == service.id
                }

                if (marker != null) {
                    selectedMarker = marker

                    googleMap?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(marker.position, 16f)
                    )

                    marker.showInfoWindow()
                }
            }

            ratingContainer.setOnClickListener {
                val i = Intent(this, DetalhesLocal::class.java)
                i.putExtra("SERVICO_ID", service.id)
                startActivity(i)
            }

            title.setOnClickListener {
                val intent = Intent(this, DetalhesLocal::class.java)
                intent.putExtra("SERVICO_ID", service.id)
                startActivity(intent)
            }

            container.addView(cardView)

            service.lat?.let { lat ->
                service.lng?.let { lng ->
                    val marker = googleMap?.addMarker(
                        MarkerOptions()
                            .position(LatLng(lat, lng))
                            .icon(createMarkerIconWithTitle(service.nome, service.tipo))
                            .anchor(0.5f, 1f)
                    )
                    marker?.tag = service
                    if (marker != null) markerList.add(marker)
                }
            }
        }
    }

    private fun updateCardsAndMapByService(service: ServicoListagemResponse) {
        val container = binding.cardContainer
        container.removeAllViews()
        googleMap?.clear()
        markerList.clear()
        selectedMarker = null

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
            .load(service.fotoPerfilUrl ?: service.imagemCapaUrl)
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

            // Agora procura na markerList
            val marker = markerList.find { m ->
                val t = m.tag as? ServicoListagemResponse
                t?.id == service.id
            }

            if (marker != null) {
                selectedMarker = marker

                googleMap?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(marker.position, 16f)
                )

                marker.showInfoWindow()
            }
        }

        title.setOnClickListener {
            val intent = Intent(this, DetalhesLocal::class.java)
            intent.putExtra("SERVICO_ID", service.id)
            startActivity(intent)
        }

        container.addView(cardView)

        service.lat?.let { lat ->
            service.lng?.let { lng ->
                val marker = googleMap?.addMarker(
                    MarkerOptions()
                        .position(LatLng(lat, lng))
                        .icon(createMarkerIconWithTitle(service.nome, service.tipo))
                        .anchor(0.5f, 1f)
                )
                marker?.tag = service
                marker?.showInfoWindow()
            }
        }
    }

    private fun setupLinkToListaServicos() {
        val tvLink = findViewById<TextView>(R.id.tvLink)
        tvLink.paintFlags = tvLink.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        tvLink.setTextColor(getColor(R.color.teal_700))
        tvLink.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    com.example.applicationtravo.ui.listaServicos.ListaServicos::class.java
                )
            )
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
            markerList.clear()
            selectedMarker = null
            return
        }

        val first = filtered.first()
        first.lat?.let { lat ->
            first.lng?.let { lng ->
                googleMap?.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                        LatLng(lat, lng), 14f
                    )
                )
            }
        }

        val container = binding.cardContainer
        container.removeAllViews()
        googleMap?.clear()
        markerList.clear()
        selectedMarker = null

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
                .load(service.fotoPerfilUrl ?: service.imagemCapaUrl)
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
                    val marker = googleMap?.addMarker(
                        MarkerOptions()
                            .position(LatLng(lat, lng))
                            .icon(createMarkerIconWithTitle(service.nome, service.tipo))
                            .anchor(0.5f, 1f)
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
                        googleMap?.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(lat, lng), 17f
                            )
                        )
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

    private fun createMarkerIconWithTitle(text: String, type: String?): BitmapDescriptor {
        val view = LayoutInflater.from(this).inflate(R.layout.map_marker, null)
        val tv = view.findViewById<TextView>(R.id.tvTitle)
        val pin = view.findViewById<ImageView>(R.id.imgPin)
        pin.setImageResource(getMarkerIconByType(type))
        tv.text = text
        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth,
            view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun getMarkerIconByType(type: String?): Int {
        return when (type?.lowercase()) {
            "restaurant" -> R.drawable.ic_restaurant_marker
            "store" -> R.drawable.ic_store_marker
            "park" -> R.drawable.ic_smile_marker
            "shopping" -> R.drawable.ic_shopping_marker
            else -> R.drawable.ic_default_marker
        }
    }

    private fun loadInfoWindowImage(url: String, marker: Marker, imageView: ImageView) {

        val cached = infoWindowImageCache[url]
        if (cached != null) {
            imageView.setImageBitmap(cached)
            marker.showInfoWindow()
            return
        }

        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    infoWindowImageCache[url] = resource
                    imageView.setImageBitmap(resource)
                    marker.showInfoWindow()
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    override fun onResume() { super.onResume(); mapView.onResume() }
    override fun onPause() { super.onPause(); mapView.onPause() }
    override fun onDestroy() { super.onDestroy(); mapView.onDestroy() }
    override fun onLowMemory() { super.onLowMemory(); mapView.onLowMemory() }
}
