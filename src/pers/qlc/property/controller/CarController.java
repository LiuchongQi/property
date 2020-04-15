package pers.qlc.property.controller;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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

import pers.qlc.property.dao.CarDao;
import pers.qlc.property.dao.HouseDao;

@Controller
public class CarController {
	@Autowired
	private CarDao userdao;
	@Autowired
	private HouseDao housedao;

	// 查询车辆
	@RequestMapping("carAll")
	@ResponseBody
	public List<HashMap<String, Object>> carAll(HttpServletRequest request) {
		String pagenum = request.getParameter("pagenum");
		if (pagenum == null || "".equals(pagenum.trim())) {
			pagenum = "1";
		}
		int currentPage = Integer.parseInt(pagenum);
		int totalCount = userdao.carAllNum();
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
		List<HashMap<String, Object>> feeFindByHouseIdNotDo = userdao.carAll(index, count);
		if (!feeFindByHouseIdNotDo.isEmpty()) {
			feeFindByHouseIdNotDo.get(0).put("pagenum", currentPage);
			feeFindByHouseIdNotDo.get(0).put("maxnum", totalPage);
		}
		return feeFindByHouseIdNotDo;
	}

	// 删除车辆
	@RequestMapping("cardelete")
	@ResponseBody
	public String cardelete(HttpServletRequest request) {
		userdao.carDeleteByCarId(request.getParameter("carid"));
		return "ok";
	}

	// 查询用户
	@RequestMapping("ishouseid")
	@ResponseBody
	public boolean ishouseid(HttpServletRequest request) {
		boolean findHouseById = false;
		String id = request.getParameter("houseid");
		if (id != null && !("".equals(id))) {
			findHouseById = !housedao.houseFindById(id).isEmpty();
		}
		return findHouseById;
	}

	// 插入车辆
	@RequestMapping("carinsert")
	@ResponseBody
	public String carinsert(String houseid, String carid, String type, String color, String intime) {
		userdao.carInsert(houseid, carid, type, color, intime);
		return "ok";
	}

	// 查看车辆信息
	@RequestMapping("mycar")
	@ResponseBody
	public List<HashMap<String, Object>> mycar(HttpServletRequest request) {
		List<HashMap<String, Object>> feeFindByHouseIdDo = userdao
				.carFindByHouseId((String) request.getSession().getAttribute("username"));
		return feeFindByHouseIdDo;
	}

	// 搜索
	@RequestMapping("findcar")
	@ResponseBody
	public List<HashMap<String, Object>> findcar(HttpServletRequest request) {
		List<HashMap<String, Object>> carFindByCarId = userdao.carFindByCarId(request.getParameter("homeid"),
				request.getParameter("carid"));
		return carFindByCarId;
	}
	// 导出
		@RequestMapping("exportcar")
		@ResponseBody
		public void exportinfo(HttpServletRequest request, HttpServletResponse response) {
			String homeid = request.getParameter("homeid");
			String carid = request.getParameter("carid");
			List<HashMap<String, Object>> info = null;
			if (homeid == ""&&carid=="") {
				info = userdao.carAll(-1, -1);
			}else {
				info = userdao.carFindByCarId(homeid, carid);
			}
			HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
			// 2.创建工作簿
			HSSFSheet sheet = hssfWorkbook.createSheet();
			// 3.创建标题行
			HSSFRow titlerRow = sheet.createRow(0);
			titlerRow.createCell(0).setCellValue("序号");
			titlerRow.createCell(1).setCellValue("车主");
			titlerRow.createCell(2).setCellValue("车牌号");
			titlerRow.createCell(3).setCellValue("型号");
			titlerRow.createCell(4).setCellValue("颜色");
			titlerRow.createCell(5).setCellValue("登记时间");
			// 4.遍历数据,创建数据行
			int i = 1;
			for (HashMap<String, Object> dw : info) {
				// 获取最后一行的行号
				int lastRowNum = sheet.getLastRowNum();
				HSSFRow dataRow = sheet.createRow(lastRowNum + 1);
				dataRow.createCell(0).setCellValue(i);
				if(dw.get("houseid")!=null) {
					dataRow.createCell(1).setCellValue(dw.get("houseid").toString());
				}
				if(dw.get("carid")!=null) {
					dataRow.createCell(2).setCellValue(dw.get("carid").toString());
				}
				if(dw.get("type")!=null) {
					dataRow.createCell(3).setCellValue(dw.get("type").toString());
				}
				if(dw.get("color")!=null) {
					dataRow.createCell(4).setCellValue(dw.get("color").toString());
				}
				if(dw.get("intime")!=null) {
					dataRow.createCell(5).setCellValue(dw.get("intime").toString());
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
		@RequestMapping("daorucar")
		@ResponseBody
	public String daorucar(HttpServletRequest request) {
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
					if(!("车辆导入表".equals(tablename))) {
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
						String carid=row.getCell(1).getStringCellValue();
						String houseid=row.getCell(2).getStringCellValue();
						String type=row.getCell(3).getStringCellValue();
						String color=row.getCell(4).getStringCellValue();
						String intime=String.valueOf((int)row.getCell(5).getNumericCellValue());
						if(intime.equals("")) {
							intime=new SimpleDateFormat("yyyyMMdd").format(new Date());
						}
						userdao.carInsert(houseid, carid, type, color, intime);
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
