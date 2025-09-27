package com.estrella.cineverse.data.remote

import com.estrella.cineverse.data.remote.dto.MovieListResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface TmdbApi {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("page") page: Int = 1,
        @Query("language") language: String = "pt-BR"
    ): MovieListResponseDto

    companion object {
        const val BASE_URL = "https://api.themoviedb.org/3/"
    }
}