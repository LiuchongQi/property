package pers.qlc.property.dao;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.mybatis.spring.annotation.MapperScan;

@MapperScan
public interface HouseDao {
	List<HashMap<String,Object>> houseAll(@Param("index") int index,@Param("count") int count);
	int houseAllNum();
	List<HashMap<String,Object>> houseFindById(String id);
	List<HashMap<String,Object>> myinfo(String id);
	void houseInsert(String houseid,String name,String intime,int peonum,String idcardnum,String phone);
	void houseUpdate(String houseid,String name,String intime,int peonum,String idcardnum,String phone);
	void houseDelete(String houseid,String name,String idcardnum,String outtime);
}
