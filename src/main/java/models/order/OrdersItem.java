package models.order;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrdersItem{

	@JsonProperty("createdAt")
	private String createdAt;

	@JsonProperty("number")
	private Integer number;

	@JsonProperty("name")
	private String name;

	@JsonProperty("ingredients")
	private List<String> ingredients;

	@JsonProperty("_id")
	private String id;

	@JsonProperty("status")
	private String status;

	@JsonProperty("updatedAt")
	private String updatedAt;

}