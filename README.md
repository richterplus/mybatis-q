# mybatis-q 1.0-SNAPSHOT
mybatis code generator / mybatis代码生成工具

#### 目录

- [简介](#briefing)
  - [为什么需要mybatis-q](#why)
- [安装](#install)
  - [添加依赖项](#dependency)
  - [添加插件](#plugin)
  - [执行maven命令](#run)
- [使用代码模板](#usage)
  - [数据库示例](#database)
  - [SELECT查询](#select)
    - [支持的查询条件清单](#query)
    - [已知的限制](#limitation)
    - [使用SELECT COUNT](#count)
  - [INSERT插入](#insert)
    - [批量INSERT](#batchInsert)
    - [通过SELECT来INSERT](#insertBySelect)
  - [UPDATE更新](#update)
    - [批量UPDATE](#batchUpdate)
    - [使用UpdateBuilder](#updateBuilder)
  - [DELETE删除](#delete)
    - [自定义WHERE条件](#deleteByQuery)
- [添加自定义的xml映射](#custom)

<a name="briefing"></a>

## 简介

mybatis-q是一个基于maven插件方式工作的针对[mybatis](https://github.com/mybatis/mybatis-3)以及[mysql](https://www.mysql.com/)数据库的代码生成工具。使用mybatis-q，从现有的mysql数据库生成类型安全的CRUD代码模板，覆盖从SELECT查询、INSERT插入、UPDATE更新以及DELETE删除的大量场景。这些模板均经过多年实战的经验总结得出，具备高可读性、高易用性，使得开发人员可以立即获得高质量和高效的DAO代码模板。

<a name="why"></a>

### 为什么需要mybatis-q

区别于一般的mybatis生成工具，mybatis-q提供了一个支持类型安全的、具备高可读性的、支持编译器检查的、功能强大的SQL构建器，并且mybatis-q具备良好的扩展性，你可以方便地自己撰写xml映射文件，并融入mybatis-q已有的SQL构建功能。

<a name="install"></a>

## 安装

> mybatis-q基于maven工作，你需要正确地配置pom.xml文件

<a name="dependency"></a>

### 添加依赖项

```xml
<dependency>
    <groupId>com.github</groupId>
    <artifactId>mybatis-q-core</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

<a name="plugin"></a>

### 添加插件

```xml
<plugin>
    <groupId>com.github</groupId>
    <artifactId>mybatis-q-maven-plugin</artifactId>
    <version>1.0-SNAPSHOT</version>
    <dependencies>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.12</version>
        </dependency>
    </dependencies>
    <configuration>
        <entityPackage>com.github.mybatisq.entity</entityPackage><!--实体类会生成到该package内-->
        <genPackage>com.github.mybatisq.mapper</genPackage><!--Mapper接口会生成到该package内-->
        <mapperFolder>mybatis-mapper</mapperFolder><!--xml映射文件会生成到resource的该目录下-->
        <datasource>
            <driverClassName>com.mysql.cj.jdbc.Driver</driverClassName>
            <url>jdbc:mysql://127.0.0.1:3306/mybatis-q?useSSL=false&amp;serverTimezone=Asia/Shanghai&amp;allowPublicKeyRetrieval=true</url> <!--数据库连接字符串-->
            <username>root</username><!--数据库用户名-->
            <password>123456</password><!--数据库密码-->
        </datasource>
    </configuration>
</plugin>
```

<a name="run"></a>

### 执行maven命令

> 执行下面的命令（你也可以使用ide菜单执行maven命令）后，会根据pom.xml文件中设置的数据库连接、包和路径生成代码

```bash
mvn mybatis-q:gencode
```

<a name="usage"></a>

## 使用代码模板

<a name="database"></a>

### 数据库示例

> 本文档中的举例都基于以下数据库示例

```sql
CREATE TABLE `department` (
  `dept_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `dept_no` varchar(45) NOT NULL COMMENT '部门编号',
  `dept_name` varchar(45) NOT NULL COMMENT '部门名称',
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  PRIMARY KEY (`dept_id`)
) COMMENT='部门';
CREATE TABLE `employee` (
  `emp_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '员工id',
  `emp_no` varchar(45) NOT NULL COMMENT '工号',
  `emp_name` varchar(45) NOT NULL COMMENT '员工姓名',
  `is_fulltime` bit(1) NOT NULL COMMENT '是否全职',
  `serial_no` bigint(20) DEFAULT NULL COMMENT '序列号',
  `gender` int(11) NOT NULL COMMENT '性别（1:男，2:女）',
  `birthday` datetime DEFAULT NULL COMMENT '出生年月',
  `height` float DEFAULT NULL COMMENT '身高',
  `weight` double DEFAULT NULL COMMENT '体重',
  `salary` decimal(10,2) DEFAULT NULL COMMENT '薪资',
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  PRIMARY KEY (`emp_id`)
) COMMENT='人员';
CREATE TABLE `position` (
  `post_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '岗位id',
  `post_no` varchar(45) NOT NULL COMMENT '岗位编号',
  `post_name` varchar(45) NOT NULL COMMENT '岗位名称',
  PRIMARY KEY (`post_id`)
) COMMENT='岗位';
CREATE TABLE `emp_dept` (
  `ed_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `emp_id` int(11) NOT NULL COMMENT '员工id',
  `dept_id` int(11) NOT NULL COMMENT '部门id',
  PRIMARY KEY (`ed_id`)
) COMMENT='员工所在部门';
CREATE TABLE `emp_post` (
  `ep_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `emp_id` int(11) NOT NULL COMMENT '员工id',
  `post_id` int(11) NOT NULL COMMENT '职位id',
  PRIMARY KEY (`ep_id`)
) COMMENT='员工职位';
```

<a name="select"></a>

### SELECT查询

> mybatis-q为每张表生成`{表名}Mapper`接口，使用该接口中的方法执行SELECT查询，
> 在上面的示例中，工具已经生成了`EmployeeMapper`等5个Mapper，
> 生成的代码模板是`类型安全`的，如果类型不匹配，则编译器会报错

```java
//该示例用来展示如何使用select方法来执行SELECT查询
public class SampleUsage {
    
    @Autowired
    private EmployeeMapper employeeMapper;
    
    //展示一些典型的示例
    public void showcase() {
        EmployeeTable emp = EmployeeTable.employee; //为employee表设置一个简短的别名，方便后面代码的撰写
        
        //一个简单的主键查询
        //select emp.xxx ... from employee emp where emp.emp_id = 1
        List<Employee> employees = employeeMapper.select(emp.query().where(emp.emp_id.eq(1)));
        
        //组合多个条件，包含排序、分页，并只返回特定的列
        //select emp.emp_id, emp.emp_name, emp.is_fulltime from employee emp where emp.height >= 150 and emp.weight <= 80 order by emp.emp_no asc, emp.create_date desc limit 20, 10
        employees = employeeMapper.select(
                        emp.query()
                                .columns(emp.emp_id, emp.emp_name, emp.is_fulltime)
                                .where(emp.height.ge(150f))
                                .where(emp.weight.le(80d))
                                .orderBy(emp.emp_no.asc())
                                .orderBy(emp.create_date.desc())
                                .limit(10).skip(20));
        
        //连接emp_dept和department表的查询
        EmpDeptTable ed = EmpDeptTable.emp_dept;
        DepartmentTable dept = DepartmentTable.department;

        //select emp.xxx ... from employee emp inner join emp_dept ed on emp.emp_id = ed.emp_id and ed.dept_id > 0 inner join department dept on ed.dept_id = dept.dept_id where emp.emp_id > 0
        employees = employeeMapper.select(
                emp.query()
                        .join(emp.inner(ed)
                                .on(emp.emp_id.eq(ed.emp_id))
                                .and(ed.dept_id.gt(0)))
                        .join(ed.inner(dept).on(ed.dept_id.eq(dept.dept_id)))
                        .where(emp.emp_id.gt(0)));
    }
}
```

<a name="query"></a>

#### 支持的查询条件清单

方法 | 说明 | 举例 | 对应的SQL语句
:-- | :-- | :-- | :--
Column.eq | 等于 | a.id.eq(10) | a.id = 10
Column.eqAnother | 等于另一个字段的值 | a.create_date.eq(a.update_date) | a.create_date = a.update_date
Column.gt | 大于 | a.amount.gt(5000) | a.amount > 5000
Column.ge | 大于等于 | a.amount.ge(5000) | a.amount >= 5000
Column.lt | 小于 | a.amount.lt(10000) | a.amount < 10000
Column.le | 小于等于 | a.amount.le(10000) | a.amount <= 10000
Column.between | 介于两个值之间 | a.amount.between(5000, 10000) | a.amount between 5000 and 10000
Column.in | 包含指定的值 | a.id.in(Arrays.asList(5, 10, 15, 20)) | a.id in (5, 10, 15, 20)
Column.notIn | 不包含指定的值 | a.id.notIn(Arrays.asList(100, 200, 300)) | a.id not in (100, 200, 300)
Column.startWith | 字符串以指定的字符开始 | a.name.startWith("王") | a.name like '王%'
Column.endWith | 字符串以指定的字符结尾 | a.name.endWith("国") | a.name like '%国'
Column.contains | 字符串包含指定的字符 | a.name.contains("中") | a.name like '%中%'
Column.isNull | 为空 | a.alias.isNull() | a.alias is null
Column.notNull | 不为空
Column.asc | 升序排序 | a.create_date.asc() | order by a.create_date asc
Column.desc | 降序排序 | a.create_date.desc() | order by a.create_date desc
Query.limit和Query.skip | 分页 | .limit(10).skip(20) | limit 20, 10
Query.columns | 选择列 | .columns(a.id, a.name, a.amount) | select a.id, a.name, a.amount
Query.join | 表连接 | .join(a.inner(b).on(a.id.eq(b.id)).and(b.quantity.ge(100))) | a inner join b on a.id = b.id and b.quantity >= 100

<a name="limitation"></a>

#### 已知的限制

* 由于mybatis需要事先配置好映射(ResultMap)，当前版本不支持在表连接时，从其他表返回数据。（当然可以通过在数据库里设置好外键关联，然后在生成代码时根据外键关联来建立映射关系，但目前不考虑这么做）
* 不支持group by等或者其他复杂的SQL语句，需要撰写复杂SQL语句仍然需要自己写xml映射文件，但可以充分利用mybatis-q生成的QMapper中预先定义好的SQL语句并利用mybatis-q相关的类来简化工作，具体请参考[添加自定义的xml映射](#custom)

<a name="count"></a>

#### 使用SELECT COUNT

> 由于SELECT COUNT的功能与SELECT基本一致，直接使用Mapper.count方法即可，使用方式与Mapper.select完全相同，并且可以复用Query查询对象。
> 如果在Query中指定了limit、skip、orderBy，Mapper.count方法会自动忽略这些设置

<a name="insert"></a>

### INSERT插入

* mybatis-q为每张表生成了一个对应的实体类
* 使用Mapper.insert方法，并且传入一个实体类的实例即可完成插入
* 忽略值为null的字段
* 支持自动填充自增字段的值

<a name="batchInsert"></a>

#### 批量INSERT

* 使用Mapper.batchInsert方法，并且传入实体类的实例集合
* 不会忽略值为null的字段
* 支持自动填充自增字段的值
* 由于不同的数据库能够支持的最大参数个数存在差异，请在编程时自行控制传入实例的数量

<a name="insertBySelect"></a>

#### 通过SELECT来INSERT

* 使用Mapper.insertBySelect方法，来通过select来进行插入
* 该功能目前仅仅是前期版本，许多常见的功能都没有完全实现

```java
//该示例用来展示如何使用select方法来执行INSERT插入
public class SampleUsage {
    
    @Autowired
    private EmployeeMapper employeeMapper;
    
    //展示一些典型的示例
    public void showcase() {
        EmployeeTable emp = EmployeeTable.employee; //为employee表设置一个简短的别名，方便后面代码的撰写
        
        //insert into employee (emp_no, emp_name, is_fulltime, gender, create_date) select emp_no, emp_name, is_fulltime, gender, create_date from employee emp where emp.emp_id > 0
        employeeMapper.insertBySelect(
                emp.insert()
                        .columns(emp.emp_no, emp.emp_name, emp.is_fulltime, emp.gender, emp.create_date)
                        .select(emp.query()
                                .columns(emp.emp_no, emp.emp_name, emp.is_fulltime, emp.gender, emp.create_date)
                                .where(emp.emp_id.gt(0))
                                .limit(10)));
    }
}
```

<a name="update"></a>

### UPDATE更新

* mybatis-q为每张表生成了一个对应的实体类
* 使用Mapper.update方法，并且传入一个实体类的实例即可完成插入
* 忽略值为null的字段
* 根据主键的值来更新数据

<a name="batchUpdate"></a>

#### 批量UPDATE

* 使用Mapper.batchUpdate方法，并且传入实体类的实例集合
* 不会忽略值为null的字段
* 基于case when实现
* 由于不同的数据库能够支持的最大参数个数存在差异，请在编程时自行控制传入实例的数量

<a name="updateBuilder"></a>

#### 使用UpdateBuilder

* 使用Mapper.updateByBuilder方法来自行构建SQL语句

```java
//该示例用来展示如何使用updateBuilder
public class SampleUsage {
    
    @Autowired
    private EmployeeMapper employeeMapper;
    
    //展示一些典型的示例
    public void showcase() {
        EmployeeTable emp = EmployeeTable.employee; //为employee表设置一个简短的别名，方便后面代码的撰写
        
        //update employee set create_date = now(), serial_no = serial_no + 10 where emp_id > 10 and serial_no > 0
        employeeMapper.updateByBuilder(
                emp.update()
                        .set(emp.create_date, new Date())
                        .set(emp.serial_no, NumberOps.plus(10L))
                        .where(emp.emp_id.gt(10))
                        .where(emp.serial_no.gt(0L)));
    }
}
```

<a name="delete"></a>

### DELETE删除

* 使用Mapper.delete方法，并传入主键的值来删除单行数据

<a name="deleteByQuery"></a>

#### 自定义WHERE条件

* 使用Mapper.deleteByQuery方法，进行自定义WHERE条件的批量删除

```java
//该示例用来展示如何使用deleteByQuery
public class SampleUsage {
    
    @Autowired
    private EmployeeMapper employeeMapper;
    
    //展示一些典型的示例
    public void showcase() {
        EmployeeTable emp = EmployeeTable.employee; //为employee表设置一个简短的别名，方便后面代码的撰写
        
        //delete from employee where gender = 1 and emp_no like 'A%'
        employeeMapper.deleteByQuery(emp.deleteQuery().where(emp.gender.eq(1)).where(emp.emp_no.startWith("A")));
    }
}
```

<a name="custom"></a>

## 添加自定义的xml映射

> 有时候，我们需要实现诸如
> select count(*), d.dept_name from employee e
> inner join emp_dept ed on e.emp_id=d.emp_id
> inner join department d on ed.dept_id=d.dept_id
> where e.emp_id>100 group by d.dept_name
> 之类的mybatis-q模板无法支持的语句，此时需要自己编写xml映射文件

我们以上面的例子为例，来编写一个自定义xml映射文件，首先，我们添加一个EmployeeCountByDepartment类，用来映射数据

```java
package com.github.mybatisq.entity;

public class EmployeeCountByDepartment {
    private Integer count;
    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }
    private String departmentName;
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
}
```

接下来编写xml映射文件

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="com.github.mybatisq.mapper.custom.EmployeeMapper2">
    <resultMap id="employeeCountByDepartment" type="com.github.mybatisq.entity.EmployeeCountByDepartment">
        <result column="count" property="count"/>
        <result column="dept_name" property="departmentName"/>
    </resultMap>
    <select id="countGroupByDeptName" parameterType="com.github.mybatisq.Query" resultMap="employeeCountByDepartment">
        select count(*) as count, dept_name <include refid="com.github.mybatisq.mapper.QMapper.selectFrom"/> group by gender
    </select>
</mapper>
```

添加Mapper接口

```java
package com.github.mybatisq.mapper.custom;
import java.util.List;
import com.github.mybatisq.entity.EmployeeCountByDepartment;
import com.github.mybatisq.mapper.EmployeeTable;
@Mapper
public interface EmployeeMapper2 {
    List<EmployeeCountByDepartment> countGroupByDeptName(Query<EmployeeTable> query);
}
```

使用Mapper接口

```java
//该示例用来展示如何使用自定义的Mapper
public class SampleUsage {
    
    @Autowired
    private EmployeeMapper2 employeeMapper2;
    
    //展示一些典型的示例
    public void showcase() {
        EmployeeTable e = EmployeeTable.employee;
        EmpDeptTable ed = EmpDeptTable.empDept;
        DepartmentTable d = DepartmentTable.department;
        
        // select count(*), d.dept_name from employee e
        // inner join emp_dept ed on e.emp_id=d.emp_id
        // inner join department d on ed.dept_id=d.dept_id
        // where e.emp_id>100 group by d.dept_name
        List<EmployeeCountByDepartment> counts = employeeMapper2.countGroupByDeptName(
                emp.query()
                        .join(e.inner(ed).on(e.emp_id.eq(ed.emp_id)))
                        .join(ed.inner(d).on(ed.dept_id.eq(d.dept_id)))
                        .where(emp.emp_id.gt(100)));
    }
}
```

> 在这里例子中，配合`com.github.mybatisq.Query`对象，我们复用了`QMapper.xml`预先定义好的`selectFrom`语句，仅仅需要自己编辑新的ResultMap和创建一个新的entity类，就能够复用大部分的模板查询功能。
> 在创建自定义的xml映射文件时，建议翻看QMapper.xml映射文件的源代码，以便尽可能的复用已有的SQL语句，从而复用大部分的SQL构建能力




