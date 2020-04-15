package pers.qlc.property.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import pers.qlc.property.dao.FixDao;

@Controller
public class FixController {
	@Autowired
	private FixDao fixDao;

	// 用户申报记录
	@RequestMapping("myfixAll")
	@ResponseBody
	public List<HashMap<String, Object>> myfixAll(HttpServletRequest request) {
		List<HashMap<String, Object>> fixByHouseId = fixDao
				.fixByHouseId((String) request.getSession().getAttribute("username"));
		for (HashMap<String, Object> fixbyid : fixByHouseId) {
			if ("未评价".equals(fixbyid.get("type"))) {
				fixbyid.put("iseval", true);
			} else {
				fixbyid.put("iseval", false);
			}

		}
		return fixByHouseId;
	}

	// 用户申报
	@RequestMapping("myfixup")
	@ResponseBody
	public String myfixup(HttpServletRequest request) {
		int time = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new Date()));
		fixDao.fixInsert(request.getParameter("fixtype"), request.getParameter("mainthing"),
				request.getParameter("where"), time, (String) request.getSession().getAttribute("username"),
				request.getParameter("phone"));
		return "ok";
	}

	// 未完成申报
	@RequestMapping("notdo")
	@ResponseBody
	public List<HashMap<String, Object>> notdo(HttpServletRequest request) {
		List<HashMap<String, Object>> fixAll = fixDao.fixAll();
		return fixAll;
	}

	// 已完成申报
	@RequestMapping("done")
	@ResponseBody
	public List<HashMap<String, Object>> done(HttpServletRequest request) {
		List<HashMap<String, Object>> fixAll = fixDao.fixedAll();
		return fixAll;
	}

	// 已受理
	@RequestMapping("havesee")
	@ResponseBody
	public void havesee(HttpServletRequest request) {
		fixDao.see(request.getParameter("id"));
	}

	// 已完成
	@RequestMapping("haveok")
	@ResponseBody
	public void haveok(HttpServletRequest request) {
		fixDao.done(request.getParameter("id"));
	}

	// 评价
	@RequestMapping("myeval")
	@ResponseBody
	public void myeval(HttpServletRequest request) {
		fixDao.eval(request.getParameter("id"), Integer.parseInt(request.getParameter("eval")));
	}
}
