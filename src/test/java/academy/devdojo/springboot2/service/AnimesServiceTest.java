package academy.devdojo.springboot2.service;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.exception.BadRequestException;
import academy.devdojo.springboot2.repositories.AnimeRepository;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimePostRequestBodyCreator;
import academy.devdojo.springboot2.util.AnimePutRequestBodyCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@DisplayName("Tests For Anime Services")
class AnimesServiceTest {

    @InjectMocks
    private AnimesService animesService;

    @Mock
    private AnimeRepository animeRepositoryMock;

    @BeforeEach
    void setUp() {
        PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));
        BDDMockito.when(animeRepositoryMock.findAll(ArgumentMatchers.any(PageRequest.class)))
                .thenReturn(animePage);

        BDDMockito.when(animeRepositoryMock.findAll())
                .thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(List.of(AnimeCreator.createValidAnime()));

        BDDMockito.when(animeRepositoryMock.save(ArgumentMatchers.any(Anime.class)))
                .thenReturn(AnimeCreator.createValidAnime());

        BDDMockito.doNothing().when(animeRepositoryMock).delete(ArgumentMatchers.any(Anime.class));
    }

    @Test
    @DisplayName("listAll Return List of Animes Inside Page Object When Successful")
    void listAll_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {

        String expectedName = AnimeCreator.createValidAnime().getName();
        Page<Anime> animePage = animesService.listAll(PageRequest.of(1, 1));

        Assertions.assertThat(animePage).isNotNull()
                .hasSize(1);

        Assertions.assertThat(animePage.toList()).isNotNull();

        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);


    }

    @Test
    @DisplayName("listAllNonPageable Return List of Animes When Successful")
    void listAllNonPageable_ReturnsListOfAnimes_WhenSuccessful() {

        String expectedName = AnimeCreator.createValidAnime().getName();
        List<Anime> animes = animesService.listAllNonPageable();

        Assertions.assertThat(animes).isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);


    }

    @Test
    @DisplayName("findById Return Anime When Successful")
    void findById_ReturnsAnime_WhenSuccessful() {

        Long expectedId = AnimeCreator.createValidAnime().getId();

        Anime anime = animesService.findByIdOrThrowBadRequestException(1);

        Assertions.assertThat(anime).isNotNull();

        Assertions.assertThat(anime.getId()).isEqualTo(expectedId);

    }

    @Test
    @DisplayName("findByName Return a List of Animes When Successful")
    void findByName_ReturnsListOfAnimes_WhenSuccessful() {

        String expectedName = AnimeCreator.createValidAnime().getName();
        List<Anime> animes = animesService.findByName("animes");

        Assertions.assertThat(animes).isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("findByName Return an Empty List of Animes When Anime is Not Found")
    void findByName_ReturnsAnEmptyListOfAnimes_WhenAnimeIsNotFound() {

        BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString()))
                .thenReturn(Collections.emptyList());

        List<Anime> animes = animesService.findByName("animes");

        Assertions.assertThat(animes).isNotNull()
                .isEmpty();

    }

    @Test
    @DisplayName("save Return Anime When Successful")
    void save_ReturnsAnime_WhenSuccessful() {

        Long expectedId = AnimeCreator.createValidAnime().getId();

        Anime anime = animesService.save(AnimePostRequestBodyCreator.createAnimePostRequestBody());

        Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectedId);

    }

    @Test
    @DisplayName("replace Updates Anime When Successful")
    void replace_UpdatesAnime_WhenSuccessful() {


        Assertions.assertThatCode(() -> animesService.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody())
        ).doesNotThrowAnyException();

    }

    @Test
    @DisplayName("delete Removes Anime When Successful")
    void delete_RemovesAnime_WhenSuccessful() {


        Assertions.assertThatCode(() -> animesService.delete(1)).doesNotThrowAnyException();

    }

    @Test
    @DisplayName("findById Throws BadRequestException When Anime Is Not Found")
    void findById_ThrowsBadRequestException_WhenAnimeIsNotFound() {

        BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong()))
                .thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> animesService.findByIdOrThrowBadRequestException(1))
                .isInstanceOf(BadRequestException.class);

    }

}