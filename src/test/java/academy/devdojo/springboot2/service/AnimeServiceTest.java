package academy.devdojo.springboot2.service;

import java.util.List;
import java.util.Optional;
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
import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.exception.BadRequestException;
import academy.devdojo.springboot2.repository.AnimeRepository;
import academy.devdojo.springboot2.util.AnimeCreator;
import academy.devdojo.springboot2.util.AnimePostRequestBodyCreator;
import academy.devdojo.springboot2.util.AnimePutRequestBodyCreator;

@ExtendWith(SpringExtension.class)
class AnimeServiceTest {

	@InjectMocks
	private AnimeService animeService;
	@Mock
	private AnimeRepository animeRepositoryMock;

	@BeforeEach
	void setUp() {
		PageImpl<Anime> animePage = new PageImpl<Anime>(List.of(AnimeCreator.createValidAnime()));
		BDDMockito.when(animeRepositoryMock.findAll(ArgumentMatchers.any(PageRequest.class))).thenReturn(animePage);

		BDDMockito.when(animeRepositoryMock.findAll()).thenReturn(List.of(AnimeCreator.createValidAnime()));

		BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong()))
				.thenReturn(Optional.of(AnimeCreator.createValidAnime()));

		BDDMockito.when(animeRepositoryMock.findByName(ArgumentMatchers.anyString()))
				.thenReturn(List.of(AnimeCreator.createValidAnime()));

		BDDMockito.when(animeRepositoryMock.save(ArgumentMatchers.any(Anime.class)))
				.thenReturn(AnimeCreator.createValidAnime());

		BDDMockito.doNothing().when(animeRepositoryMock).delete(ArgumentMatchers.any(Anime.class));
	}

	@Test
	@DisplayName("List returns list of anime inside page object when successful")
	void list_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
		String expectedName = AnimeCreator.createValidAnime().getName();

		Page<Anime> animePage = animeService.listAll(PageRequest.of(1, 1));

		Assertions.assertThat(animePage).isNotNull();

		Assertions.assertThat(animePage.toList()).hasSize(1);

		Assertions.assertThat(animePage.toList().get(0).getName()).isEqualTo(expectedName);

	}

	@Test
	@DisplayName("listAll returns list of anime inside page object when successful")
	void listAll_ReturnsListOfAnimesInsidePageObject_WhenSuccessful() {
		String expectedName = AnimeCreator.createValidAnime().getName();

		List<Anime> animes = animeService.listAllNonPageable();

		Assertions.assertThat(animes).isNotNull().isNotEmpty().hasSize(1);

		Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);

	}

	@Test
	@DisplayName("findById returns anime when successful")
	void findById_ReturnAnimesInsidePageObject_WhenSuccessful() {
		Long expectId = AnimeCreator.createValidAnime().getId();

		Anime anime = animeService.findByIdOrThrowBadRequestException(1);

		Assertions.assertThat(anime).isNotNull();

		Assertions.assertThat(anime.getId()).isNotNull().isEqualTo(expectId);

	}

	@Test
	@DisplayName("findByIdOrThrowBadRequestException throws BadRequestException when anime is not found")
	void findByIdOrThrowBadRequestException_ReturnsAnime_WhenAnimeIsNotFound() {
		BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyLong())).thenReturn(Optional.empty());

		Assertions.assertThatExceptionOfType(BadRequestException.class)
				.isThrownBy(() -> this.animeService.findByIdOrThrowBadRequestException(1));

	}

	@Test
	@DisplayName("findByName returns a List of animes when successful")
	void findByName_ReturnListOfAnimesInsidePageObject_WhenSuccessful() {
		String expectedName = AnimeCreator.createValidAnime().getName();

		List<Anime> animes = animeService.findByName("anime");

		Assertions.assertThat(animes).isNotNull().isNotEmpty().hasSize(1);

		Assertions.assertThat(animes.get(0).getName()).isEqualTo(expectedName);

	}

	@Test
	@DisplayName("save returns anime when successful")
	void save_ReturnAnime_WhenSuccessful() {
		Anime anime = animeService.save(AnimePostRequestBodyCreator.createAnimePostRequestBody());

		Assertions.assertThat(anime).isNotNull().isEqualTo(AnimeCreator.createValidAnime());
	}

	@Test
	@DisplayName("replace updates anime when successful")
	void replace_UpdatesAnime_WhenSuccessful() {
		Assertions.assertThatCode(() -> animeService.replace(AnimePutRequestBodyCreator.createAnimePutRequestBody()))
				.doesNotThrowAnyException();

	}

	@DisplayName("delete removes anime when successful")
	void delete_RemovesAnime_WhenSuccessful() {
		Assertions.assertThatCode(() -> animeService.delete(1)).doesNotThrowAnyException();

	}
}
