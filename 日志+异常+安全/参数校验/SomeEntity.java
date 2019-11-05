package com.telecom.js.noc.hxtnms.operationplan.entity;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * @author liuwei
 * @date 2019-11-05 09:59
 * @desc 实体类参数校验
 * 请求控制方法中免去常规参数校验，在请求的数据对象实体类中加入校验
 * 使用开源或自定义的注解，切面校验，失败时抛出异常到前端
 * 本例中使用的校验注解类位于javax.validation.constraints包下
 * 对于开放接口，需要提供后端强校验
 * 对于己方前端的内部调用，可以放松对前端的校验
 *
 * 注意：实体类对象一定要有开放的getter和setter方法，否则参数无法赋值，实体类对象将为空对象
 * 关于正则表达式，参见https://www.runoob.com/java/java-regular-expressions.html
 */
@Data
public class SomeEntity {

    private String id;

    @NotBlank(message = "名称不能为null且不能为空字符串也不能为纯空格")
    @Size(min = 2,max = 10,message = "名称长度需要在2-10之间")
    private String name;

    /**
     * 如果需要非空校验，非空注解需要单独添加，否则将允许为空，并且为空时不再校验其它约束
     */
    @Min(value = 18,message = "年龄不能低于18岁")
    @Max(value = 60,message = "年龄不能高于60岁")
    @NotNull
    private Integer age;

    @Pattern(regexp = "female|male", message = "无效的性别")
    @NotNull
    private String gender;

    @NotNull(message = "地址不能为null")
    private String address;

    @NotEmpty(message = "部门不能为null且不能为空字符串")
    private String depart;

    @PositiveOrZero(message = "工作月长非负")
    private Integer workMonth;

    @PastOrPresent(message = "入职时间不能超过当前时间")
    private LocalDateTime serviceStartTime;

    @Digits(integer = 10, fraction = 0,message = "工号必须为数字，长度不超过10位")
    private String workNum;

}
