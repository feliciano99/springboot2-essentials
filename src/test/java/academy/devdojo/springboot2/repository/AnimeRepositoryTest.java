package academy.devdojo.springboot2.repository;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import academy.devdojo.springboot2.domain.Anime;
import academy.devdojo.springboot2.util.AnimeCreator;

@DataJpaTest
@DisplayName(value = "Tests for Anime Repository")
class AnimeRepositoryTest {
	@Autowired
	private AnimeRepository animeRepository;

	@Test
	@DisplayName(value = "Save persists anime when Successful")
	void save_Persistence_WhenSuccessful() {
		Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

		Anime animeSaved = this.animeRepository.save(animeToBeSaved);

		org.assertj.core.api.Assertions.assertThat(animeSaved).isNotNull();

		org.assertj.core.api.Assertions.assertThat(animeSaved.getId()).isNotNull();

		org.assertj.core.api.Assertions.assertThat(animeSaved.getName()).isEqualTo(animeToBeSaved.getName());
	}

	@Test
	@DisplayName(value = "Save update anime when Successful")
	void save_UpdateAnime_WhenSuccessful() {
		Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

		Anime animeSaved = this.animeRepository.save(animeToBeSaved);

		animeSaved.setName("naturo 2");

		Anime animeUpdated = this.animeRepository.save(animeSaved);

		org.assertj.core.api.Assertions.assertThat(animeUpdated).isNotNull();

		org.assertj.core.api.Assertions.assertThat(animeUpdated.getId()).isNotNull();

		org.assertj.core.api.Assertions.assertThat(animeUpdated.getName()).isEqualTo(animeSaved.getName());
	}

	@Test
	@DisplayName(value = "Delete removes anime when Successful")
	void delete_RemovesAnime_WhenSuccessful() {
		Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

		Anime animeSaved = this.animeRepository.save(animeToBeSaved);

		this.animeRepository.delete(animeSaved);

		java.util.Optional<Anime> animeOptional = this.animeRepository.findById(animeSaved.getId());

		org.assertj.core.api.Assertions.assertThat(animeOptional).isEmpty();

	}

	@Test
	@DisplayName(value = "Find By Name return List of animes when Successful")
	void findByName_ReturnListOfAnime_WhenSuccessful() {
		Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

		Anime animeSaved = this.animeRepository.save(animeToBeSaved);

		String name = animeSaved.getName();

		List<Anime> animes = this.animeRepository.findByName(name);

		org.assertj.core.api.Assertions.assertThat(animes).isNotEmpty();

		org.assertj.core.api.Assertions.assertThat(animes).contains(animeSaved);

	}

	@Test
	@DisplayName(value = "Find By Name return Empty List when no anime is found")
	void findByName_ReturnEmptyList_WhenAnimeIsNotFound() {
		List<Anime> animes = this.animeRepository.findByName("NotFound");

		org.assertj.core.api.Assertions.assertThat(animes).isEmpty();
	}

}
