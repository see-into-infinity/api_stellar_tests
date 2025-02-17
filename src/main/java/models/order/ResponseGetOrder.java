package models.order;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseGetOrder{

	@JsonProperty("total")
	private Integer total;

	@JsonProperty("success")
	private boolean success;

	@JsonProperty("orders")
	private List<OrdersItem> orders;

	@JsonProperty("totalToday")
	private Integer totalToday;


}