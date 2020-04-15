package pers.qlc.property.dao;

import java.util.HashMap;

import org.mybatis.spring.annotation.MapperScan;

@MapperScan
public interface UserDao {
	HashMap<String,Object> loginFindById(String id);
	void loginInsert(String id,String password,String role);
	void loginUpdate(String id,String pass);
	void loginDelete(String id);
}
