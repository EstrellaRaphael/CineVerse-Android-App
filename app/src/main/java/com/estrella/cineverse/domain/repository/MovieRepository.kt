package com.estrella.cineverse.domain.repository

import com.estrella.cineverse.domain.model.Movie

interface MovieRepository {
    suspend fun getPopularMovies(): List<Movie>
}