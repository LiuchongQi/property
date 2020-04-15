package pers.qlc.property.dao;

import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.annotation.MapperScan;

@MapperScan
public interface FixDao {
	List<HashMap<String,Object>> fixAll();
	List<HashMap<String,Object>> fixedAll();
	List<HashMap<String,Object>> fixByHouseId(String id);
	void fixInsert(String fixtype,String mainthing,String where,int time,String peo,String phone);
	void see(String id);
	void done(String id);
	void eval(String id,int eval);
}
