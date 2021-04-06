package academy.devdojo.springboot2.client;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import academy.devdojo.springboot2.domain.Anime;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SpringClient {

//	public static void main(String[] args) {
//		ResponseEntity<Anime> entity = new RestTemplate().getForEntity("http://localhost:8080/animes/5", Anime.class);
//		log.info("ENTITY" + entity);
//
//		ResponseEntity<List<Anime>> exchange = new RestTemplate().exchange("http://localhost:8080/animes/all",
//				HttpMethod.GET, null, new ParameterizedTypeReference<>() {
//				});
//		log.info("LIST" + exchange.getBody());
//
//		Anime kingdom = Anime.builder().name("kingdom").build();
//		ResponseEntity<Anime> kingdomSaved = new RestTemplate().exchange("http://localhost:8080/animes",
//				HttpMethod.POST, 
//				new HttpEntity<>(kingdom, createJsonHeader()), Anime.class);
//		
//		log.info("Saved anime {}", kingdomSaved);
//		
//		Anime animeToBeUpdated = kingdomSaved.getBody();
//		animeToBeUpdated.setName("Naruto");
//		
//		ResponseEntity<Void> animeUpdated = new RestTemplate().exchange("http://localhost:8080/animes",
//				HttpMethod.PUT, 
//				new HttpEntity<>(animeToBeUpdated, createJsonHeader()), Void.class);
//		
//		log.info("UPDATED");
//		
//		ResponseEntity<Void> animeDeleted = new RestTemplate().exchange("http://localhost:8080/animes/{id}",
//				HttpMethod.DELETE, 
//				null, 
//				Void.class,
//				animeToBeUpdated.getId());
//		
//		log.info("DELETED");
//
//	}
	
	private static org.springframework.http.HttpHeaders createJsonHeader() {
		org.springframework.http.HttpHeaders httpheaders = new org.springframework.http.HttpHeaders();
		httpheaders.setContentType(MediaType.APPLICATION_JSON);
		return httpheaders;
	}

}
