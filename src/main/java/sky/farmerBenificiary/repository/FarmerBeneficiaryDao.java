package sky.farmerBenificiary.repository;

import java.sql.Connection;
import java.util.List;

import sky.farmerBenificiary.payloads.FarmerPayload;

public interface FarmerBeneficiaryDao {
	List<FarmerPayload> getBeneficiaryList(FarmerPayload oFarmerPayload, Connection con);

	int updateStatusBenf(FarmerPayload oReqFarmerPayload, Connection con);

	int updateModifedRecordsBenf(FarmerPayload oReqFarmerPayload, Connection con);
	
	int InsertRecordsBenf(FarmerPayload oReqFarmerPayload, Connection con);
}
