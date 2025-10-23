package sky.farmerBenificiary.serviceImpl;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sky.farmerBenificiary.payloads.MasterPayload;
import sky.farmerBenificiary.payloads.FarmerPayload;
import sky.farmerBenificiary.repository.MasterDao;
import sky.farmerBenificiary.service.MasterService;

@Service
public class MasterServiceImpl implements MasterService{
	
	@Autowired
	DataSource dataSource;
	
	@Autowired
	MasterDao oMasterDao;
	
	@Override
	public List<MasterPayload> getStateList(MasterPayload requestBean) {
		List<MasterPayload> stateList= new ArrayList<MasterPayload>();
		Connection con=null;
		try {
			con = dataSource.getConnection();
			stateList =oMasterDao.getStateList(requestBean, con);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (!con.isClosed()) {
					con.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return stateList;
	}

	@Override
	public List<MasterPayload> getDistrictList(MasterPayload requestBean) {
		List<MasterPayload> districtList= new ArrayList<MasterPayload>();
		Connection con=null;
		try {
			con = dataSource.getConnection();
			districtList =oMasterDao.getDistrictList(requestBean, con);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (!con.isClosed()) {
					con.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return districtList;
	}

	@Override
	public List<MasterPayload> getTalukaList(MasterPayload requestBean) {
		List<MasterPayload> talukaList= new ArrayList<MasterPayload>();
		Connection con=null;
		try {
			con = dataSource.getConnection();
			talukaList =oMasterDao.talukaList(requestBean, con);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (!con.isClosed()) {
					con.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return talukaList;
	}

	@Override
	public List<MasterPayload> getVillageList(MasterPayload requestBean) {
		// TODO Auto-generated method stub
		return null;
	}



}
