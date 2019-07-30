package design.pattern.structural_patterns.facade;


import design.pattern.structural_patterns.facade.entity.Entity1;
import design.pattern.structural_patterns.facade.entity.Entity2;
import design.pattern.structural_patterns.facade.entity.Entity3;
import design.pattern.structural_patterns.facade.entity.Entity4;

/**
 * @author liuwei
 * @date 2019-07-30 14:23
 * @desc 提供给调用者的所有子系统的统一服务窗口类，即外观类
 * 本外观类中持有所有子系统对象，并创建了相应的方法代为调用子系统的功能
 * 对调用者隐藏了子系统的内部信息
 * 对外提供一个功能入口
 */
public class ServiceWindow {
    private Entity1 entity1;
    private Entity2 entity2;
    private Entity3 entity3;
    private Entity4 entity4;

    public ServiceWindow(){
        this.entity1 = new Entity1();
        this.entity2 = new Entity2();
        this.entity3 = new Entity3();
        this.entity4 = new Entity4();
    }

    //子系统1
    public void system1Method1(){
        entity1.system1Method1();
    }

    public void system1Method2(){
        entity1.system1Method2();
    }

    //子系统2
    public void system2Method1(){
        entity2.system2Method1();
    }

    public void system2Method2(){
        entity2.system2Method2();
    }

    //子系统3
    public void system3Method1(){
        entity3.system3Method1();
    }

    public void system3Method2(){
        entity3.system3Method2();
    }

    public void system3Method3(){
        entity3.system3Method3();
    }

    //子系统4
    public void system4Method(){
        entity4.system4Method();
    }
}
