package com.angelpuentesdevv.screenmatch.principal;


import com.angelpuentesdevv.screenmatch.model.DatosSerie;
import com.angelpuentesdevv.screenmatch.model.DatosTemporadas;
import com.angelpuentesdevv.screenmatch.service.ConsumoAPI;
import com.angelpuentesdevv.screenmatch.service.ConvierteDatos;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Principal {

    private Scanner lectura = new Scanner(System.in);

    private ConsumoAPI consumoApi = new ConsumoAPI();

    private ConvierteDatos convierteDatos = new ConvierteDatos();

    private final String URL_BASE = "https://www.omdbapi.com/?t=";

    private final String API_KEY = "&apikey=8e4ab839";

    public void muestraMenu() {

        //Menú de interacción con el usuario
        System.out.println("Por favor, escribe el nombre de la serie que deseas ver: ");
        var nombreSerie = lectura.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        var datos = convierteDatos.obtenerDatos(json, DatosSerie.class);

        //Buscar los datos de todas las temporadas
        List<DatosTemporadas> temporadas = new ArrayList<>();
        for (int i = 1; i <= datos.totalTemporadas(); i++) {
            json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + "&Season=" + i + API_KEY);
            var datosTemporadas = convierteDatos.obtenerDatos(json, DatosTemporadas.class);
            temporadas.add(datosTemporadas);
        }
        //temporadas.forEach(System.out::println);

        //Mostrar solo el título de los episodios para las temporadas
        /*for (int i = 0; i < datos.totalTemporadas(); i++) {
            List<DatosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
            for (int j = 0; j < episodiosTemporada.size(); j++) {
                System.out.println(episodiosTemporada.get(j).titulo());
            }
        }*/

        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));
    }
}
