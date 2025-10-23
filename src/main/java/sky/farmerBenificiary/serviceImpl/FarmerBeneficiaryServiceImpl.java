package sky.farmerBenificiary.serviceImpl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;
import sky.farmerBenificiary.payloads.ApiResponse;
import sky.farmerBenificiary.payloads.FarmerPayload;
import sky.farmerBenificiary.repository.FarmerBeneficiaryDao;
import sky.farmerBenificiary.service.FarmerBeneficiaryService;
import sky.farmerBenificiary.utils.ErrorConstants;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

@Slf4j
@Service
public class FarmerBeneficiaryServiceImpl implements FarmerBeneficiaryService {

	@Value("${file.upload-dir}")
	private String uploadDir;

	@Autowired
	FarmerBeneficiaryDao oFarmerBeneficiaryDao;

	@Autowired
	@Qualifier("procprd")
	DataSource dataSource;

	@Override
	public List<FarmerPayload> getStatewiseFarmerDetails(FarmerPayload requestBean) {
		log.info("Entering into {} and getStatewiseFarmerDetails() with userId {}",
				FarmerBeneficiaryServiceImpl.class.getName(), "Test");
		List<FarmerPayload> oList = new ArrayList<>();
		Connection con = null;
		try {
			con = dataSource.getConnection();
			oList = oFarmerBeneficiaryDao.getBeneficiaryList(requestBean, con);
		} catch (Exception e) {
			log.error("Error getting getStatewiseFarmerDetails list");
			e.printStackTrace();
		} finally {
			try {
				if (con != null && !con.isClosed()) {
					con.close();
				}
			} catch (Exception e2) {
				log.error("Exception getting getStatewiseFarmerDetails in conection close");
			}
		}
		log.info("Exsting into {} and getStatewiseFarmerDetails() with userId {}",
				FarmerBeneficiaryServiceImpl.class.getName(), "Test");
		return oList;
	}

	@Override
	public ApiResponse InsertBenfDetails(FarmerPayload requestBean) {
		ApiResponse response = new ApiResponse();
		Connection con=null;
		try {
			con = dataSource.getConnection();
			int res =oFarmerBeneficiaryDao.InsertRecordsBenf(requestBean, con);
			if(res>0) {
				response.setResponseCode(ErrorConstants.SUCCESS_FAILED_CODE.SUCCESS.value);
				response.setResponseMessage("Record Save successfully");
			}else {
				response.setResponseCode(ErrorConstants.SUCCESS_FAILED_CODE.FAILED.value);
				response.setResponseMessage("Record Save failed");
			}
			
		} catch (Exception e) {
			log.error("Error getting getStatewiseFarmerDetails list");
			e.printStackTrace();
		} finally {
			try {
				if (con != null && !con.isClosed()) {
					con.close();
				}
			} catch (Exception e2) {
				log.error("Exception getting getStatewiseFarmerDetails in conection close");
			}
		}

		return response;
	}
	@Override
	public ApiResponse modifyBenfDetails(FarmerPayload requestBean) {
		ApiResponse response = new ApiResponse();
		Connection con=null;
		try {
			con = dataSource.getConnection();
			int res =oFarmerBeneficiaryDao.updateModifedRecordsBenf(requestBean, con);
			if(res>0) {
				response.setResponseCode(ErrorConstants.SUCCESS_FAILED_CODE.SUCCESS.value);
				response.setResponseMessage("Record Update successfully");
			}else {
				response.setResponseCode(ErrorConstants.SUCCESS_FAILED_CODE.FAILED.value);
				response.setResponseMessage("Record Update failed");
			}
			
		} catch (Exception e) {
			log.error("Error getting getStatewiseFarmerDetails list");
			e.printStackTrace();
		} finally {
			try {
				if (con != null && !con.isClosed()) {
					con.close();
				}
			} catch (Exception e2) {
				log.error("Exception getting getStatewiseFarmerDetails in conection close");
			}
		}

		return response;
	}

	@Override
	public ApiResponse approveBenfDetails(List<FarmerPayload> requestBeanList) {
		ApiResponse response = new ApiResponse();
		Connection con=null;
		long count=0;
		try {
			con = dataSource.getConnection();
			if (requestBeanList == null || requestBeanList.isEmpty()) {
				response.setResponseCode(ErrorConstants.SUCCESS_FAILED_CODE.FAILED.value);
				response.setResponseMessage("No records provided for approval");
				return response;
			}
			for (FarmerPayload farmerPayload : requestBeanList) {
				int res =oFarmerBeneficiaryDao.updateStatusBenf(farmerPayload, con);
				if(res>0) {
					count++;
				}
			}
			if(requestBeanList.size()==count) {
				response.setResponseCode(ErrorConstants.SUCCESS_FAILED_CODE.SUCCESS.value);
				response.setResponseMessage("Records Approved successfully");
			}else {
				response.setResponseCode(ErrorConstants.SUCCESS_FAILED_CODE.FAILED.value);
				response.setResponseMessage("Records Approved Failed");
			}
		} catch (Exception e) {
			log.error("Error getting getStatewiseFarmerDetails list");
			e.printStackTrace();
		} finally {
			try {
				if (con != null && !con.isClosed()) {
					con.close();
				}
			} catch (Exception e2) {
				log.error("Exception getting getStatewiseFarmerDetails in conection close");
			}
		}

		return response;
	}

	@Override
	public ApiResponse uploadFarmerDetails(MultipartFile file) {
		ApiResponse response = new ApiResponse();
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			// 1. Check if file is empty
			if (file.isEmpty()) {
				response.setResponseCode(ErrorConstants.SUCCESS_FAILED_CODE.FAILED.value);
				response.setResponseMessage("Please upload a file");
				return response;
			}
			// 2. Validate Excel file type (xls, xlsx, mimetype check)
			String fileName = file.getOriginalFilename();
			if (fileName == null || !(fileName.endsWith(".xls") || fileName.endsWith(".xlsx")
					|| file.getContentType().equals("application/vnd.ms-excel") || file.getContentType()
							.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))) {

				response.setResponseCode(ErrorConstants.SUCCESS_FAILED_CODE.FAILED.value);
				response.setResponseMessage("Upload valid excel file");
				return response;
			}

			// 3. Generate Random TXN folder
			String txnId = "TXN" + String.format("%010d", new Random().nextInt(1_000_000_000));
			Path txnFolder = Paths.get(uploadDir, txnId);
			if (!Files.exists(txnFolder)) {
				Files.createDirectories(txnFolder);
			}

			// 4. Save file to directory
			Path filePath = txnFolder.resolve(fileName);
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

			response.setResponseCode(ErrorConstants.SUCCESS_FAILED_CODE.SUCCESS.value);
			response.setResponseMessage("File uploaded successfully");
			// response.setFileName(fileName);
			// response.setUploadFilePath(filePath.toString());
			// response.setDownloadFilePath(filePath.toString());

			// Step 3: Read Excel data
			try (InputStream is = Files.newInputStream(filePath); Workbook workbook = new XSSFWorkbook(is)) {

				Sheet sheet = workbook.getSheetAt(0);
				Iterator<Row> rows = sheet.iterator();
				if (rows.hasNext())
					rows.next(); // skip header

				conn = dataSource.getConnection();
				conn.setAutoCommit(false);

				String sql = """
						    INSERT INTO farmer_beneficiary_details (
						        state, state_code, district, district_code, taluka, taluka_code,
						        village, village_code, beneficiary_id, beneficiary_name,
						        shop_no, rc_no, unique_no_old, aadhaar_number, aadhaar_reference_no,
						        first_name, middle_name, surname, full_name,
						        bank_name, account_number, ifsc_code, account_holder_name,
						        total_members, amount, created_by, modified_by,
						        created_at, modified_at
						    ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),now())
						    ON CONFLICT (beneficiary_id)
						    DO UPDATE SET
						        state = EXCLUDED.state,
						        district = EXCLUDED.district,
						        taluka = EXCLUDED.taluka,
						        village = EXCLUDED.village,
						        beneficiary_name = EXCLUDED.beneficiary_name,
						        bank_name = EXCLUDED.bank_name,
						        amount = EXCLUDED.amount,
						        modified_at = now(),
						        modified_by = EXCLUDED.modified_by
						""";

				pstmt = conn.prepareStatement(sql);

				while (rows.hasNext()) {
					Row row = rows.next();
					pstmt.setString(1, getCellValue(row.getCell(0))); // state
					pstmt.setString(2, getCellValue(row.getCell(1))); // state_code
					pstmt.setString(3, getCellValue(row.getCell(2))); // district
					pstmt.setString(4, getCellValue(row.getCell(3))); // district_code
					pstmt.setString(5, getCellValue(row.getCell(4))); // taluka
					pstmt.setString(6, getCellValue(row.getCell(5))); // taluka_code
					pstmt.setString(7, getCellValue(row.getCell(6))); // village
					pstmt.setString(8, getCellValue(row.getCell(7))); // village_code
					pstmt.setString(9, getCellValue(row.getCell(8))); // beneficiary_id
					pstmt.setString(10, getCellValue(row.getCell(9))); // beneficiary_name
					pstmt.setString(11, getCellValue(row.getCell(10))); // shop_no
					pstmt.setString(12, getCellValue(row.getCell(11))); // rc_no
					pstmt.setString(13, getCellValue(row.getCell(12))); // unique_no_old
					pstmt.setString(14, getCellValue(row.getCell(13))); // aadhaar_number
					pstmt.setString(15, getCellValue(row.getCell(14))); // aadhaar_reference_no
					pstmt.setString(16, getCellValue(row.getCell(15))); // first_name
					pstmt.setString(17, getCellValue(row.getCell(16))); // middle_name
					pstmt.setString(18, getCellValue(row.getCell(17))); // surname
					pstmt.setString(19, getCellValue(row.getCell(18))); // full_name
					pstmt.setString(20, getCellValue(row.getCell(19))); // bank_name
					pstmt.setString(21, getCellValue(row.getCell(20))); // account_number
					pstmt.setString(22, getCellValue(row.getCell(21))); // ifsc_code
					pstmt.setString(23, getCellValue(row.getCell(22))); // account_holder_name
					pstmt.setString(24, getCellValue(row.getCell(23))); // total_members
					// pstmt.setString(25, getCellValue(row.getCell(24))); // amount
					pstmt.setDouble(25, Double.parseDouble(getCellValue(row.getCell(24))));// amount
					pstmt.setString(26, getCellValue(row.getCell(26))); // created_by
					pstmt.setString(27, getCellValue(row.getCell(28))); // modified_by
					pstmt.addBatch();
				}

				pstmt.executeBatch();
				conn.commit();

				response.setResponseCode(ErrorConstants.SUCCESS_FAILED_CODE.SUCCESS.value);
				response.setResponseMessage("Excel uploaded and data inserted/updated successfully");
			}

		} catch (Exception e) {
			e.printStackTrace();
			try {
				if (conn != null)
					conn.rollback();
			} catch (SQLException ignored) {
			}
			response.setResponseCode(ErrorConstants.SUCCESS_FAILED_CODE.FAILED.value);
			response.setResponseMessage("Error while processing file: " + e.getMessage());
		} finally {
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (SQLException ignored) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException ignored) {
			}
		}
		return response;
	}

	// Utility: Read cell values safely
	private String getCellValue(Cell cell) {
		if (cell == null)
			return null;
		switch (cell.getCellType()) {
		case STRING:
			return cell.getStringCellValue().trim();
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell))
				return cell.getDateCellValue().toString();
			else
				return String.valueOf(cell.getNumericCellValue());
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		default:
			return null;
		}
	}

	// Utility: Convert String to Timestamp
	private Timestamp toTimestamp(String value) {
		try {
			if (value == null || value.isEmpty())
				return null;
			return Timestamp.valueOf(value.replace("T", " "));
		} catch (Exception e) {
			return null;
		}
	}
}
