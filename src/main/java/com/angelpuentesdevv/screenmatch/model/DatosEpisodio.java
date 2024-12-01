package com.angelpuentesdevv.screenmatch.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DatosEpisodio (

    @JsonAlias("Title") String titulo,

    @JsonAlias("Episode") Integer episodio,

    @JsonAlias("Rated") String clasificacion,

    @JsonAlias("Released") String fechaLanzamiento,

    @JsonAlias("imdbRating") String evaluacion

        ){

}
