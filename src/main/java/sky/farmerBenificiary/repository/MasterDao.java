package sky.farmerBenificiary.repository;

import java.sql.Connection;
import java.util.List;

import sky.farmerBenificiary.payloads.MasterPayload;

public interface MasterDao {

	List<MasterPayload> getStateList(MasterPayload requestBean,Connection con);

	List<MasterPayload> getDistrictList(MasterPayload requestBean, Connection con);

	List<MasterPayload> talukaList(MasterPayload requestBean, Connection con);
	
	List<MasterPayload> villageList(MasterPayload requestBean, Connection con);

}
