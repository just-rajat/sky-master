package sky.farmerBenificiary.repositotyImpl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import sky.farmerBenificiary.payloads.MasterPayload;
import sky.farmerBenificiary.repository.MasterDao;
import sky.farmerBenificiary.utils.QueryMaster;
import sky.farmerBenificiary.utils.Utils;

@Repository
public class MasterDaoImpl implements MasterDao{

	@Override
	public List<MasterPayload> getStateList(MasterPayload requestBean, Connection con) {
		QueryMaster queryMaster = new QueryMaster();
		List<MasterPayload> stateList= new ArrayList<MasterPayload>();
		StringBuilder query = new StringBuilder();
		List<Object> param = new ArrayList<Object>();
		try {
			query.append(" select state_code, state_name from state_master where 1=1 ");
			if(Utils.isNeitherNullNorEmpty(requestBean.getStateCode())) {
				query.append(" and state_code= ? ");
				requestBean.getStateCode();
			}
			ResultSet rs = queryMaster.select(query.toString(), param, con);
			while (rs.next()) {
				MasterPayload bean = new MasterPayload();
				bean.setStateCode(rs.getString("state_code"));
				bean.setStateName(rs.getString("state_name"));
				stateList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();e.getMessage();
		}
		return stateList;
	}

	@Override
	public List<MasterPayload> getDistrictList(MasterPayload requestBean, Connection con) {
		QueryMaster queryMaster = new QueryMaster();
		List<MasterPayload> districtList= new ArrayList<MasterPayload>();
		StringBuilder query = new StringBuilder();
		List<Object> param = new ArrayList<Object>();
		try {
			query.append(" select district_code, district_name from state_district_master where state_code= ? ");
			requestBean.getStateCode();
			if(Utils.isNeitherNullNorEmpty(requestBean.getDistrictCode())) {
				query.append(" and district_code= ? ");
				requestBean.getDistrictCode();
			}
			ResultSet rs = queryMaster.select(query.toString(), param, con);
			while (rs.next()) {
				MasterPayload bean = new MasterPayload();
				bean.setDistrictCode(rs.getString("district_code"));
				bean.setDistrictName(rs.getString("district_name"));
				districtList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();e.getMessage();
		}
		return districtList;
	}

	@Override
	public List<MasterPayload> talukaList(MasterPayload requestBean, Connection con) {
		QueryMaster queryMaster = new QueryMaster();
		List<MasterPayload> talukaList= new ArrayList<MasterPayload>();
		StringBuilder query = new StringBuilder();
		List<Object> param = new ArrayList<Object>();
		try {
			query.append(" select taluka_code, taluka_name from state_district_taluka_master where state_code= ? "
					+ " and district_code= ? ");
			requestBean.getStateCode();
			requestBean.getDistrictCode();
			if(Utils.isNeitherNullNorEmpty(requestBean.getTalukaCode())) {
				query.append(" and taluka_code= ? ");
				requestBean.getTalukaCode();
			}
			ResultSet rs = queryMaster.select(query.toString(), param, con);
			while (rs.next()) {
				MasterPayload bean = new MasterPayload();
				bean.setTalukaCode(rs.getString("taluka_code"));
				bean.setTalukaName(rs.getString("taluka_name"));
				talukaList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();e.getMessage();
		}
		return talukaList;
	}

	@Override
	public List<MasterPayload> villageList(MasterPayload requestBean, Connection con) {
		QueryMaster queryMaster = new QueryMaster();
		List<MasterPayload> talukaList= new ArrayList<MasterPayload>();
		StringBuilder query = new StringBuilder();
		List<Object> param = new ArrayList<Object>();
		try {
			query.append(" select village_code, village_name from state_district_taluka_village_master where state_code= ? "
					+ " and district_code= ? and taluka_code= ?");
			requestBean.getStateCode();
			requestBean.getDistrictCode();
			if(Utils.isNeitherNullNorEmpty(requestBean.getVillageCode())) {
				query.append(" and village_code= ? ");
				requestBean.getTalukaCode();
			}
			ResultSet rs = queryMaster.select(query.toString(), param, con);
			while (rs.next()) {
				MasterPayload bean = new MasterPayload();
				bean.setVillageCode(rs.getString("village_code"));
				bean.setVillageName(rs.getString("village_name"));
				talukaList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();e.getMessage();
		}
		return talukaList;
	}


}
