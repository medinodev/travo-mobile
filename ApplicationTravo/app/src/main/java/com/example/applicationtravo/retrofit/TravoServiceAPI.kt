package com.example.applicationtravo.retrofit

import com.example.applicationtravo.models.AnexoResponse
import com.example.applicationtravo.models.CupomResponse
import com.example.applicationtravo.models.LocalDetalheResponse
import com.example.applicationtravo.models.LoginRequest
import com.example.applicationtravo.models.LoginResponse
import com.example.applicationtravo.models.RegistroRequest
import com.example.applicationtravo.models.ReviewResponse
import com.example.applicationtravo.models.ServicoListagemResponse
import com.example.applicationtravo.models.UsuariosResponse
import com.example.applicationtravo.models.ChangePasswordRequest
import com.example.applicationtravo.models.CupomClienteResponse
import com.example.applicationtravo.models.FavoriteResponse
import com.example.applicationtravo.models.ValidarCupomRequest
import com.example.applicationtravo.models.ValidarCupomResponse

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.PATCH
import retrofit2.http.DELETE
import retrofit2.http.Query

interface TravoServiceAPI {

    @POST("usuarios")
    suspend fun registrar(@Body registroRequest: RegistroRequest): Response<Unit>

    @POST("usuario/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>

    @GET("usuarios")
    suspend fun getAllUsers():Response<List<UsuariosResponse>>

    @GET("usuarios/{id}")
    suspend fun getUserById(@Path("id") id: Int): Response<UsuariosResponse>

    @PATCH("usuarios/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body request: com.example.applicationtravo.models.UsuarioUpdateRequest
    ): Response<UsuariosResponse>

    @PATCH("usuarios/{id}/senha")
    suspend fun changePassword(
        @Path("id") id: Int,
        @Body request: ChangePasswordRequest
    ): Response<Unit>

    // SERVIÇOS
    @GET("servicos")
    suspend fun listarServicos(): Response<List<ServicoListagemResponse>>

    @GET("servicos/{id}")
    suspend fun obterServicoPorId(
        @Path("id") idServico: Int
    ): Response<LocalDetalheResponse>

    // CUPONS
    @POST("cupons/{id}/claim")
    suspend fun claimCupom(@Path("id") cupomId: Int): Response<CupomClienteResponse>

    @POST("cupons/validar")
    suspend fun validarCupom(@Body request: ValidarCupomRequest): Response<ValidarCupomResponse>


    @GET("cuponsall")
    suspend fun listarTodosCupons(): Response<List<CupomResponse>>

    // filtrar por serviço:

    @GET("cupons")
    suspend fun listarCuponsDoServico(
        @Query("servicoId") idServico: Int? = null
    ): Response<List<CupomResponse>>

    // AVALIAÇÕES
    // Lista de avaliações; filtrar por serviço, use servicoId
    @GET("avaliacao")
    suspend fun listarAvaliacoesDoServico(
        @Query("servicoId") idServico: Int? = null
    ): Response<List<ReviewResponse>>

    // FAVORITOS
    // Retorna os serviços favoritados do usuário autenticado (identificado pelo token JWT)
    // A tabela favoritos relaciona usuario_id com estabelecimento_id (servico_id)
    @GET("favoritos")
    suspend fun listarFavoritos(): Response<List<ServicoListagemResponse>>

    // Adiciona um favorito: cria registro na tabela favoritos com usuario_id (do token) e estabelecimento_id (servicoId)
    @POST("favoritos/{servicoId}")
    suspend fun adicionarFavorito(
        @Path("servicoId") idServico: Int // estabelecimento_id no banco
    ): Response<Unit>

    // Remove um favorito: deleta registro da tabela favoritos para o usuário autenticado e serviço especificado
    @DELETE("favoritos/{servicoId}")
    suspend fun removerFavorito(
        @Path("servicoId") idServico: Int // estabelecimento_id no banco
    ): Response<Unit>

    @GET(value = "favoritos")
    suspend fun getAllFavoritos(): Response<List<FavoriteResponse>>

    @GET("anexos/perfil/{entidade_tipo}/{entidade_id}")
    suspend fun getProfilePic(
        @Path("entidade_tipo") entidadeTipo: String,
        @Path("entidade_id") entidadeId: Int
    ): Response<AnexoResponse>

}