package junit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.junit.Assert.assertEquals;

/**
 * @author liuwei
 * @date 2020-01-26 21:14
 * @desc 三方运行器测试---参数化测试运行器
 * 当类被@RunWith注释修饰, 或者类继承了一个被该注解类修饰的类,
 * JUnit将会使用这个注解所指明的运行器(runner)--而不是JUnit默认的运行器--来运行测试
 *
 * @RunWith 的参数是一个Runner类，通常是org.junit.runners.ParentRunner的子类
 * 如常见的JUnit4、Parameterized、SpringJUnit4ClassRunner
 * 其中，JUnit4是默认运行器，Parameterized是参数化测试运行器，SpringJUnit4ClassRunner则是spring框架运行器
 */
@RunWith(Parameterized.class)
public class ParameterizedRunnerTest {

    private static void print(Object msg){
        System.out.println(msg);
    }

    //静态方法，Collection参数初始化
    @Parameterized.Parameters
    public static Collection<?> data() {
        /**
         * 如果是一维数组，则每一个元素为Parameter(0)
         * 如果是二维数组，则第一列元素为Parameter(0)，第二列元素为Parameter(1)，多维数组类推
         * 通常为二维数组，第一列为待计算值，第二列为期望值
         * 在@Test方法执行时，将循环遍历每一个元素，分别测试，任何失败的测试不会影响其它参数测试的运行
         */
        return Arrays.asList(new Object[][] {
                { "1+2", 3 }, { "1+2*3", 7 }, { "123*4-567/8", 422 },
                { "1+2-3*4/5", 1 }, { "1/2*3-4+5", 1 }, { "12--34-56--78", 618 }
        });
    }

    //待计算值
    @Parameterized.Parameter(0)
    public String input;

    //期望值
    @Parameterized.Parameter(1)
    public Integer expected;

    LocalDateTime now;

    @Before
    public void before() {
        now = LocalDateTime.now();
        print("当前时间："+now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
    }

    @Test
    public void test() {
        print(">>>本次入参："+input);
        print("期望值："+expected);
        assertEquals(expected, Calculator.execute(input));
    }
}

//四则运算
class Calculator{

    //简易四则长运算，不支持括号
    public static Integer execute(String expression){
        int sum = 0;
        boolean multi = expression.contains("*") || expression.contains("/");
        if (expression.contains("--")) {
            expression = expression.replaceAll("--","+");
        }
        if (expression.contains("-")) {
            expression = expression.replaceAll("-","+-");
        }
        String[] plusArr = expression.split("\\+");
        for (String plus : plusArr) {
            String trim = plus.trim();
            if (multi && (plus.contains("*") || plus.contains("/"))) {
                sum += multi(plus);
            }else{
                sum += Integer.parseInt(trim);
            }
        }
        return sum;
    }

    //乘除运算
    private static Integer multi(String expression){
        int multiValue = 1;
        String[] multiArr = expression.split("\\*");
        for (String multiExp : multiArr) {
            if (multiExp.contains("/")) {
                String[] divideArr = multiExp.split("/");
                multiValue *= Integer.parseInt(divideArr[0].trim());
                for (int i = 1; i < divideArr.length; i++) {
                    multiValue /= Integer.parseInt(divideArr[i].trim());
                }
            }else{
                multiValue *= Integer.parseInt(multiExp.trim());
            }
        }
        return multiValue;
    }

}
