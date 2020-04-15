package pers.qlc.property.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import pers.qlc.property.dao.UserDao;

@Controller
public class LoginController {
	@Autowired
	private UserDao userdao;

//	用户登录
	@RequestMapping("login")
	@ResponseBody
	public String test1(String uname, String psd, HttpServletRequest request) {
		if (uname != null && !("".equals(uname)) && psd != null && !("".equals(psd))) {
			HashMap<String, Object> per = userdao.loginFindById(uname);
			if (per != null) {
				String pass = (String) per.get("password");
				if (psd.equals(pass)) {
					String role = (String) per.get("role");
					HttpSession session = request.getSession(true);
					session.setAttribute("username", uname);
					session.setAttribute("role", role);
					if ("admin".equals(role)) {
						return "admin";
					} else if ("user".equals(role)) {
						return "user";
					}
				}
			}
		}
		return "no";
	}

	// 登录检测
	@RequestMapping("check")
	@ResponseBody
	public String check(HttpServletRequest request) {
		HttpSession session = request.getSession();
		String username = (String) session.getAttribute("username");
		String role = (String) session.getAttribute("role");
		if (username != null && !("".equals(username)) && "admin".equals(role)) {
			return username;
		} else {
			session.removeAttribute(username);
			return "no";
		}
	}

	// 登录检测2
	@RequestMapping("check2")
	@ResponseBody
	public String check2(HttpServletRequest request) {
		HttpSession session = request.getSession();
		String username = (String) session.getAttribute("username");
		String role = (String) session.getAttribute("role");
		if (username != null && !("".equals(username)) && "user".equals(role)) {
			return username;
		} else {
			session.removeAttribute(username);
			return "no";
		}
	}

	// 登陆注销
	@RequestMapping("logout")
	@ResponseBody
	public String logout(HttpServletRequest request) {
		request.getSession().removeAttribute("username");
		return "ok";
	}
}
