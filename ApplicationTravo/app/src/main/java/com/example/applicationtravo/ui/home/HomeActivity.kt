package com.example.applicationtravo.ui.home

import android.os.Bundle
import android.content.Intent
import android.graphics.Paint
import android.util.Log
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.applicationtravo.R
import com.example.applicationtravo.databinding.ActivityHomeBinding
import com.example.applicationtravo.models.ServicoListagemResponse
import com.example.applicationtravo.retrofit.RetrofitService
import com.google.android.material.chip.Chip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import com.example.applicationtravo.ui.listaServicos.ListaServicos

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var mMap: MapView
    private lateinit var controller: IMapController
    private lateinit var mMyLocationOverlay: MyLocationNewOverlay

    private var allServices = listOf<ServicoListagemResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupMap()
        setupFilters()
        fetchServices()
        setupLinkToListaServicos()
    }

    private fun setupMap() {
        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        )

        mMap = binding.osmmap
        mMap.setMultiTouchControls(true)
        controller = mMap.controller
        controller.setZoom(13.0)
        controller.setCenter(GeoPoint(-3.71722, -38.54306))

        mMyLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), mMap)
        mMyLocationOverlay.enableMyLocation()
        mMap.overlays.add(mMyLocationOverlay)
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
        }
    }

    private fun fetchServices() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val api = RetrofitService.getTravoServiceAPI()

                val response = api.listarServicos()

                if (response.isSuccessful) {
                    val services = response.body() ?: emptyList()
                    allServices = if (services.isNotEmpty()) services else getMockedServices()

                    withContext(Dispatchers.Main) {
                        updateCardsAndMap("Todos")
                    }
                } else {
                    Log.e("HomeActivity", "Erro listarServicos(): ${response.code()}")
                    allServices = getMockedServices()
                    withContext(Dispatchers.Main) {
                        updateCardsAndMap("Todos")
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeActivity", "Erro ao buscar serviços: ${e.message}")
                allServices = getMockedServices()
                withContext(Dispatchers.Main) {
                    updateCardsAndMap("Todos")
                }
            }
        }
    }

    private fun getMockedServices(): List<ServicoListagemResponse> {
        Log.w("HomeActivity", "Usando dados mocados (sem conexão ou erro do backend).")

        return listOf(
            ServicoListagemResponse(
                id = 1,
                nome = "Bulls",
                endereco = "R. Cel. Jucá, 700",
                tipo = "restaurant",
                lat = -3.7378847,
                lng = -38.4913999,
                imagemCapaUrl = "https://ozgnuchcsmoedkyhrqip.supabase.co/storage/v1/object/public/travo/servicos/perfil/bulls_aldeota_perfil.jpg",
                mediaAvaliacao = 4.8,
                totalAvaliacoes = 945
            ),
            ServicoListagemResponse(
                id = 2,
                nome = "Barney’s Burguer",
                endereco = "R. Monsenhor Otávio de Castro, 901",
                tipo = "restaurant",
                lat = -3.7507870,
                lng = -38.5263363,
                imagemCapaUrl = "https://ozgnuchcsmoedkyhrqip.supabase.co/storage/v1/object/public/travo/servicos/perfil/barneys_burguer_perfil.jpg",
                mediaAvaliacao = 4.9,
                totalAvaliacoes = 346
            ),
            ServicoListagemResponse(
                id = 3,
                nome = "Pizza Hut",
                endereco = "Av. Beira Mar, 2500",
                tipo = "restaurant",
                lat = -3.7241545,
                lng = -38.5027959,
                imagemCapaUrl = "https://ozgnuchcsmoedkyhrqip.supabase.co/storage/v1/object/public/travo/servicos/perfil/pizza_hut_perfil.jpg",
                mediaAvaliacao = 4.3,
                totalAvaliacoes = 535
            )
        )
    }

    private fun updateCardsAndMap(selectedType: String) {
        val container = binding.cardContainer
        container.removeAllViews()

        // Remove apenas Markers do mapa
        mMap.overlays.removeIf { it is Marker }

        val filtered = if (selectedType == "Todos") {
            allServices
        } else {
            allServices.filter { svc ->
                val tipo = svc.tipo ?: ""
                tipo.equals(selectedType, ignoreCase = true)
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

            val mediaAvaliacao = service.mediaAvaliacao ?: 0.0   // Float ou Double
            val totalAvaliacoes = service.totalAvaliacoes ?: 0   // Int

            tvRating.text = String.format("%.1f", mediaAvaliacao)
            tvTotalRatings.text = "($totalAvaliacoes)"

            title.text = service.nome
            address.text = service.endereco ?: "Endereço não informado"

            val imageUrl = service.imagemCapaUrl
            if (!imageUrl.isNullOrBlank()) {
                Glide.with(this)
                    .load(imageUrl)
                    .into(image)
            } else {
                image.setImageDrawable(null)
            }

            var liked = false
            likeButton.setImageResource(R.drawable.ic_heart_border)

            likeButton.setOnClickListener {
                liked = !liked
                if (liked) {
                    likeButton.setImageResource(R.drawable.ic_heart_filled)
                } else {
                    likeButton.setImageResource(R.drawable.ic_heart_border)
                }
            }

            container.addView(cardView)

            val lat = service.lat
            val lng = service.lng
            if (lat != null && lng != null) {
                val marker = Marker(mMap)
                marker.position = GeoPoint(lat, lng)
                marker.title = service.nome
                marker.setOnMarkerClickListener { _, _ ->
                    showMarkerPopup(service)
                    true
                }
                mMap.overlays.add(marker)
            } else {
                Log.w("HomeActivity", "Serviço ${service.id} sem lat/lng — ajuste backend/model.")
            }
        }

        mMap.invalidate()
    }

    private fun setupLinkToListaServicos() {
        val tvLink = findViewById<TextView>(R.id.tvLink)
        tvLink.paintFlags = tvLink.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        tvLink.setTextColor(getColor(R.color.teal_700))
        tvLink.setOnClickListener {
            val intent = Intent(this, ListaServicos::class.java)
            startActivity(intent)
        }
    }

    private fun showMarkerPopup(service: ServicoListagemResponse) {
        // TODO: inflar layout customizado para exibir imagem + nome + endereço
    }
}
