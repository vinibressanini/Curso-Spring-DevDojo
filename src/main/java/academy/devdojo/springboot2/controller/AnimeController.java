package academy.devdojo.springboot2.controller;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.service.AnimesService;
import academy.devdojo.springboot2.util.DateUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@RestController
@Log4j2
@RequestMapping("animes")
public class AnimeController {
    private final DateUtil dateUtil;
    private final AnimesService animesService;

    @GetMapping
    public List<Anime> list () {
        log.info(dateUtil.formatLocalDateTimeToDatabaseStyle(LocalDateTime.now()));
        return animesService.listAll();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<Anime> findById (@PathVariable long id) {
        return ResponseEntity.ok(animesService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Anime> save(@RequestBody Anime anime) {
        return new ResponseEntity<>(animesService.save(anime),HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) {
        animesService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping
    public ResponseEntity<Anime> replace(@RequestBody Anime anime) {
        animesService.replace(anime);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
