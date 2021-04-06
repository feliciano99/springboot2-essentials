package academy.devdojo.springboot2.requests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AnimePostRequestBody {
	@javax.validation.constraints.NotEmpty(message = "The anime name cannot be empty")
	@Schema(description = "This is the Anime's name", example = "Tensei Shittara Slime Datta Kan", required = true)
	private String name;
	
	@JsonCreator
	public AnimePostRequestBody(@JsonProperty("name") String name) {
		this.name = name;
	}
}
