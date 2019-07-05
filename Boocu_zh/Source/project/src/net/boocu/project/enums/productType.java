package net.boocu.project.enums;


public enum productType {
	/**销售*/
	sale(1),
	/**求购*/
	buy(2),
	/**租赁*/
	rent(3),
	/**求租*/
	wantRent(4),   
	/**维修*/
	repair(5),
	/**求修*/
	wantRepair(6),
	/**自动化测试方案*/
	autoTest(7),
	/**方案需求*/
	projectNeed(8),
	/**拍卖*/
	auction(9),
	/**招标*/
	invite(10),
	/**产品测试*/
	productTest(11),
	/**测试需求*/
	requireTest(12),
	/**计量标准*/
	calibration(13);

	
	private final int value;
	
	 private productType(int value) {
	        this.value = value;
	    }
	
	public int getValue(){
		return value;
	}
	
}
