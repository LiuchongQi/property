package pers.qlc.property.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;

import pers.qlc.property.entity.Fee;

@Service
public class DataService {
	public List<Fee> dataToObj(List<HashMap<String, Object>> feeFindByHouseIdDo) {
		List<Fee> feelist = new ArrayList<Fee>();
		boolean flag = false;
		for (HashMap<String, Object> map : feeFindByHouseIdDo) {
			String houseid = (String) map.get("houseid");
			String feename = (String) map.get("feename");
			int num = (int) map.get("num");
			flag = false;
			for (Fee fee : feelist) {
				if (fee.getHouseid().equals(houseid)) {
					if (feename.equals("电费")) {
						fee.setElefee(num);
					} else if (feename.equals("水费")) {
						fee.setWatfee(num);
					} else if (feename.equals("燃气费")) {
						fee.setFirfee(num);
					}
					flag = true;
					break;
				}
			}
			if (flag == false) {
				if (feename.equals("电费")) {
					Fee fee = new Fee();
					fee.setElefee(num);
					fee.setHouseid(houseid);
					feelist.add(fee);
				} else if (feename.equals("水费")) {
					Fee fee = new Fee();
					fee.setWatfee(num);
					fee.setHouseid(houseid);
					feelist.add(fee);
				} else if (feename.equals("燃气费")) {
					Fee fee = new Fee();
					fee.setFirfee(num);
					fee.setHouseid(houseid);
					feelist.add(fee);
				}
			}
		}
		return feelist;
	}

	public double[] point(List<Fee> feelist1, double[] point1) {
		int j = 0, k = 0, l = 0, p = 0;
		for (Fee fee : feelist1) {
			j += fee.getElefee();
			k += fee.getWatfee();
			l += fee.getFirfee();
		}
		p = feelist1.size();
		point1[0] = new BigDecimal((float) j / p).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		point1[1] = new BigDecimal((float) k / p).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		point1[2] = new BigDecimal((float) l / p).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
		return point1;
	}

	public int compare(Fee fee, double[] point1, double[] point2, double[] point3, double[] point4) {
		double j = 0, k = 0, l = 0, o = 0;
		int elefee = fee.getElefee();
		int watfee = fee.getWatfee();
		int firfee = fee.getFirfee();
		j = Math.pow(point1[0] - elefee, 2) + Math.pow(point1[1] - watfee, 2) + Math.pow(point1[2] - firfee, 2);
		k = Math.pow(point2[0] - elefee, 2) + Math.pow(point2[1] - watfee, 2) + Math.pow(point2[2] - firfee, 2);
		l = Math.pow(point3[0] - elefee, 2) + Math.pow(point3[1] - watfee, 2) + Math.pow(point3[2] - firfee, 2);
		o = Math.pow(point4[0] - elefee, 2) + Math.pow(point4[1] - watfee, 2) + Math.pow(point4[2] - firfee, 2);
		double min1 = (j <= k) ? j : k;
		double min2 = (l <= o) ? l : o;
		double min = (min1 <= min2) ? min1 : min2;
		if (min == j) {
			return 1;
		} else if (min == k) {
			return 2;
		} else if (min == l) {
			return 3;
		} else if (min == o) {
			return 4;
		}
		return 0;
	}

	public ArrayList<String> retList(List<Fee> feelist1, List<Fee> feelist2, List<Fee> feelist3, List<Fee> feelist4, double[] clone1,
			double[] clone2, double[] clone3, double[] clone4) {
		System.out.println(clone1[0]);
		System.out.println(clone1[1]);
		System.out.println(clone1[2]);
		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add(String.valueOf(feelist1.size()));
		arrayList.add(String.valueOf(feelist2.size()));
		arrayList.add(String.valueOf(feelist3.size()));
		arrayList.add(String.valueOf(feelist4.size()));
		for(int i=0;i<3;i++) {
			arrayList.add(String.valueOf(clone1[i]));
			arrayList.add(String.valueOf(clone2[i]));
			arrayList.add(String.valueOf(clone3[i]));
			arrayList.add(String.valueOf(clone4[i]));
		}
		for(Fee fee:feelist1) {
			arrayList.add(fee.getHouseid());
		}
		for(Fee fee:feelist2) {
			arrayList.add(fee.getHouseid());
		}
		for(Fee fee:feelist3) {
			arrayList.add(fee.getHouseid());
		}
		for(Fee fee:feelist4) {
			arrayList.add(fee.getHouseid());
		}
		return arrayList;
	}
}
