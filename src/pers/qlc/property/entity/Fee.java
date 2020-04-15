package pers.qlc.property.entity;

public class Fee {
	private String houseid;
	private int elefee=0;		//电费
	private int watfee=0;		//水费
	private int firfee=0;		//燃气费
	public String getHouseid() {
		return houseid;
	}
	public void setHouseid(String houseid) {
		this.houseid = houseid;
	}
	public int getElefee() {
		return elefee;
	}
	public void setElefee(int elefee) {
		this.elefee = elefee;
	}
	public int getWatfee() {
		return watfee;
	}
	public void setWatfee(int watfee) {
		this.watfee = watfee;
	}
	public int getFirfee() {
		return firfee;
	}
	public void setFirfee(int firfee) {
		this.firfee = firfee;
	}
	
}
