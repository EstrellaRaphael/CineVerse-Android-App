package com.estrella.cineverse.domain.usecase

import com.estrella.cineverse.domain.model.Movie
import com.estrella.cineverse.domain.repository.MovieRepository

class GetPopularMoviesUseCase(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(): List<Movie> {
        return movieRepository.getPopularMovies()
    }
}