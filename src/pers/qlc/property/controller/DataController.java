package pers.qlc.property.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import pers.qlc.property.dao.FeeDao;
import pers.qlc.property.entity.Fee;
import pers.qlc.property.service.DataService;

@Controller
public class DataController {
	@Autowired
	private FeeDao feedao;
	
	@Autowired
	private DataService dataService;

	// 聚类分析
	@RequestMapping("datasys")
	@ResponseBody
	public ArrayList<String> cardelete(HttpServletRequest request) {
		String begintime=request.getParameter("begintime").replace("-", "");
		String stoptime=request.getParameter("stoptime").replace("-", "");
		List<HashMap<String,Object>> feeFindByHouseIdDo = feedao.feeFindByHouseIdDo(null, begintime, stoptime, -1, -1);
		System.out.println(feeFindByHouseIdDo);
		//数据封装到类
		List<Fee> feelist = dataService.dataToObj(feeFindByHouseIdDo);
		//k-means
		//数据分组
		List<Fee> feelist1=new ArrayList<Fee>();
		List<Fee> feelist2=new ArrayList<Fee>();
		List<Fee> feelist3=new ArrayList<Fee>();
		List<Fee> feelist4=new ArrayList<Fee>();
		double[] point1=new double[3];
		double[] point2=new double[3];
		double[] point3=new double[3];
		double[] point4=new double[3];
		int size=feelist.size();
		for(int i=0;i<size;i++) {
			if(i%4==0) {
				feelist1.add(feelist.get(i));
			}
			else if(i%4==1) {
				feelist2.add(feelist.get(i));
			}
			else if(i%4==2) {
				feelist3.add(feelist.get(i));
			}
			else if(i%4==3) {
				feelist4.add(feelist.get(i));
			}
		}
		//求中心点
		boolean flag=false;
		if(feelist.isEmpty()==false&&feelist2.isEmpty()==false&&feelist3.isEmpty()==false&&feelist4.isEmpty()==false) {
			point1 = dataService.point(feelist1,point1);
			point2 = dataService.point(feelist2,point2);
			point3 = dataService.point(feelist3,point3);
			point4 = dataService.point(feelist4,point4);
			//循环迭代
			do {
				feelist1.clear();
				feelist2.clear();
				feelist3.clear();
				feelist4.clear();
				for(Fee fee:feelist) {
					int compare = dataService.compare(fee, point1, point2, point3, point4);
					if(compare==1) {
						feelist1.add(fee);
					}
					else if(compare==2) {
						feelist2.add(fee);
					}
					else if(compare==3) {
						feelist3.add(fee);
					}
					else if(compare==4) {
						feelist4.add(fee);
					}
					else {
						System.out.println("中心点比较错误！！！");
					}
				}
				double[] clone1 = point1.clone();
				double[] clone2 = point2.clone();
				double[] clone3 = point3.clone();
				double[] clone4 = point4.clone();
				point1 = dataService.point(feelist1,point1);
				point2 = dataService.point(feelist2,point2);
				point3 = dataService.point(feelist3,point3);
				point4 = dataService.point(feelist4,point4);
				flag=Arrays.equals(clone1, point1)&&Arrays.equals(clone2, point2)&&Arrays.equals(clone3, point3)&&Arrays.equals(clone4, point4);
			} while (!flag);
			return dataService.retList(feelist1, feelist2, feelist3, feelist4, point1, point2, point3, point4);
		}else {
			return null;
		}
	}
}
