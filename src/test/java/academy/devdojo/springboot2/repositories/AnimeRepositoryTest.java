package academy.devdojo.springboot2.repositories;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.util.AnimeCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("Tests for Anime Repository")
class AnimeRepositoryTest {

    @Autowired
    private AnimeRepository animeRepository;

    @Test
    @DisplayName("Save Persist Anime When Successful")
    void save_PersistAnime_WhenSuccessful() {
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
        Anime animeSaved = this.animeRepository.save(animeToBeSaved);
        Assertions.assertThat(animeSaved).isNotNull();
        Assertions.assertThat(animeSaved.getId()).isNotNull();
        Assertions.assertThat(animeSaved.getName()).isEqualTo(animeToBeSaved.getName());
    }

    @Test
    @DisplayName("Save Updates Anime When Successful")
    void save_UpdatesAnime_WhenSuccessful() {
        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
        Anime animeSaved = this.animeRepository.save(animeToBeSaved);

        animeSaved.setName("Dragon Ball");

        Anime animeUpdated = this.animeRepository.save(animeSaved);

        Assertions.assertThat(animeSaved).isNotNull();
        Assertions.assertThat(animeSaved.getId()).isNotNull();
        Assertions.assertThat(animeUpdated.getName()).isEqualTo(animeSaved.getName());
    }

    @Test
    @DisplayName("Delete Removes Anime When Successful")
    void delete_RemovesAnime_WhenSuccessful() {

        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
        Anime animeSaved = this.animeRepository.save(animeToBeSaved);

        this.animeRepository.delete(animeSaved);

        Optional<Anime> animeOptional = this.animeRepository.findById(animeSaved.getId());
        Assertions.assertThat(animeOptional.isEmpty()).isTrue();

    }

    @Test
    @DisplayName("Find By Name Returns List Of Animes When Successful")
    void FindByName_ReturnsListOfAnime_WhenSuccessful() {

        Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
        Anime animeSaved = this.animeRepository.save(animeToBeSaved);

        List<Anime> animeList = this.animeRepository.findByName(animeSaved.getName());

        Assertions.assertThat(animeList.contains(animeSaved));
        Assertions.assertThat(animeList).isNotEmpty();

    }

    @Test
    @DisplayName("Find By Name Returns Empty List When No Anime Is Found")
    void FindByName_ReturnsEmptyList_WhenAnimeIsNotFound() {

        List<Anime> animeList = this.animeRepository.findByName("Test");

        Assertions.assertThat(animeList).isEmpty();

    }

    @Test
    @DisplayName("Save Throws ConstraintViolationException When Name is empty")
    void save_ThrowsConstraintViolationException_WhenNameIsEmpty() {

        Anime anime =  new Anime();

        Assertions.assertThatThrownBy(() -> this.animeRepository.save(anime))
                .isInstanceOf(ConstraintViolationException.class);

    }

}