package design.pattern.creational_patterns.builder;

import design.pattern.creational_patterns.builder.constant.Brand;
import design.pattern.creational_patterns.builder.constant.CupSize;
import design.pattern.creational_patterns.builder.constant.SugarDegree;
import design.pattern.creational_patterns.builder.entity.Drink;
import design.pattern.creational_patterns.builder.entity.Food;
import design.pattern.creational_patterns.builder.entity.Meal;

/**
 * @author liuwei
 * @date 2019-07-27 18:14:06
 * @desc 创建者类
 * 一个类对象的创建很复杂，单是创建对象就要多行代码时，可以将创建对象的逻辑提取到一个专门的建造者类中
 * 通常，一个类有多个字段，每个字段的实例化又比较复杂导致整个对象的创建比较复杂时，使用创建者类创建对象
 * 所谓复杂，即长代码，比如字段为集合、字段依赖其它对象、字段需要异常处理、字段需要计算、字段需要同步等待等
 */
public class Builder {
	
	//一个套餐对象的创建逻辑很长，直接嵌入业务逻辑会严重影响业务代码的可读性，创建逻辑应提取出来
	public Meal singleHamburgCokeMeal(String hotDegree) {
		Meal meal = new Meal();
		meal.setPerson((byte) 1);
		meal.setName("个人套餐(1香辣鸡腿堡+1大盒薯条+1大杯可乐)");
		Drink drink = new Drink(SugarDegree.Zero.value(), hotDegree);
		drink.setValues(Brand.Coke.value(), 4.5f, 1, null, CupSize.Big.value());
		meal.addItem(drink);
		Food food = new Food();
		food.setValues(Brand.ChickenHamburg.value(), 10.0f, 1, null, null);
		meal.addItem(food);
		return meal;
	}
	
	public Meal doubleHamburgCokeMeal(String hotDegree) {
		return doubleHamburgCokeMeal(hotDegree,hotDegree);
	}
	
	public Meal doubleHamburgCokeMeal(String hotDegree1,String hotDegree2) {
		Meal meal = new Meal();
		meal.setPerson((byte) 2);
		meal.setName("双人套餐(1香辣鸡腿堡+1牛肉堡+2大盒薯条+2大杯可乐)");
		Drink drink = new Drink(SugarDegree.Zero.value(), hotDegree1);
		if (hotDegree1.equals(hotDegree2)) {
			drink.setValues(Brand.Coke.value(), 4.0f, 2, null, CupSize.Big.value());
		}else {
			drink.setValues(Brand.Coke.value(), 4.0f, 1, null, CupSize.Big.value());
			Drink drink2 = new Drink(SugarDegree.Zero.value(), hotDegree2);
			drink2.setValues(Brand.Coke.value(), 4.0f, 1, null, CupSize.Big.value());
			meal.addItem(drink2);
		}
		meal.addItem(drink);
		Food food1 = new Food();
		food1.setValues(Brand.ChickenHamburg.value(), 10.0f, 1, null, null);
		Food food2 = new Food();
		food2.setValues(Brand.BeefHamburg.value(), 12.0f, 1, null, null);
		meal.addItem(food1);
		meal.addItem(food2);
		return meal;
	}
}
