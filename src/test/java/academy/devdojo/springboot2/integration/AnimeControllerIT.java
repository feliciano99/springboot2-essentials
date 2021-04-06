package academy.devdojo.springboot2.integration;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.domain.DevDojoUser;
import academy.devdojo.springboot2.repository.AnimeRepository;
import academy.devdojo.springboot2.repository.DevDojoUserRepository;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.wrapper.PageableResponse;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase
class AnimeControllerIT {

	@Autowired
	@Qualifier(value = "testRestTemplateRoleUser")
	private TestRestTemplate testRestTemplateRoleUser;
	@Autowired
	@Qualifier(value = "testRestTemplateRoleAdmin")
	private TestRestTemplate testRestTemplateRoleAdmin;
	@MockBean
	private AnimeRepository animeRepositoryMock;
	@Autowired
	private DevDojoUserRepository devDojoUserRepository;
	private static final DevDojoUser USER = DevDojoUser.builder()
			.name("DevDojo Academy")
			.username("devdojo")
			.password("{bcrypt}$2a$10$18roLBczwkAEfj01sB0w2eIRGd09AxVA/iQ6Lc8SOZkUXFqYnz0Tq")
			.authorities("ROLE_USER")
			.build();
	
	private static final DevDojoUser ADMIN = DevDojoUser.builder()
			.name("Feliciano Carlos Gomes")
			.username("feliciano")
			.password("{bcrypt}$2a$10$18roLBczwkAEfj01sB0w2eIRGd09AxVA/iQ6Lc8SOZkUXFqYnz0Tq")
			.authorities("ROLE_USER,ROLE_ADMIN")
			.build();

	@TestConfiguration
	@Lazy
	static class Config {
		@Bean(name = "testRestTemplateRoleUser")
		public TestRestTemplate testRestTemplateRoleUserCreator(@Value("${local.server.port}") int port) {
			RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
					.rootUri("http://localhost:"+port)
					.basicAuthentication("devdojo", "academy");

			return new TestRestTemplate(restTemplateBuilder);
		}
		@Bean(name = "testRestTemplateRoleAdmin")
		public TestRestTemplate testRestTemplateRoleAdminCreator(@Value("${local.server.port}") int port) {
			RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
					.rootUri("http://localhost:"+port)
					.basicAuthentication("feliciano", "academy");

			return new TestRestTemplate(restTemplateBuilder);
		}
	}

	@BeforeEach
	public void setUp() {
		PageImpl<Anime> animePage = new PageImpl<>(List.of(AnimeCreator.createValidAnime()));
		BDDMockito.when(animeRepositoryMock.findAll(ArgumentMatchers.any(PageRequest.class))).thenReturn(animePage);

		BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong()))
				.thenReturn(Optional.of(AnimeCreator.createValidAnime()));

		BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString()))
				.thenReturn(List.of(AnimeCreator.createValidAnime()));

		BDDMockito.when(animeRepositoryMock.save(AnimeCreator.createAnimeToBeSaved()))
				.thenReturn(AnimeCreator.createValidAnime());

		BDDMockito.doNothing().when(animeRepositoryMock).delete(ArgumentMatchers.any(Anime.class));

		BDDMockito.when(animeRepositoryMock.save(AnimeCreator.createValidAnime()))
				.thenReturn(AnimeCreator.createValidUpdatedAnime());
	}

	@Test
	@DisplayName("listAll returns a pageable list of animes when successful")
	public void listAll_ReturnListOfAnimesInsidePageObject_WhenSuccessful() {		
		devDojoUserRepository.save(USER);
		String expectedName = AnimeCreator.createValidAnime().getName();

		//@formatter:off
        Page<Anime> animePage = testRestTemplateRoleUser.exchange("/animes", HttpMethod.GET,
            null, new ParameterizedTypeReference<PageableResponse<Anime>>() {}).getBody();
        //@formatter:on

		Assertions.assertThat(animePage).isNotNull();

		Assertions.assertThat(animePage.toList()).isNotEmpty();

		Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);
	}

	@Test
	@DisplayName("findById returns an anime when successful")
	public void findById_ReturnListOfAnimesInsidePageObject_WhenSuccessful() {
		Long expectedId = AnimeCreator.createValidAnime().getId();
		devDojoUserRepository.save(USER);

		Anime anime = testRestTemplateRoleUser.getForObject("/animes/1", Anime.class);

		Assertions.assertThat(anime).isNotNull();

		Assertions.assertThat(anime.getId()).isNotNull();

		Assertions.assertThat(anime.getId()).isEqualTo(expectedId);
	}

	@Test
	@DisplayName("findByName returns a list of animes when successful")
	public void findByName_ReturnListOfAnimes_WhenSuccessful() {
		String expectedName = AnimeCreator.createValidAnime().getName();
		devDojoUserRepository.save(USER);

		//@formatter:off
        List<Anime> animeList = testRestTemplateRoleUser.exchange("/animes/find?name='tensei'",
            HttpMethod.GET, null, new ParameterizedTypeReference<List<Anime>>() {
            }).getBody();
        //@formatter:on

		Assertions.assertThat(animeList).isNotNull();

		Assertions.assertThat(animeList).isNotEmpty();

		Assertions.assertThat(animeList.get(0).getName()).isEqualTo(expectedName);
	}

	@Test
	@DisplayName("save creates an anime when successful")
	public void save_CreatesAnime_WhenSuccessful() {
		Long expectedId = AnimeCreator.createValidAnime().getId();
		devDojoUserRepository.save(ADMIN);

		Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
		animeToBeSaved.setId(1l);

		Anime anime = testRestTemplateRoleUser
				.exchange("/animes", HttpMethod.POST, createJsonHttpEntity(animeToBeSaved), Anime.class).getBody();

		Assertions.assertThat(animeToBeSaved).isNotNull();

		Assertions.assertThat(animeToBeSaved.getId()).isNotNull();

		Assertions.assertThat(animeToBeSaved.getId()).isEqualTo(expectedId);
	}

	@Test
	@DisplayName("delete removes the anime when successful")
	public void delete_RemovesAnime_WhenSuccessful() {
		devDojoUserRepository.save(ADMIN);

		ResponseEntity<Void> responseEntity = testRestTemplateRoleAdmin.exchange("/animes/admin/1", HttpMethod.DELETE, null,
				Void.class);

		Assertions.assertThat(responseEntity).isNotNull();

		Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		Assertions.assertThat(responseEntity.getBody()).isNull();
	}
	
	@Test
	@DisplayName("delete removes the anime when successful")
	public void delete_Returns403WhenUserIsNotAdmin() {
		devDojoUserRepository.save(USER);

		ResponseEntity<Void> responseEntity = testRestTemplateRoleUser.exchange("/animes/admin/1", HttpMethod.DELETE, null,
				Void.class);

		Assertions.assertThat(responseEntity).isNotNull();

		Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

		Assertions.assertThat(responseEntity.getBody()).isNull();
	}

	@Test
	@DisplayName("update save updated anime when successful")
	public void update_SaveUpdatedAnime_WhenSuccessful() {
		Anime validAnime = AnimeCreator.createValidAnime();
		devDojoUserRepository.save(USER);

		ResponseEntity<Void> responseEntity = testRestTemplateRoleUser.exchange("/animes", HttpMethod.PUT,
				createJsonHttpEntity(validAnime), Void.class);

		Assertions.assertThat(responseEntity).isNotNull();

		Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		Assertions.assertThat(responseEntity.getBody()).isNull();
	}

	private HttpEntity<Anime> createJsonHttpEntity(Anime anime) {
		return new HttpEntity<>(anime, createJsonHeader());
	}

	private static HttpHeaders createJsonHeader() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		return httpHeaders;
	}
}