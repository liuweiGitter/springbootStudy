package design.pattern.creational_patterns.builder.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * @author liuwei
 * @date 2019-07-27 17:16:00
 * @desc
 */
@Data
public class Meal {
	private Byte person;
	private String name;
	private List<Drink> drinks = new ArrayList<Drink>();
	private List<Food> foods = new ArrayList<Food>();

	public void addItem(Eatting eatting) {
		if (eatting instanceof Drink) {
			drinks.add((Drink) eatting);
		} else if (eatting instanceof Food) {
			foods.add((Food) eatting);
		}
	}

	public String getMsg() {
		StringBuilder sb = new StringBuilder();
		float totalPrice = 0;
		if (drinks.size() > 0) {
			sb.append("----Drinks----\n");
			for (int i = 0; i < drinks.size(); i++) {
				totalPrice += drinks.get(i).getSubtotal();
				sb.append("[" + (i+1) + "]\t" + drinks.get(i).showMsg() + "\n");
			}
		}
		if (foods.size() > 0) {
			sb.append("----Foods----\n");
			for (int i = 0; i < foods.size(); i++) {
				totalPrice += foods.get(i).getSubtotal();
				sb.append("[" + (i+1) + "]\t" + foods.get(i).showMsg() + "\n");
			}
		}
		if (totalPrice == 0) {
			return "Haven't order any Food or Drink！";
		} else {
			sb.append(">>>totalPrice is:￥" + totalPrice);
			return "【"+(name==null?"非套餐":name)+"】\n"+sb.toString();
		}
	}
}
