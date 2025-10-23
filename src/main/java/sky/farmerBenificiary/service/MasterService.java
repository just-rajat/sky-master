package sky.farmerBenificiary.service;

import java.util.List;

import sky.farmerBenificiary.payloads.MasterPayload;

public interface MasterService {

	List<MasterPayload> getStateList(MasterPayload requestBean);

	List<MasterPayload> getDistrictList(MasterPayload requestBean);

	List<MasterPayload> getTalukaList(MasterPayload requestBean);

	List<MasterPayload> getVillageList(MasterPayload requestBean);

}
