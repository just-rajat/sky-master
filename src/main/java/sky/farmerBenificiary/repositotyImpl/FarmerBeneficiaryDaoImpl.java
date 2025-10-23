package sky.farmerBenificiary.repositotyImpl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import sky.farmerBenificiary.payloads.FarmerPayload;
import sky.farmerBenificiary.repository.FarmerBeneficiaryDao;
import sky.farmerBenificiary.utils.QueryMaster;
import sky.farmerBenificiary.utils.Utils;

@Repository
public class FarmerBeneficiaryDaoImpl implements FarmerBeneficiaryDao {

	@Override
	public List<FarmerPayload> getBeneficiaryList(FarmerPayload oReqFarmerPayload, Connection con) {
		List<FarmerPayload> farmerPayloadList = new ArrayList<FarmerPayload>();
		List<Object> param = new ArrayList<Object>();
		StringBuilder query = new StringBuilder();
		QueryMaster qm = new QueryMaster();
		try {
			query.append(
					" SELECT id, state, state_code, district, district_code, taluka, taluka_code, village, village_code, "
							+ "    beneficiary_id, beneficiary_name, shop_no, rc_no, unique_no_old, aadhaar_number, aadhaar_reference_no, "
							+ "   first_name, middle_name, surname, full_name, bank_name, account_number, ifsc_code, account_holder_name, "
							+ "   total_members, amount, created_at, created_by, "
							+ "   modified_at, modified_by FROM farmer_beneficiary_details where 1=1  ");
			if (Utils.isNeitherNullNorEmpty(oReqFarmerPayload.getStateCode())) {
				query.append(" and state_code = ?");
				param.add(oReqFarmerPayload.getStateCode());
			}
			if (Utils.isNeitherNullNorEmpty(oReqFarmerPayload.getDistrictCode())) {
				query.append(" and district_code = ?");
				param.add(oReqFarmerPayload.getDistrictCode());
			}
			if (Utils.isNeitherNullNorEmpty(oReqFarmerPayload.getTalukaCode())) {
				query.append(" and taluka_code = ?");
				param.add(oReqFarmerPayload.getTalukaCode());
			}
			if (Utils.isNeitherNullNorEmpty(oReqFarmerPayload.getVillageCode())) {
				query.append(" and village_code = ?");
				param.add(oReqFarmerPayload.getVillageCode());
			}
			if (Utils.isNeitherNullNorEmpty(oReqFarmerPayload.getBeneficiaryId())) {
				query.append(" and beneficiary_id = ?");
				param.add(oReqFarmerPayload.getBeneficiaryId());
			}
			if (Utils.isNeitherNullNorEmpty(oReqFarmerPayload.getShopNo())) {
				query.append(" and shop_no = ?");
				param.add(oReqFarmerPayload.getShopNo());
			}
			qm.setStartRow(oReqFarmerPayload.getStartRow());
			qm.setEndRow(oReqFarmerPayload.getEndRow());
			ResultSet rs = qm.select(query.toString(), param, con);
			while (rs.next()) {
				FarmerPayload oFarmerPayload = new FarmerPayload();
				oFarmerPayload.setId(rs.getLong("id"));
				oFarmerPayload.setState(rs.getString("state"));
				oFarmerPayload.setStateCode(rs.getString("state_code"));
				oFarmerPayload.setDistrict(rs.getString("district"));
				oFarmerPayload.setDistrictCode(rs.getString("district_code"));
				oFarmerPayload.setTaluka(rs.getString("taluka"));
				oFarmerPayload.setTalukaCode(rs.getString("taluka_code"));
				oFarmerPayload.setVillage(rs.getString("village"));
				oFarmerPayload.setVillageCode(rs.getString("village_code"));
				oFarmerPayload.setBeneficiaryId(rs.getString("beneficiary_id"));
				oFarmerPayload.setBeneficiaryName(rs.getString("beneficiary_name"));
				oFarmerPayload.setShopNo(rs.getString("shop_no"));
				oFarmerPayload.setRcNo(rs.getString("rc_no"));
				oFarmerPayload.setUniqueNoOld(rs.getString("unique_no_old"));
				oFarmerPayload.setAadhaarNumber(rs.getString("aadhaar_number"));
				oFarmerPayload.setAadhaarReferenceNo(rs.getString("aadhaar_reference_no"));
				oFarmerPayload.setFirstName(rs.getString("first_name"));
				oFarmerPayload.setMiddleName(rs.getString("middle_name"));
				oFarmerPayload.setSurname(rs.getString("surname"));
				oFarmerPayload.setFullName(rs.getString("full_name"));
				oFarmerPayload.setBankName(rs.getString("bank_name"));
				oFarmerPayload.setAccountNumber(rs.getString("account_number"));
				oFarmerPayload.setIfscCode(rs.getString("ifsc_code"));
				oFarmerPayload.setAccountHolderName(rs.getString("account_holder_name"));
				oFarmerPayload.setTotalMembers(rs.getInt("total_members"));
				oFarmerPayload.setAmount(rs.getBigDecimal("amount"));
				oFarmerPayload.setCreatedAt(rs.getTimestamp("created_at"));
				oFarmerPayload.setCreatedBy(rs.getString("created_by"));
				oFarmerPayload.setModifiedAt(rs.getTimestamp("modified_at"));
				oFarmerPayload.setModifiedBy(rs.getString("modified_by"));
				oFarmerPayload.setTotalRows(qm.getTotalRows());
				oFarmerPayload.setTotalNoOfRows(String.valueOf(qm.getTotalRows()));
				farmerPayloadList.add(oFarmerPayload);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return farmerPayloadList;
	}

	@Override
	public int updateStatusBenf(FarmerPayload oReqFarmerPayload, Connection con) {
		QueryMaster qm = new QueryMaster();
		List<Object> param = new ArrayList<Object>();
		int res = 0;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(" UPDATE farmer_beneficiary_details\n" + "        SET \n"
					+ "            approval_status = 'A',\n" + "            approved_by = ?,\n"
					+ "            approved_at = NOW(),\n" + "            modified_by = ?,\n"
					+ "            modified_at = NOW()\n" + "        WHERE id = ? AND beneficiary_id = ? ");

			param.add("DEEM_APPROVED");
			param.add("DEEM_APPROVED");
			param.add(oReqFarmerPayload.getId());
			param.add(oReqFarmerPayload.getBeneficiaryId());
			res = qm.updateInsert(sql.toString(), param, con, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	@Override
	public int updateModifedRecordsBenf(FarmerPayload oReqFarmerPayload, Connection con) {
		QueryMaster qm = new QueryMaster();
		List<Object> param = new ArrayList<Object>();
		int res = 0;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(" UPDATE farmer_beneficiary_details\n" + "    SET \n" + "        state = ?,\n"
					+ "        state_code = ?,\n" + "        district = ?,\n" + "        district_code = ?,\n"
					+ "        taluka = ?,\n" + "        taluka_code = ?,\n" + "        village = ?,\n"
					+ "        village_code = ?,\n" + "        beneficiary_name = ?,\n" + "        shop_no = ?,\n"
					+ "        rc_no = ?,\n" + "        unique_no_old = ?,\n" + "        aadhaar_number = ?,\n"
					+ "        aadhaar_reference_no = ?,\n" + "        first_name = ?,\n" + "        middle_name = ?,\n"
					+ "        surname = ?,\n" + "        full_name = ?,\n" + "        bank_name = ?,\n"
					+ "        account_number = ?,\n" + "        ifsc_code = ?,\n"
					+ "        account_holder_name = ?,\n" + "        total_members = ?,\n" + "        amount = ?,\n"
					+ "        modified_by = ?,\n" + "        modified_at = NOW()"
					+ "        WHERE id = ? AND beneficiary_id = ? ");
			param.add(oReqFarmerPayload.getState());
			param.add(oReqFarmerPayload.getStateCode());
			param.add(oReqFarmerPayload.getDistrict());
			param.add(oReqFarmerPayload.getDistrictCode());
			param.add(oReqFarmerPayload.getTaluka());
			param.add(oReqFarmerPayload.getTalukaCode());
			param.add(oReqFarmerPayload.getVillage());
			param.add(oReqFarmerPayload.getVillageCode());
			param.add(oReqFarmerPayload.getBeneficiaryName());
			param.add(oReqFarmerPayload.getShopNo());
			param.add(oReqFarmerPayload.getRcNo());
			param.add(oReqFarmerPayload.getUniqueNoOld());
			param.add(oReqFarmerPayload.getAadhaarNumber());
			param.add(oReqFarmerPayload.getAadhaarReferenceNo());
			param.add(oReqFarmerPayload.getFirstName());
			param.add(oReqFarmerPayload.getMiddleName());
			param.add(oReqFarmerPayload.getSurname());
			param.add(oReqFarmerPayload.getFullName());
			param.add(oReqFarmerPayload.getBankName());
			param.add(oReqFarmerPayload.getAccountNumber());
			param.add(oReqFarmerPayload.getIfscCode());
			param.add(oReqFarmerPayload.getAccountHolderName());
			param.add(oReqFarmerPayload.getTotalMembers());
			param.add(oReqFarmerPayload.getAmount());
			param.add(oReqFarmerPayload.getModifiedBy());
			param.add(oReqFarmerPayload.getId());
			param.add(oReqFarmerPayload.getBeneficiaryId());
			res = qm.updateInsert(sql.toString(), param, con, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}

	@Override
	public int InsertRecordsBenf(FarmerPayload oReqFarmerPayload, Connection con) {
		QueryMaster qm = new QueryMaster();
		List<Object> param = new ArrayList<Object>();
		int res = 0;
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(" INSERT INTO farmer_beneficiary_details (\n"
					+ "        state, state_code, district, district_code, taluka, taluka_code,\n"
					+ "        village, village_code, beneficiary_id, beneficiary_name,\n"
					+ "        shop_no, rc_no, unique_no_old, aadhaar_number, aadhaar_reference_no,\n"
					+ "        first_name, middle_name, surname, full_name,\n"
					+ "        bank_name, account_number, ifsc_code, account_holder_name,\n"
					+ "        total_members, amount, created_by,"
					+ "        created_at,modified_at \n"
					+ "    ) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,now(),null)");
			param.add(oReqFarmerPayload.getState());//state
			param.add(oReqFarmerPayload.getStateCode());//state_code
			param.add(oReqFarmerPayload.getDistrict());//district
			param.add(oReqFarmerPayload.getDistrictCode());//district_code
			param.add(oReqFarmerPayload.getTaluka());//taluka
			param.add(oReqFarmerPayload.getTalukaCode());//taluka_code
			param.add(oReqFarmerPayload.getVillage());//village
			param.add(oReqFarmerPayload.getVillageCode());//village_code
			param.add(oReqFarmerPayload.getBeneficiaryId());//beneficiary_id
			param.add(oReqFarmerPayload.getBeneficiaryName());//beneficiary_name
			param.add(oReqFarmerPayload.getShopNo());//shop_no
			param.add(oReqFarmerPayload.getRcNo());//rc_no
			param.add(oReqFarmerPayload.getUniqueNoOld());//unique_no_old
			param.add(oReqFarmerPayload.getAadhaarNumber());//aadhaar_number
			param.add(oReqFarmerPayload.getAadhaarReferenceNo());//aadhaar_reference_no
			param.add(oReqFarmerPayload.getFirstName());//first_name
			param.add(oReqFarmerPayload.getMiddleName());//middle_name
			param.add(oReqFarmerPayload.getSurname());//surname
			param.add(oReqFarmerPayload.getFullName());//full_name
			param.add(oReqFarmerPayload.getBankName());//bank_name
			param.add(oReqFarmerPayload.getAccountNumber());//account_number
			param.add(oReqFarmerPayload.getIfscCode());//ifsc_code
			param.add(oReqFarmerPayload.getAccountHolderName());//account_holder_name
			param.add(oReqFarmerPayload.getTotalMembers());//total_members
			param.add(oReqFarmerPayload.getAmount());// amount
			param.add(oReqFarmerPayload.getCreatedBy());//created_by
			res = qm.updateInsert(sql.toString(), param, con, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
}
