package academy.devdojo.springboot2.integration;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.domain.DevDojoUser;
import academy.devdojo.springboot2.repositories.AnimeRepository;
import academy.devdojo.springboot2.repositories.DevDojoUserRepository;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimePostRequestBodyCreator;
import academy.devdojo.springboot2.wrapper.PageableResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
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
    private DevDojoUserRepository devDojoUserRepository;

    @Autowired
    @Qualifier(value = "testRestTemplateRoleUser")
    private TestRestTemplate testRestTemplateRoleUser;

    @Autowired
    @Qualifier(value = "testRestTemplateRoleAdmin")
    private TestRestTemplate testRestTemplateRoleAdmin;

    private static final DevDojoUser USER = DevDojoUser.builder()
            .name("User for tests")
            .authorities("ROLE_USER")
            .username("user")
            .password("{bcrypt}$2a$10$oTe63jbZggfINlByTsAOD.eRRMe4HDdyNPOT6Zd5YrUHszgt8AgZm")
            .build();

    private static final DevDojoUser ADMIN = DevDojoUser.builder()
            .name("Admin for tests")
            .authorities("ROLE_ADMIN,ROLE_USER")
            .username("vini")
            .password("{bcrypt}$2a$10$oTe63jbZggfINlByTsAOD.eRRMe4HDdyNPOT6Zd5YrUHszgt8AgZm")
            .build();

    @TestConfiguration
    @Lazy
    static class Config {

        @Bean(name = "testRestTemplateRoleUser")
        public  TestRestTemplate testRestTemplateRoleUserCreator (@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("user","academy");
            return new TestRestTemplate(restTemplateBuilder);
        }

        @Bean(name = "testRestTemplateRoleAdmin")
        public  TestRestTemplate testRestTemplateRoleAdminCreator (@Value("${local.server.port}") int port) {
            RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                    .rootUri("http://localhost:" + port)
                    .basicAuthentication("vini","academy");
            return new TestRestTemplate(restTemplateBuilder);
        }

    }


    @Test
    @DisplayName("list Return List of Animes Inside Page Object When Successful")
    void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        DevDojoUser user = devDojoUserRepository.save(USER);

        String expectedName = savedAnime.getName();

        PageableResponse<Anime> animePage = testRestTemplateRoleUser.exchange("/animes", HttpMethod.GET, null,
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

        DevDojoUser user = devDojoUserRepository.save(USER);

        String expectedName = savedAnime.getName();

        List<Anime> animes = testRestTemplateRoleUser.exchange("/animes/all", HttpMethod.GET, null,
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

        DevDojoUser user = devDojoUserRepository.save(USER);

        Long expectedId = savedAnime.getId();

        Anime anime = testRestTemplateRoleUser.getForObject("/animes/{id}", Anime.class, expectedId);

        Assertions.assertThat(anime).isNotNull();

        Assertions.assertThat(anime.getId()).isEqualTo(expectedId);

    }

    @Test
    @DisplayName("findByName Return a List of Animes When Successful")
    void findByName_ReturnsListOfAnimes_WhenSuccessful() {

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        DevDojoUser user = devDojoUserRepository.save(USER);

        String expectedName = savedAnime.getName();

        String url = String.format("/animes/find?name=%s", expectedName);

        List<Anime> animes = testRestTemplateRoleUser.exchange(url, HttpMethod.GET, null,
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

        DevDojoUser user = devDojoUserRepository.save(USER);

        List<Anime> animes = testRestTemplateRoleUser.exchange("/animes/find?name=Naruto", HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Anime>>() {
                }).getBody();

        Assertions.assertThat(animes).isNotNull()
                .isEmpty();

    }

    @Test
    @DisplayName("save Return Anime When Successful")
    void save_ReturnsAnime_WhenSuccessful() {

        AnimePostRequestBody animePostRequestBody = AnimePostRequestBodyCreator.createAnimePostRequestBody();

        DevDojoUser user = devDojoUserRepository.save(USER);

        ResponseEntity<Anime> anime = testRestTemplateRoleUser.postForEntity("/animes", animePostRequestBody, Anime.class);

        Assertions.assertThat(anime).isNotNull();

        Assertions.assertThat(anime.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        Assertions.assertThat(anime.getBody()).isNotNull();

        Assertions.assertThat(anime.getBody().getId()).isNotNull();

    }

    @Test
    @DisplayName("replace Updates Anime When Successful")
    void replace_UpdatesAnime_WhenSuccessful() {

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        DevDojoUser user = devDojoUserRepository.save(USER);

        savedAnime.setName("New Name");

        ResponseEntity<Void> anime = testRestTemplateRoleUser.exchange("/animes",
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

        DevDojoUser user = devDojoUserRepository.save(ADMIN);

        ResponseEntity<Void> anime = testRestTemplateRoleAdmin.exchange("/animes/admin/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                savedAnime.getId());

        Assertions.assertThat(anime.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Assertions.assertThat(anime).isNotNull();

    }

    @Test
    @DisplayName("delete Returns 403 when user is not admin")
    void delete_Returns403_WhenUserIsNotAdmin() {

        Anime savedAnime = animeRepository.save(AnimeCreator.createAnimeToBeSaved());

        DevDojoUser user = devDojoUserRepository.save(USER);

        ResponseEntity<Void> anime = testRestTemplateRoleUser.exchange("/animes/admin/{id}",
                HttpMethod.DELETE,
                null,
                Void.class,
                savedAnime.getId());

        Assertions.assertThat(anime).isNotNull();

        Assertions.assertThat(anime.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

}