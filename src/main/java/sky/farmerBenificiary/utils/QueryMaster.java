package sky.farmerBenificiary.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Component
@Data
@Slf4j
public class QueryMaster {


	StringBuffer logString = null;
	ResultSet rs = null;
	PreparedStatement prepstmt = null;

	private int totalRows;

	private int startRow;

	private int endRow;
	public int[] batchUpdateInsert(String query, List<List<Object>> paramList, Connection con) {

		int[] updateinsertResultBatch = new int[paramList.size()];
		boolean conToBeClosed = false;
		int counter = 0;
		try {
			if (!Utils.isNeitherNullNorEmpty(con) || con.isClosed()) {
				conToBeClosed = true;
			}
			logString = new StringBuffer(" DBUser query:  | " + query)
					.append("  with values ::");
			prepstmt = con.prepareStatement(query);

			for (List<Object> param : paramList) {

				if (param != null) {
					int q = param.size();
					for (int j = 1; j <= q; j++) {
						Object temp = param.get(j - 1);
						logString.append(temp).append(",");
						prepstmt.setObject(j, temp);
					}
					++counter;
				}
				//log.info(logString.toString());
				prepstmt.addBatch();
			}

			if (counter == paramList.size()) {
				updateinsertResultBatch = prepstmt.executeBatch();
			}
		//log.info(logString.toString());
		} catch (Exception e) {
			updateinsertResultBatch = new int[] {};
			//log.error("Exception while batchUpdateInsert : " + e.getMessage());
			//log.error("Exception", e);
		} finally {
			try {
				if (conToBeClosed) {
					if (con != null) {
						con.close();
					}
				}
				if (prepstmt != null) {
					prepstmt.close();
				}
				if (paramList != null)
					paramList.clear();
				paramList = null;
				query = null;
			} catch (SQLException e) {
				//log.error("Exception", e);
			}
		}
		return updateinsertResultBatch;
	}
	public synchronized String changeQuery(String query) {

		try {
			if (query.contains("nvl") || query.contains("NVL")) {
				query = query.replaceAll("nvl", "coalesce");
				query = query.replaceAll("NVL", "coalesce");
			}

			if (query.contains("sysdate") || query.contains("SYSDATE")) {
				query = query.replaceAll("sysdate", "current_timestamp");
				query = query.replaceAll("SYSDATE", "current_timestamp");
			}

			if (query.contains("trunc") || query.contains("TRUNC")) {
				query = query.replaceAll("TRUNC", "date");
				query = query.replaceAll("trunc", "date");
			}

			if (query.contains("(15/1440)")) {
				query = query.replaceAll("(15/1440)", "(15 * interval '1 minute')");
			}

		} catch (Exception e) {
			
			//log.error("Exception while changeQuery for query : " + query);
			//log.error("Exception while changeQuery : " + e.getMessage());
		}
	//	log.info("Exit from changeQuery : " + query);
		return query;
	}

	/**
	 * This method closes preparedStatment variable, used for clearing resource.
	 */
	public void closeStatement() {
		try {
			if (prepstmt != null)
				prepstmt.close();
		} catch (SQLException e) {
			//log.error("Exception", e);
		}
	}

	public int getEndRow() {
		return endRow;
	}

	public int getStartRow() {
		return startRow;
	}

	public int getTotalRows() {
		return totalRows;
	}

	/**
	 * This method is used to run 'select' queries.
	 * 
	 * @param query The select query which needs to be run on the database
	 * @param param List of objects which form the parameters passed in the query
	 * @param con   Database connection object
	 * @param db    Defines which DB needs to be accessed
	 * @param log   Variable of log4j, used for logging into respective classes
	 * @return rs - ResultSet obtained after running the query
	 * @author Vikram
	 * @throws SQLException
	 */

	public int getTotalRows(String query, List<Object> param, Connection con) throws SQLException {
		int totalRows = 0;
		query = "Select count(*) From ( " + query + " )";

		logString = new StringBuffer(" DBUser query:  | " + query)
				.append("  with values ::");
		prepstmt = con.prepareStatement(query);

		if (Utils.isNeitherNullNorEmpty(param)) {
			int q = param.size();
			for (int j = 1; j <= q; j++) {
				Object temp = param.get(j - 1);
				logString.append(temp).append("|");
				prepstmt.setObject(j, temp);
			}
		}

		//log.info(logString.toString() + "[QueryStartTime]=" + System.currentTimeMillis());

		rs = prepstmt.executeQuery();
		if (rs.next()) {
			totalRows = rs.getInt(1);
		}
		return totalRows;
	}

	public boolean IsExist(String query, List<Object> param, Connection con) {
		long executionStartTime = 0;
		long executionEndTime = 0;
		boolean Result = false;
		try {
			logString = new StringBuffer(" DBUser query:  | " + query)
					.append("  with values ::");
			prepstmt = con.prepareStatement(query);

			if (Utils.isNeitherNullNorEmpty(param)) {
				int q = param.size();
				for (int j = 1; j <= q; j++) {
					Object temp = param.get(j - 1);
					logString.append(temp).append("|");
					prepstmt.setObject(j, temp);
				}
			}

			/*
			 * if(log != null){ if(LogMstr.LOG_LEVEL_2){ log.info(logString); } }
			 */
			executionStartTime = System.currentTimeMillis();
			//log.info(logString.toString());
			rs = prepstmt.executeQuery();
			if (rs.next()) {
				Result = true;
			}
			executionEndTime = System.currentTimeMillis();
		} catch (Exception e) {
			//log.error("Exception", e);
			executionEndTime = System.currentTimeMillis();
			/*
			 * if(log != null){ log.error("Exception while select : " + e.getMessage());
			 * }
			 */
		} finally {
			try {
				// log.info("Query Execution time :" +
				// (executionEndTime-executionStartTime)+ " milliseconds");
				if (param != null)
					param.clear();
				param = null;
				query = null;
			} catch (Exception e) {
				//log.error("Exception", e);
			}
		}
		return Result;
	}

	public synchronized ResultSet select(String query, List<Object> param, Connection con) throws SQLException {
		try {
			// ResultSet rs1=null;
			logString = new StringBuffer(" DBUser query:  | " + query)
					.append("  with values ::");
			if (endRow == 0) {
				prepstmt = con.prepareStatement(query);
			}

			PreparedStatement prepstmt1 = null;
			PreparedStatement prepstmt2 = null;
			// pagination
			if (endRow > 0) {
				String TotalRowQuery = "Select count(*) From ( " + query + " ) r";
				prepstmt1 = con.prepareStatement(TotalRowQuery);
				String paginationQuery = "SELECT *  FROM ( Select AA.*,row_number() over() RR   From ( " + query
						+ ") AA) SS WHERE RR > " + (startRow - 1) + " AND RR<=" + endRow + " ";
				prepstmt2 = con.prepareStatement(paginationQuery);
			}
			if (Utils.isNeitherNullNorEmpty(param)) {
				int q = param.size();
				for (int j = 1; j <= q; j++) {
					Object temp = param.get(j - 1);
					logString.append(temp).append("|");

					if (endRow > 0) {
						prepstmt1.setObject(j, temp);
						prepstmt2.setObject(j, temp);
					} else {
						prepstmt.setObject(j, temp);
					}
				}
			}
			if (endRow > 0) {

				ResultSet rs_1 = prepstmt1.executeQuery();
				rs_1 = prepstmt1.executeQuery();
				if (rs_1.next()) {
					totalRows = rs_1.getInt(1);
				}
				rs = prepstmt2.executeQuery();
			} else {
				rs = prepstmt.executeQuery();
			}

			//log.info(logString.toString() + "[QueryStartTime]=" + System.currentTimeMillis());

		} catch (Exception e) {
			
//			if (log != null) {
//				log.error("Exception while select : " + e.getMessage());
//			}
		} finally {
			//log.info(logString.toString() + "[QueryEndTime]=" + System.currentTimeMillis());
			try {
				if (param != null)
					param.clear();
				param = null;
				query = null;
			} catch (Exception e) {
				//log.error("Exception", e);
			}
		}
		return rs;
	}

	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}

	/**
	 * @param query The select query which needs to be run on the database
	 * @param param List of objects which form the parameters passed in the query
	 * @param db    Defines which DB needs to be accessed
	 * @param log   Variable of log4j, used for logging into respective classes
	 * @return updateinsertResult (int) - number of rows which got inserted/updated
	 */
	public int updateInsert(String query, List<Object> param, Connection con) {

		int updateinsertResult = 0;
		boolean conToBeClosed = false;
		try {
			if (!Utils.isNeitherNullNorEmpty(con) || con.isClosed()) {
				conToBeClosed = true;
				//con = NemlDBUtil.getConnection();
			}
			logString = new StringBuffer(" DBUser query:  | " + query)
					.append("  with values ::");
			prepstmt = con.prepareStatement(query);

			if (param != null) {
				int q = param.size();
				for (int j = 1; j <= q; j++) {
					Object temp = param.get(j - 1);
					logString.append(temp).append(",");
					prepstmt.setObject(j, temp);
				}
			}

			//log.info(logString.toString());

			updateinsertResult = prepstmt.executeUpdate();

			logString.setLength(0);
			logString.append("result of updateInsert ::").append(updateinsertResult);
			//log.info(logString.toString());
		} catch (Exception e) {
			updateinsertResult = -1;
			//log.error("Exception while updateInsert : " + e.getMessage());

		} finally {
			try {
				if (conToBeClosed) {
					if (con != null) {
						con.close();
					}
				}
				if (prepstmt != null) {
					prepstmt.close();
				}
				if (param != null)
					param.clear();
				param = null;
				query = null;
			} catch (SQLException e) {
				//log.error("Exception", e);
			}
		}
		return updateinsertResult;
	}

	public int updateInsert(String query, List<Object> param, Connection con, boolean insertFlag) {

		int updateinsertResult = 0;
		boolean conToBeClosed = false;
		try {
			if (!Utils.isNeitherNullNorEmpty(con) || con.isClosed()) {
				conToBeClosed = true;
			}
			logString = new StringBuffer(" DBUser query:  | " + query)
					.append("  with values ::");
			prepstmt = con.prepareStatement(query);

			if (param != null) {
				int q = param.size();
				for (int j = 1; j <= q; j++) {
					Object temp = param.get(j - 1);
					logString.append(temp).append(",");
					prepstmt.setObject(j, temp);
				}
			}

			//log.info(logString.toString());

			updateinsertResult = prepstmt.executeUpdate();
			if (insertFlag && updateinsertResult == 0) {
				updateinsertResult = 1;
			}
			if (!insertFlag) {
				//log.info("Update Result : " + updateinsertResult);
			}
			logString.setLength(0);
			logString.append("result of updateInsert ::").append(updateinsertResult);
			//log.info(logString.toString());
		} catch (Exception e) {
			updateinsertResult = -1;
			//log.error("Exception while updateInsert : " + e.getMessage());
			 e.printStackTrace();
		} finally {
			try {
				if (conToBeClosed) {
					if (con != null) {
						con.close();
					}
				}
				if (prepstmt != null) {
					prepstmt.close();
				}
				if (param != null)
					param.clear();
				param = null;
				query = null;
			} catch (SQLException e) {
				//log.error("Exception", e);
			}
		}
		return updateinsertResult;
	}


}
