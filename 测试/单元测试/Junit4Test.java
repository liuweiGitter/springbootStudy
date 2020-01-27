package junit;

import org.hamcrest.collection.*;
import org.hamcrest.core.*;
import org.hamcrest.object.IsCompatibleType;
import org.hamcrest.text.IsEmptyString;
import org.hamcrest.text.IsEqualIgnoringCase;
import org.hamcrest.text.IsEqualIgnoringWhiteSpace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertThat;

/**
 * @author liuwei
 * @date 2020-01-22 18:20
 * @desc Junit4 断言测试
 * 1.一个方法中可以串行执行多个断言，若其中任何一个断言结果失败，则中断线程
 * 2.可以对整个类的所有方法进行test，也可以选择对某个方法test
 * 3.多个注解的执行顺序：@BeforeClass=>@Before=>@Test=>@After=>@Before=>@Test=>@After=>...=>@AfterClass
 */
public class Junit4Test {

    private static String trueMsg = "liuwei_true";
    private static String falseMsg = "liuwei_false";
    private static final Object NULL = null;
    private static final Object NOT_NULL = new Object();
    
    private static void print(String msg){
        System.out.println(msg);
    }

    public Junit4Test(){
        print("===构造方法");
    }

    @BeforeClass
    public static void beforeClass() {
        print("***初始化类资源：在构造方法之前执行一次");
    }

    @Before
    public void before() {
        print(">>>初始化测试资源：每个@Test方法前执行一次");
    }

    @After
    public void after() {
        print(">>>释放测试资源：每个@Test方法后执行一次");
    }

    @AfterClass
    public static void afterClass() {
        print("***释放类资源：在类测试结束前执行一次");
    }

    //布尔断言
    @Test
    public void testBoolean() {
        /**
         * assertTrue断言
         */
        //ok
        assertTrue(true);
        //java.lang.AssertionError
        //assertTrue(false);
        //ok
        assertTrue(falseMsg,true);
        //java.lang.AssertionError: liuwei_false
        //assertTrue(falseMsg,false);

        /**
         * assertFalse断言
         */
        //java.lang.AssertionError
        //assertFalse(true);
        //ok
        assertFalse(false);
        //java.lang.AssertionError: liuwei_true
        //assertFalse(trueMsg,true);
        //ok
        assertFalse(trueMsg,false);
    }

    //空值断言
    @Test
    public void testNull() {
        /**
         * assertNull断言
         */
        //ok
        assertNull(NULL);
        //java.lang.AssertionError: expected null, but was:<java.lang.Object@3834d63f>
        //assertNull(NOT_NULL);
        //ok
        assertNull(falseMsg,NULL);
        //java.lang.AssertionError: liuwei_false expected null, but was:<java.lang.Object@3834d63f>
        //assertNull(falseMsg,NOT_NULL);

        /**
         * assertNotNull断言
         */
        //java.lang.AssertionError
        //assertNotNull(NULL);
        //ok
        assertNotNull(NOT_NULL);
        //java.lang.AssertionError: liuwei_false
        //assertNotNull(falseMsg,NULL);
        //ok
        assertNotNull(falseMsg,NOT_NULL);
    }

    //等值断言
    @Test
    public void testEqual() {
        //double类型：允许误差范围内是否等值
        assertEquals(1.25d,1.17d,0.09d);
        //float类型：允许误差范围内是否等值
        assertEquals(1.25f,1.17f,0.09f);
        //long类型：是否等值
        assertEquals(12L,12L);
        //object类型：都为null或object1.equals(object2)时为true
        assertEquals(NULL,NULL);

        //8种基本类型和Object类型的数组之间的等值判断
        String[] strArr1 = new String[]{"hello","world"};
        String[] strArr2 = new String[]{"hello","world"};
        assertArrayEquals(strArr1,strArr2);

        //另外，可以自定义失败时的断言message，还可以assertNotEquals，不赘述
    }

    //等址断言
    @Test
    public void testSame() {
        //object1 == object2时为true
        assertSame(NULL,NULL);

        //另外，可以自定义失败时的断言message，还可以assertNotSame，不赘述
    }

    //主动报错和非错断言
    @Test
    public void testArray() {
        fail("auto call fail");
        assertThat("always returns true", IsAnything.anything());
    }

    //正则断言
    @Test
    public void testExpress() {
        //有很多重复断言功能，如空值断言等，不赘述

        String[] strArr = new String[]{"0","1","2"};

        //等值判断
        assertThat(0,Is.is(0));
        //类型判断：是否子类
        assertThat("0",Is.isA(Object.class));
        assertThat("0", IsInstanceOf.any(String.class));
        assertThat(Integer.class, IsCompatibleType.typeCompatibleWith(Number.class));
        //字符串
        assertThat("liuwei123", StringContains.containsString("liuwei"));
        assertThat("liuwei123", StringStartsWith.startsWith("liuwei"));
        assertThat("liuwei123", StringEndsWith.endsWith("123"));
        assertThat("", IsEmptyString.isEmptyString());
        assertThat("", IsEmptyString.isEmptyOrNullString());
        assertThat(null, IsEmptyString.isEmptyOrNullString());
        assertThat("liuwei123", IsEqualIgnoringCase.equalToIgnoringCase("Liuwei123"));
        assertThat(" liuwei123 ", IsEqualIgnoringWhiteSpace.equalToIgnoringWhiteSpace("liuwei123"));
        assertThat("0", IsIn.isIn(strArr));
        assertThat("0", IsIn.isOneOf("0","1"));
        assertThat("always returns true", IsAnything.anything());

        //Map
        Map<String,Object> map = new HashMap<>();
        map.put("key1","liuwei123");
        assertThat(map, IsMapContaining.hasKey("key1"));
        assertThat(map, IsMapContaining.hasValue("liuwei123"));

        //Collection
        Collection<String> collection = new ArrayList<String>();
        collection.add("0");
        collection.add("00");
        collection.add("000");
        assertThat(collection, IsCollectionContaining.hasItem("0"));
        //assertThat(collection, IsEmptyCollection.empty());
        assertThat(collection, IsCollectionWithSize.hasSize(3));

        //数组
        assertThat(new Integer[]{1,2}, IsArray.array(IsEqual.equalTo(1), IsEqual.equalTo(2)));
        assertThat(new Integer[]{1,2}, IsArrayContaining.hasItemInArray(1));
        assertThat(new Integer[]{1,2}, IsArrayWithSize.arrayWithSize(2));

        //逻辑
        assertThat("liuwei", AnyOf.anyOf(IsIn.isIn(strArr),StringContains.containsString("liuwei")));
        assertThat("liuwei", AllOf.allOf(StringContains.containsString("liu"),StringContains.containsString("wei")));
        assertThat(collection, Every.everyItem(StringContains.containsString("0")));

    }

    //异常测试
    @Test(expected = ArithmeticException.class)
    public void testException() {
        print("当方法抛出期望异常时通过，否则不通过："+(1 / 0));
    }

    //超时测试
    @Test(timeout = 1000)
    public void testTimeout() {
        print("当方法超时时不通过，单位毫秒");
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        print("方法通过");
    }
}
