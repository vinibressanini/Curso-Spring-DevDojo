package academy.devdojo.springboot2.service;

import academy.devdojo.springboot2.domain.Anime;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnimesService {
    public List<Anime> listAll() {
        return List.of(new Anime(1L,"Naruto"),new Anime(2L,"DBZ"));
    }
}
