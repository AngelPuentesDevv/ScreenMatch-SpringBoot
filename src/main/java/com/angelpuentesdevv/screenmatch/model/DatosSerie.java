package com.angelpuentesdevv.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosSerie(

        @JsonAlias("Title") String titulo,

        @JsonAlias("Year") String fechaLanzamiento,

        @JsonAlias("totalSeasons") Integer totalTemporadas,

        @JsonAlias("Country") String pais,

        @JsonAlias("imdbRating") String evaluacion,

        @JsonAlias("Genre") String genero

        ) {
}
