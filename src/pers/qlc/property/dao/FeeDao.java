package pers.qlc.property.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

@MapperScan
public interface FeeDao {
	List<HashMap<String, Object>> feeFindByHouseIdNotDo(@Param("p1") String houseid);

	List<HashMap<String, Object>> feeFindByHouseIdDo(@Param("p2") String houseid, @Param("bt") String begintime,
			@Param("st") String stoptime, @Param("index") int index, @Param("count") int count);

	void feeInsert(String houseid, String feename, int num, String intime, String remarks);

	void feeDo(String id, int outtime, String peo);
	
	void deleteDo(String id);

	int feeNumFindByHouseIdDo(@Param("p2") String houseid, @Param("bt") String begintime, @Param("st") String stoptime);

}
