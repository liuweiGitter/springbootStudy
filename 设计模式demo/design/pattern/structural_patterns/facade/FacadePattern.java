package design.pattern.structural_patterns.facade;

/**
 * @author liuwei
 * @date 2019-07-29 23:22:25
 * @desc 外观模式demo
 */
public class FacadePattern {
	
	public static void main(String[] args) {
		ServiceWindow serviceWindow = new ServiceWindow();
		serviceWindow.system1Method1();
		serviceWindow.system1Method2();
		serviceWindow.system2Method2();
		serviceWindow.system3Method3();
	}
	
}
