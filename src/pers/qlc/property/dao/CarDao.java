package pers.qlc.property.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

@MapperScan
public interface CarDao {
	List<HashMap<String,Object>> carAll(@Param("index") int index,@Param("count") int count);
	int carAllNum();
	List<HashMap<String,Object>> carFindByHouseId(String id);
	List<HashMap<String,Object>> carFindByCarId(@Param("houseid") String houseid,@Param("carid") String carid);
	void carInsert(String houseid,String carid,String type,String color,String time);
	void carDeleteByCarId(String carid);
	void carDeleteByHouseId(String houseid);
}
