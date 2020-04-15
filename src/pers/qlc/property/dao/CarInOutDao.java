package pers.qlc.property.dao;

import java.util.HashMap;
import java.util.List;

import org.mybatis.spring.annotation.MapperScan;

@MapperScan
public interface CarInOutDao {
	List<HashMap<String,Object>> carNotDone();
	List<HashMap<String,Object>> carDone();
	void carDo(String id,String peo,String time,int isdo);
	void carIn(String houseid,String in_out,String carid,String color,String type);
}
