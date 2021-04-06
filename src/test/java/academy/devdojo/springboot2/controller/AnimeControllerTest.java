package academy.devdojo.springboot2.controller;

import java.util.List;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.requests.AnimePostRequestBody;
import academy.devdojo.springboot2.requests.AnimePutRequestBody;
import academy.devdojo.springboot2.service.AnimeService;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimePostRequestBodyCreator;
import academy.devdojo.springboot2.util.AnimePutRequestBodyCreator;

@ExtendWith(SpringExtension.class)
class AnimeControllerTest {

	@InjectMocks
	private AnimeController animeController;
	@Mock
	private AnimeService animeServiceMock;

	@BeforeEach
	void setUp() {
		PageImpl<Anime> animePage = new PageImpl<Anime>(List.of(AnimeCreator.createValidAnime()));
		BDDMockito.when(animeServiceMock.listAll(ArgumentMatchers.any())).thenReturn(animePage);

		BDDMockito.when(animeServiceMock.listAllNonPageable()).thenReturn(List.of(AnimeCreator.createValidAnime()));

		BDDMockito.when(animeServiceMock.findByIdOrThrowBadRequestException(ArgumentMatchers.anyLong()))
				.thenReturn(AnimeCreator.createValidAnime());

		BDDMockito.when(animeServiceMock.findByName(ArgumentMatchers.anyString()))
				.thenReturn(List.of(AnimeCreator.createValidAnime()));

		BDDMockito.when(animeServiceMock.save(ArgumentMatchers.any(AnimePostRequestBody.class)))
				.thenReturn(AnimeCreator.createValidAnime());

		BDDMockito.doNothing().when(animeServiceMock).replace(ArgumentMatchers.any(AnimePutRequestBody.class));
		
		BDDMockito.doNothing().when(animeServiceMock).delete(ArgumentMatchers.anyLong());
	}

	@Test
	@DisplayName("List returns list of anime inside page object when successful")
	void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
		String expectedName = AnimeCreator.createValidAnime().getName();

		Page<Anime> animePage = animeController.list(null).getBody();

		Assertions.assertThat(animePage).isNotNull();

		Assertions.assertThat(animePage.toList()).hasSize(1);

		Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);

	}

	@Test
	@DisplayName("listAll returns list of anime inside page object when successful")
	void listAll_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
		String expectedName = AnimeCreator.createValidAnime().getName();

		List<Anime> animes = animeController.listAll().getBody();

		Assertions.assertThat(animes).isNotNull().isNotEmpty().hasSize(1);

		Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);

	}

	@Test
	@DisplayName("findById returns anime when successful")
	void findById_ReturnAnimesInsidePageObject_WhenSuccessful() {
		Long expectId = AnimeCreator.createValidAnime().getId();

		Anime anime = animeController.findById(1).getBody();

		Assertions.assertThat(anime).isNotNull();

		Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectId);

	}

	@Test
	@DisplayName("findByName returns a List of animes when successful")
	void findByName_ReturnListOfAnimesInsidePageObject_WhenSuccessful() {
		String expectedName = AnimeCreator.createValidAnime().getName();

		List<Anime> animes = animeController.findByName("anime").getBody();

		Assertions.assertThat(animes).isNotNull().isNotEmpty().hasSize(1);

		Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);

	}

	@Test
	@DisplayName("save returns anime when successful")
	void save_ReturnAnime_WhenSuccessful() {
		Anime anime = animeController.save(AnimePostRequestBodyCreator.createAnimePostRequestBody()).getBody();

		Assertions.assertThat(anime).isNotNull().isEqualTo(AnimeCreator.createValidAnime());
	}

	@Test
	@DisplayName("replace updates anime when successful")
	void replace_UpdatesAnime_WhenSuccessful() {
		Assertions
				.assertThatCode(
						() -> animeController.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody()).getBody())
				.doesNotThrowAnyException();

		ResponseEntity<Void> entity = animeController.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody());

		Assertions.assertThat(entity).isNotNull();
		Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

	}
	
	@DisplayName("delete removes anime when successful")
	void delete_RemovesAnime_WhenSuccessful() {
		Assertions
				.assertThatCode(
						() -> animeController.delete(1))
				.doesNotThrowAnyException();

		ResponseEntity<Void> entity = animeController.delete(1);

		Assertions.assertThat(entity).isNotNull();
		Assertions.assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

	}

}
