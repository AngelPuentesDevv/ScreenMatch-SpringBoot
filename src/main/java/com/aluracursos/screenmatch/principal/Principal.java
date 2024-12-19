package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.*;
import com.aluracursos.screenmatch.repository.SerieRepository;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=8e4ab839";
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosSerie> datosSeries = new ArrayList<>();
    private SerieRepository serieRepository;
    private List<Serie> series;
    Optional<Serie> serieBuscada;

    public Principal(SerieRepository serieRepository) {
        this.serieRepository = serieRepository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar series 
                    2 - Buscar episodios
                    3 - Mostrar series buscadas
                    4 - Buscar series por título
                    5 - Top 5 series
                    6 - Buscar series por categoría
                    7 - Buscar por total de temporadas
                    8 - Filtrar series por temporada y evaluación
                    9 - Buscar episodios por titulo 
                    10 - Top 5 episodios por serie 
                                  
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    mostrarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriesPorTitulo();
                    break;
                case 5:
                    buscarTop5Series();
                    break;
                case 6:
                    buscarSeriesPorCategoria();
                    break;
                case 7:
                    buscarSeriesPorTotalTemporada();
                    break;
                case 8:
                    filtrarSeriesPorTemporadaYEvaluacion();
                    break;
                case 9:
                    buscarEpisodiosPorTitulo();
                    break;
                case 10:
                    buscarTop5Episodios();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }

    }

    private DatosSerie getDatosSerie() {
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        System.out.println(json);
        DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
        return datos;
    }
    private void buscarEpisodioPorSerie() {
        mostrarSeriesBuscadas();
        System.out.println("Elije la serie en la siguiente lista: ");
        var nombreSerie = teclado.nextLine();

        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
                .findFirst();

        if (serie.isPresent()) {
            var serieEncontrada = serie.get();
            List<DatosTemporadas> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoApi.obtenerDatos(URL_BASE + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DatosTemporadas datosTemporada = conversor.obtenerDatos(json, DatosTemporadas.class);
                temporadas.add(datosTemporada);
            }
            temporadas.forEach(System.out::println);
            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());
            serieEncontrada.setEpisodios(episodios);
            serieRepository.save(serieEncontrada);
        }
    }
    private void buscarSerieWeb() {
        DatosSerie datos = getDatosSerie();
        Serie serie = new Serie(datos);
        serieRepository.save(serie);
        //datosSeries.add(datos);
        System.out.println(datos);
    }

    private void mostrarSeriesBuscadas() {
        series = serieRepository.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriesPorTitulo(){
        System.out.println("Escribe el nombre de la serie: ");
        var nombreSerie = teclado.nextLine();

        serieBuscada = serieRepository.findByTituloContainsIgnoreCase(nombreSerie);

        if (serieBuscada.isPresent()) {
            System.out.println("La serie buscada es: " + serieBuscada.get());
        } else {
            System.out.println("Serie no encontrada!");
        }
    }

    private void buscarTop5Series(){
        List<Serie> topSeries = serieRepository.findTop5ByOrderByEvaluacionDesc();
        topSeries.forEach(s -> System.out.println("Serie: " + s.getTitulo() + " Evaluación: " + s.getEvaluacion()));
    }

    private void buscarSeriesPorCategoria(){
        System.out.println("Escribe la categoria/género de la serie: ");
        var genero = teclado.nextLine();
        var categoria = Categoria.fromEspanol(genero);
        List<Serie> seriePorCategoria = serieRepository.findByGenero(categoria);
        System.out.println("Las series de la categoría " + genero);
        seriePorCategoria.forEach(System.out::println);
    }

    private void buscarSeriesPorTotalTemporada(){
        System.out.println("Digita la cantidad de temporadas: ");
        var temporadas = teclado.nextInt();
        List<Serie> seriePorTemporadas = serieRepository.findByTotalTemporadas(temporadas);
        seriePorTemporadas.forEach(System.out::println);
    }

    private void buscarSeriesPorEvaluacion(){
        System.out.println("Digita la evaluación deseada: ");
        var evaluacion = teclado.nextDouble();
        Optional<Serie> seriePorEvaluacion = serieRepository.findByEvaluacion(evaluacion);

        if (seriePorEvaluacion.isPresent()) {
            System.out.println("Las series encontradas son: " + seriePorEvaluacion.get());
        } else {
            System.out.println("Serie no encontrada");
        }
    }

    private void filtrarSeriesPorTemporadaYEvaluacion(){
        System.out.println("¿Cuántas temporadas desea? ");
        var totalTemporadas = teclado.nextInt();
        teclado.nextLine();
        System.out.println("¿Evaluación a partir de cuál valor? ");
        var evaluacion = teclado.nextDouble();
        teclado.nextLine();
        List<Serie> filtroSeries = serieRepository.seriesPorTemporadaYEvaluacion(totalTemporadas, evaluacion);
        System.out.println("*** Series Filtradas ***");
        filtroSeries.forEach(s ->
                System.out.println(s.getTitulo() + " - evaluación: " + s.getEvaluacion()));
    }

    private void buscarEpisodiosPorTitulo(){
        System.out.println("Escribe el nombre del episodio ");
        var nombreEpisodio = teclado.nextLine();

        List<Episodio> episodiosEncontrados = serieRepository.episodiosPorNombre(nombreEpisodio);
        episodiosEncontrados.forEach(e -> System.out.printf("Serie: %s Episodio %s Temporada %s Evaluación %s \n",
                e.getSerie(), e.getTitulo(), e.getTemporada(), e.getEvaluacion()));
    }

    private void buscarTop5Episodios(){
        buscarSeriesPorTitulo();

        if (serieBuscada.isPresent()) {
            Serie serie = serieBuscada.get();
            List<Episodio> topEpisodios = serieRepository.top5Episodios(serie);
            topEpisodios.forEach(e -> System.out.printf("Serie: %s Episodio %s Temporada %s Evaluación %s \n",
                    e.getSerie(), e.getTitulo(), e.getTemporada(), e.getEvaluacion()));
        }
    }
}

