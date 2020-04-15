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

import pers.qlc.property.dao.CarDao;
import pers.qlc.property.dao.CarInOutDao;

@Controller
public class CarInOutController {
	@Autowired
	private CarInOutDao carInOutDao;
	@Autowired
	private CarDao carDao;

	// 车辆驶入报备
	@RequestMapping("drivein")
	@ResponseBody
	public String drivein(HttpServletRequest request) {
		String houseid = (String) request.getSession().getAttribute("username");
		carInOutDao.carIn(houseid, "迁入", request.getParameter("carid"), request.getParameter("color"),
				request.getParameter("type"));
		return "ok";
	}

	// 车辆驶出报备
	@RequestMapping("driveout")
	@ResponseBody
	public String driveout(HttpServletRequest request) {
		String houseid = (String) request.getSession().getAttribute("username");
		carInOutDao.carIn(houseid, "迁出", request.getParameter("carid"), null, null);
		return "ok";
	}

	// 车辆报备信息
	@RequestMapping("carinout")
	@ResponseBody
	public List<HashMap<String, Object>> carinout(HttpServletRequest request) {
		List<HashMap<String, Object>> carNotDone = carInOutDao.carNotDone();
		return carNotDone;
	}

	// 车辆报备yes
	@RequestMapping("feeyes")
	@ResponseBody
	public String feeyes(HttpServletRequest request) {
		String time = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String id = request.getParameter("id");
		String houseid = request.getParameter("houseid");
		String carid = request.getParameter("carid");
		String color = request.getParameter("color");
		String type = request.getParameter("type");
		carInOutDao.carDo(id, (String) request.getSession().getAttribute("username"), time, 1);
		carDao.carInsert(houseid, carid, type, color, time);
		return "ok";
	}

	// 车辆报备no
	@RequestMapping("feeno")
	@ResponseBody
	public String feeno(HttpServletRequest request) {
		String time = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String id = request.getParameter("id");
		carInOutDao.carDo(id, (String) request.getSession().getAttribute("username"), time, 2);
		return "ok";
	}

	// 车辆报备日志
	@RequestMapping("carlog")
	@ResponseBody
	public List<HashMap<String, Object>> carlog(HttpServletRequest request) {
		List<HashMap<String, Object>> carDone = carInOutDao.carDone();
		for(HashMap<String, Object> carlog:carDone) {
			if((Integer)carlog.get("isdo")==1) {
				carlog.replace("isdo", 1, "已同意");
			}
			else if((Integer)carlog.get("isdo")==2) {
				carlog.replace("isdo", 2, "已拒绝");
			}
		}
		return carDone;
	}
}
