package academy.devdojo.springboot2.integration;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.repositories.AnimeRepository;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimePostRequestBodyCreator;
import academy.devdojo.springboot2.wrapper.PageableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AnimeControllerIT {
    @Autowired
    private AnimeRepository animeRepository;
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    @DisplayName("list Return List of Animes Inside Page Object When Successful")
    void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        String expectedName = savedAnime.getName();

        PageableResponse<Anime> animePage = testRestTemplate.exchange("/animes", HttpMethod.GET, null,
                new ParameterizedTypeReference<PageableResponse<Anime>>() {
                }).getBody();

        Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);

        Assertions.assertThat(animePage.toList())
                .isNotEmpty()
                .hasSize(1)
                .isNotNull();

    }

    @Test
    @DisplayName("listAllNonPageable Return List of Animes When Successful")
    void listAllNonPageable_ReturnsListOfAnimes_WhenSuccessful() {

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());
        String expectedName = savedAnime.getName();

        List<Anime> animes = testRestTemplate.exchange("/animes/all", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        Assertions.assertThat(animes)
                .isNotEmpty()
                .hasSize(1)
                .isNotNull();

        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("findById Return Anime When Successful")
    void findById_ReturnsAnime_WhenSuccessful() {

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        Long expectedId = savedAnime.getId();

        Anime anime = testRestTemplate.getForObject("/animes/{id}", Anime.class, expectedId);

        Assertions.assertThat(anime).isNotNull();

        Assertions.assertThat(anime.getId()).isEqualTo(expectedId);

    }

    @Test
    @DisplayName("findByName Return a List of Animes When Successful")
    void findByName_ReturnsListOfAnimes_WhenSuccessful() {

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        String expectedName = savedAnime.getName();

        String url = String.format("/animes/find?name=%s", expectedName);

        List<Anime> animes = testRestTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        Assertions.assertThat(animes).isNotNull()
                .isNotEmpty()
                .hasSize(1);

        Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);

    }

    @Test
    @DisplayName("findByName Return an Empty List of Animes When Anime is Not Found")
    void findByName_ReturnsAnEmptyListOfAnimes_WhenAnimeIsNotFound() {

        List<Anime> animes = testRestTemplate.exchange("/animes/find?name=Naruto", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        Assertions.assertThat(animes).isNotNull()
                .isEmpty();

    }

    @Test
    @DisplayName("save Return Anime When Successful")
    void save_ReturnsAnime_WhenSuccessful() {

        AnimePostRequestBody animePostRequestBody = AnimePostRequestBodyCreator.createAnimePostRequestBody();

        ResponseEntity<Anime> anime = testRestTemplate.postForEntity("/animes", animePostRequestBody, Anime.class);

        Assertions.assertThat(anime).isNotNull();

        Assertions.assertThat(anime.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Assertions.assertThat(anime.getBody()).isNotNull();

        Assertions.assertThat(anime.getBody().getId()).isNotNull();

    }

    @Test
    @DisplayName("replace Updates Anime When Successful")
    void replace_UpdatesAnime_WhenSuccessful() {

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        savedAnime.setName("New Name");

        ResponseEntity<Void> anime = testRestTemplate.exchange("/animes",
                HttpMethod.PUT,
                new HttpEntity<>(savedAnime),
                Void.class);

        Assertions.assertThat(anime.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Assertions.assertThat(anime).isNotNull();

    }

    @Test
    @DisplayName("delete Removes Anime When Successful")
    void delete_RemovesAnime_WhenSuccessful() {

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        ResponseEntity<Void> anime = testRestTemplate.exchange("/animes/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                savedAnime.getId());

        Assertions.assertThat(anime.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Assertions.assertThat(anime).isNotNull();

    }
}