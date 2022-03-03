package academy.devdojo.springboot2.repositories;

import academy.devdojo.springboot2.domain.Anime;

import java.util.List;

public interface AnimeRepository {
    List<Anime> listAll();
}
