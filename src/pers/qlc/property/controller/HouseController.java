package pers.qlc.property.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;


import net.sf.json.JSON;
import pers.qlc.property.dao.HouseDao;
import pers.qlc.property.dao.UserDao;

@Controller
public class HouseController {
	@Autowired
	private HouseDao housedao;
	@Autowired
	private UserDao userdao;

	// 查看全部住户信息
	@RequestMapping(value = "houseAll", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public List<HashMap<String, Object>> houseAll(HttpServletRequest request, HttpServletResponse response) {
		response.setHeader("Access-Control-Allow-Origin", "*");
		String pagenum = request.getParameter("pagenum");
		if (pagenum == null || "".equals(pagenum.trim())) {
			pagenum = "1";
		}
		int currentPage = Integer.parseInt(pagenum);
		int totalCount = housedao.houseAllNum();
		int totalPage = 0;
		if (totalCount % 16 == 0) {
			totalPage = totalCount / 16;
		} else {
			totalPage = totalCount / 16 + 1;
		}
		if (currentPage <= 0) {
			currentPage = 1; // 把当前页设置为1
		} else if (totalPage == 0) {
			totalPage = 1;
		} else if (currentPage > totalPage) {
			currentPage = totalPage; // 把当前页设置为最大页数
		}
		int index = (currentPage - 1) * 16; // 查询的起始行
		int count = 16;
		List<HashMap<String, Object>> feeFindByHouseIdNotDo = housedao.houseAll(index, count);
		if (!feeFindByHouseIdNotDo.isEmpty()) {
			feeFindByHouseIdNotDo.get(0).put("pagenum", currentPage);
			feeFindByHouseIdNotDo.get(0).put("maxnum", totalPage);
		}
		return feeFindByHouseIdNotDo;
	}

	// 修改住户信息
	@RequestMapping("houseUpdate")
	@ResponseBody
	public String houseUpdate(HttpServletRequest request) {
		housedao.houseUpdate(request.getParameter("houseId"), request.getParameter("name"),
				request.getParameter("intime"), Integer.parseInt(request.getParameter("peonum")),
				request.getParameter("idcardnum"), request.getParameter("phone"));
		return "ok";
	}

	// 插入住户
	@RequestMapping("houseinsert")
	@ResponseBody
	public String houseinsert(HttpServletRequest request) {
		String intime = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String houseid = request.getParameter("houseId");
		housedao.houseInsert(houseid, request.getParameter("name"), intime,
				Integer.parseUnsignedInt(request.getParameter("peonum")), request.getParameter("idcardnum"),
				request.getParameter("phone"));
		userdao.loginInsert(houseid, houseid, "user");
		return "ok";
	}

	// 删除住户
	@RequestMapping("houseout")
	@ResponseBody
	public String houseout(HttpServletRequest request) {
		String outtime = new SimpleDateFormat("yyyyMMdd").format(new Date());
		String houseid = request.getParameter("houseId");
		housedao.houseDelete(houseid, request.getParameter("name"), request.getParameter("idcardnum"), outtime);
		userdao.loginDelete(houseid);
		return "ok";
	}

	// 个人信息
	@RequestMapping("myinfo")
	@ResponseBody
	public HashMap<String, Object> myinfo(HttpServletRequest request) {
		HashMap<String, Object> hashMap = housedao.myinfo((String) request.getSession().getAttribute("username"))
				.get(0);
		return hashMap;
	}

	// 修改密码
	@RequestMapping("changepass")
	@ResponseBody
	public String changepass(HttpServletRequest request) {
		String id = (String) request.getSession().getAttribute("username");
		String oldpass = (String) userdao.loginFindById(id).get("password");
		String parameter = request.getParameter("oldpass");
		String parameter1 = request.getParameter("newpass");
		if (oldpass.equals(parameter)) {
			userdao.loginUpdate(id, parameter1);
			return "ok";
		} else {
			return "no";
		}
	}

	// id搜索
	@RequestMapping("houseFindById")
	@ResponseBody
	public List<HashMap<String, Object>> houseFindById(HttpServletRequest request) {
		String houseid = request.getParameter("houseid");
		List<HashMap<String, Object>> houseFindById = housedao.houseFindById(houseid);
		return houseFindById;
	}

	// 导入模板下载
	@RequestMapping("/download")
	public void export(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String filename = request.getParameter("filename");
		String path = null;
		if ("house".equals(filename)) {
			path = request.getSession().getServletContext().getRealPath("/Template/用户表.xlsx");
		} else if ("car".equals(filename)) {
			path = request.getSession().getServletContext().getRealPath("/Template/车辆表.xlsx");
		} else if ("fee".equals(filename)) {
			path = request.getSession().getServletContext().getRealPath("/Template/费用表.xlsx");
		}

		File file = new File(path);
		response.setHeader("content-type", "application/octet-stream");
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;filename=" + file.getName());
		byte[] buff = new byte[1024];
		BufferedInputStream bis = null;
		OutputStream os = null;
		try {
			os = response.getOutputStream();
			bis = new BufferedInputStream(new FileInputStream(file));
			int i = bis.read(buff);
			while (i != -1) {
				os.write(buff, 0, buff.length);
				os.flush();
				i = bis.read(buff);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 导出
	@RequestMapping("exportinfo")
	@ResponseBody
	public void exportinfo(HttpServletRequest request, HttpServletResponse response) {
		String find = request.getParameter("find");
		System.out.println(find);
		List<HashMap<String, Object>> info = null;
		if (find == "") {
			info = housedao.houseAll(-1, -1);
		} else {
			info = housedao.houseFindById(find);
		}
		System.out.println(info);
		HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
		// 2.创建工作簿
		HSSFSheet sheet = hssfWorkbook.createSheet();
		// 3.创建标题行
		HSSFRow titlerRow = sheet.createRow(0);
		titlerRow.createCell(0).setCellValue("序号");
		titlerRow.createCell(1).setCellValue("住户编号");
		titlerRow.createCell(2).setCellValue("户主姓名");
		titlerRow.createCell(3).setCellValue("入住时间");
		titlerRow.createCell(4).setCellValue("搬出时间");
		titlerRow.createCell(5).setCellValue("家庭人数");
		titlerRow.createCell(6).setCellValue("身份证号码");
		titlerRow.createCell(7).setCellValue("电话");
		// 4.遍历数据,创建数据行
		int i = 1;
		for (HashMap<String, Object> dw : info) {
			// 获取最后一行的行号
			int lastRowNum = sheet.getLastRowNum();
			HSSFRow dataRow = sheet.createRow(lastRowNum + 1);
			dataRow.createCell(0).setCellValue(i);
			if (dw.get("houseid") != null) {
				dataRow.createCell(1).setCellValue(dw.get("houseid").toString());
			}
			if (dw.get("name") != null) {
				dataRow.createCell(2).setCellValue(dw.get("name").toString());
			}
			if (dw.get("intime") != null) {
				dataRow.createCell(3).setCellValue(dw.get("intime").toString());
			}
			if (dw.get("outtime") != null) {
				dataRow.createCell(4).setCellValue(dw.get("outtime").toString());
			}
			if (dw.get("peonum") != null) {
				dataRow.createCell(5).setCellValue(dw.get("peonum").toString());
			}
			if (dw.get("idcardnum") != null) {
				dataRow.createCell(6).setCellValue(dw.get("idcardnum").toString());
			}
			if (dw.get("phone") != null) {
				dataRow.createCell(7).setCellValue(dw.get("phone").toString());
			}
			i++;
		}
		// 6.获取输出流对象
		ServletOutputStream outputStream;
		try {
			outputStream = response.getOutputStream();
			// 10.写出文件,关闭流
			hssfWorkbook.write(outputStream);
			hssfWorkbook.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 导入
	@RequestMapping("/daoruinfo")
	@ResponseBody
	public String ininfo(HttpServletRequest request) {
		MultipartHttpServletRequest servletRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = servletRequest.getFileMap();
		for (Map.Entry entry : fileMap.entrySet()) {
			MultipartFile multipartFile = (MultipartFile) entry.getValue();
			InputStream inputStream=null;
			XSSFWorkbook xssfWorkbook=null;
			try {
				inputStream = multipartFile.getInputStream();
				xssfWorkbook = new XSSFWorkbook(inputStream);
				//获取文件表
				XSSFSheet sheet = xssfWorkbook.getSheetAt(0);
				String tablename=((XSSFSheet) sheet).getRow(0).getCell(0).getStringCellValue();
				if(!("用户导入表".equals(tablename))) {
					xssfWorkbook.close();
					inputStream.close();
					return "no";
				}
				for (Row row : sheet) {
					//表头不读取
					if (row.getRowNum() <=1) {
						continue;
					}
					if(row.getCell(1).getStringCellValue().equals("")) {
						break;
					}
					String houseid=row.getCell(1).getStringCellValue();
					String name=row.getCell(2).getStringCellValue();
					String intime=String.valueOf((int)row.getCell(3).getNumericCellValue());
					int num=(int) row.getCell(4).getNumericCellValue();
					String idcard=String.valueOf((int)row.getCell(5).getNumericCellValue());
					String phone=String.valueOf((int)row.getCell(6).getNumericCellValue());
					if(intime.equals("")) {
						intime=new SimpleDateFormat("yyyyMMdd").format(new Date());
					}
					housedao.houseInsert(houseid, name, intime, num, idcard, phone);
					userdao.loginInsert(houseid, "123", "user");
				}
				return "ok";
			} catch (Exception e) {
				e.printStackTrace();
				return "no";
			}finally {
				// 5、关闭流
				if(xssfWorkbook!=null) {
					try {
						xssfWorkbook.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(inputStream!=null) {
					try {
						inputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
		
	}
}
