package design.pattern.behavioral_patterns.template;

/**
 * @author liuwei
 * @date 2019-08-08 22:18:43
 * @desc 抽象实体类
 * 定义了一些接口方法，以及一些final方法
 * 接口方法由子类实现
 * final方法则是所有子类通用的不可更改或不需要更改的方法
 * 所谓不需要更改，是由于，方法实现逻辑比较复杂，子类没必要去实现，从父类继承就可以了
 * 
 * 一般情况下，这些final方法的内部调用了同胞接口方法，是一系列接口方法调用计算的步骤
 * 这些步骤是固定的，因此是final的
 * 
 * 所谓模板方法，实际为继承自父类的final方法
 */
public abstract class AbstractEntity {
   abstract void start();
   abstract void firstStep();
   abstract void secondStep();
   abstract void thirdStep();
   abstract void end();
 
   //模板方法
   public final void template(){
      //启动
      start(); 
      //第一步
      firstStep();
      //第二步
      secondStep();
      //第三步
      thirdStep();
      //结束
      end();
   }
}
