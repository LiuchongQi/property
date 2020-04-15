package pers.qlc.property.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import pers.qlc.property.dao.FeeDao;

@Controller
public class FeeController {
	@Autowired
	private FeeDao userdao;

	// 查看住户代缴费用
	@RequestMapping("feeNotDoById")
	@ResponseBody
	public List<HashMap<String, Object>> feeNotDoById(HttpServletRequest request) {
		List<HashMap<String, Object>> feeNotDone = null;
		try {
			feeNotDone = userdao.feeFindByHouseIdNotDo(request.getParameter("houseid"));
		} catch (Exception e) {
			// TODO: handle exception
		}

		return feeNotDone;
	}

	// 缴费
	@RequestMapping("feeDo")
	@ResponseBody
	public String feeDo(HttpServletRequest request) {
		String feeid = request.getParameter("feeid");
		String peo = (String) request.getSession().getAttribute("username");
		int outtime = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(new Date()));
		userdao.feeDo(feeid, outtime, peo);
		return "ok";
	}
	// 用户待缴
	@RequestMapping("feeneeddo")
	@ResponseBody
	public List<HashMap<String, Object>> feeneeddo(HttpServletRequest request) {
		List<HashMap<String, Object>> feeFindByHouseIdNotDo = userdao
				.feeFindByHouseIdNotDo((String) request.getSession().getAttribute("username"));
		return feeFindByHouseIdNotDo;
	}

	// 新增缴费
	@RequestMapping("infee")
	@ResponseBody
	public String infee(HttpServletRequest request) {
		userdao.feeInsert(request.getParameter("houseid"), request.getParameter("feename"),
				Integer.parseInt(request.getParameter("num")), new SimpleDateFormat("yyyyMMdd").format(new Date()),
				request.getParameter("remark"));
		return "ok";
	}

	// 删除缴费
	@RequestMapping("deletedo")
	@ResponseBody
	public String deleteDo(HttpServletRequest request) {
		userdao.deleteDo(request.getParameter("feeid"));
		return "ok";
	}

	// 用户已缴
	@RequestMapping("userfeedone")
	@ResponseBody
	public List<HashMap<String, Object>> userfeedone(HttpServletRequest request) {
		String role = (String) request.getSession().getAttribute("role");
		String houseid = null;
		if (role.equals("user")) {
			houseid = (String) request.getSession().getAttribute("username");
		} else {
			houseid = request.getParameter("houseid");
		}
		String begintime = request.getParameter("begintime");
		if (begintime != null) {
			begintime = begintime.replace("-", "");
		}
		String stoptime = request.getParameter("stoptime");
		if (stoptime != null) {
			stoptime = stoptime.replace("-", "");
		}
		String pagenum = request.getParameter("pagenum");
		if (pagenum == null || "".equals(pagenum.trim())) {
			pagenum = "1";
		}
		int currentPage = Integer.parseInt(pagenum);
		int totalCount = userdao.feeNumFindByHouseIdDo(houseid, begintime, stoptime);
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
		List<HashMap<String, Object>> feeFindByHouseIdNotDo = userdao.feeFindByHouseIdDo(houseid, begintime, stoptime,
				index, count);
		if (!feeFindByHouseIdNotDo.isEmpty()) {
			feeFindByHouseIdNotDo.get(0).put("pagenum", currentPage);
			feeFindByHouseIdNotDo.get(0).put("maxnum", totalPage);
		}
		System.out.println(feeFindByHouseIdNotDo);
		return feeFindByHouseIdNotDo;
	}

	// 导出
	@RequestMapping("exportfee")
	@ResponseBody
	public void exportinfo(HttpServletRequest request, HttpServletResponse response) {
		String find = request.getParameter("find");
		List<HashMap<String, Object>> info = null;
		info = userdao.feeFindByHouseIdNotDo(find);
		HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
		// 2.创建工作簿
		HSSFSheet sheet = hssfWorkbook.createSheet();
		// 3.创建标题行
		HSSFRow titlerRow = sheet.createRow(0);
		titlerRow.createCell(0).setCellValue("序号");
		titlerRow.createCell(1).setCellValue("住户编号");
		titlerRow.createCell(2).setCellValue("费用名称");
		titlerRow.createCell(3).setCellValue("数额");
		titlerRow.createCell(4).setCellValue("开始时间");
		titlerRow.createCell(5).setCellValue("备注");
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
			if (dw.get("feename") != null) {
				dataRow.createCell(2).setCellValue(dw.get("feename").toString());
			}
			if (dw.get("num") != null) {
				dataRow.createCell(3).setCellValue(dw.get("num").toString());
			}
			if (dw.get("intime") != null) {
				dataRow.createCell(4).setCellValue(dw.get("intime").toString());
			}
			if (dw.get("remarks") != null) {
				dataRow.createCell(5).setCellValue(dw.get("remarks").toString());
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
	@RequestMapping("daorufee")
	@ResponseBody
	public String daorufee(HttpServletRequest request) {
		MultipartHttpServletRequest servletRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = servletRequest.getFileMap();
		for (Map.Entry entry : fileMap.entrySet()) {
			MultipartFile multipartFile = (MultipartFile) entry.getValue();
			InputStream inputStream = null;
			XSSFWorkbook xssfWorkbook = null;
			try {
				inputStream = multipartFile.getInputStream();
				xssfWorkbook = new XSSFWorkbook(inputStream);
				// 获取文件表
				XSSFSheet sheet = xssfWorkbook.getSheetAt(0);
				String tablename = ((XSSFSheet) sheet).getRow(0).getCell(0).getStringCellValue();
				if (!("费用导入表".equals(tablename))) {
					xssfWorkbook.close();
					inputStream.close();
					return "no";
				}
				for (Row row : sheet) {
					// 表头不读取
					if (row.getRowNum() <= 1) {
						continue;
					}
					if (row.getCell(1).getStringCellValue().equals("")) {
						break;
					}
					String houseid = row.getCell(1).getStringCellValue();
					String feename = row.getCell(2).getStringCellValue();
					int num = (int) row.getCell(3).getNumericCellValue();
					String beizhu = row.getCell(4).getStringCellValue();
					String intime = new SimpleDateFormat("yyyyMMdd").format(new Date());
					userdao.feeInsert(houseid, feename, num, intime, beizhu);
				}
				return "ok";
			} catch (Exception e) {
				e.printStackTrace();
				return "no";
			} finally {
				// 5、关闭流
				if (xssfWorkbook != null) {
					try {
						xssfWorkbook.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (inputStream != null) {
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
