/**
 * 
 */
package sky.farmerBenificiary.payloads;

import lombok.Data;

/**
 * 
 */
@Data
public class Pagination {
	
	private int startRow;
	private int endRow;
	private int totalRows;
	private String totalNoOfRows;
}
