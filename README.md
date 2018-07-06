# mybatis-q
mybatis code generator / mybatis代码生成工具

## Sample Usage

### pom.xml -- database first

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.github</groupId>
            <artifactId>mybatis-q-maven-plugin</artifactId>
            <version>0.0.1</version>
            <dependencies>
                <dependency>
                    <groupId>mysql</groupId>
                    <artifactId>mysql-connector-java</artifactId>
                    <version>5.1.42</version>
                </dependency>
            </dependencies>
            <configuration>
                <entityPackage>com.github.mybatisq.entity</entityPackage><!--entity class package-->
                <genPackage>com.github.mybatisq.mapper</genPackage><!--mybatis mapper interface package-->
                <mapperFolder>mybatis-mapper</mapperFolder><!--mybatis xml mapper file location in src/main/resources folder-->
                <!--database connection (currently only mysql supported)-->
                <datasource>
                    <driverClassName>com.mysql.jdbc.Driver</driverClassName>
                    <url>jdbc:mysql://127.0.0.1:3306/test?useSSL=false&amp;useUnicode=true&amp;characterEncoding=UTF-8</url>
                    <username>root</username>
                    <password>123456</password>
                </datasource>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### pom.xml -- code first

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.github</groupId>
            <artifactId>mybatis-q-maven-plugin</artifactId>
            <version>0.0.1</version>
            <configuration>
                <entityPackage>com.github.mybatisq.entity</entityPackage><!--entity class package (entities must be defined first) -->
                <genPackage>com.github.mybatisq.mapper</genPackage><!--mybatis mapper interface package-->
                <mapperFolder>mybatis-mapper</mapperFolder><!--mybatis xml mapper file location in src/main/resources folder-->
            </configuration>
        </plugin>
    </plugins>
</build>
```

### maven build

```bash
mvn mybatis-q:gencode
```

### example usage

#### example database definition:

```sql
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

CREATE TABLE `department` (
  `dept_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `dept_no` varchar(45) NOT NULL COMMENT '部门编号',
  `dept_name` varchar(45) NOT NULL COMMENT '部门名称',
  `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  PRIMARY KEY (`dept_id`)
) COMMENT='部门';

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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COMMENT='员工所在部门';

CREATE TABLE `emp_post` (
  `ep_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `emp_id` int(11) NOT NULL COMMENT '员工id',
  `post_id` int(11) NOT NULL COMMENT '职位id',
  PRIMARY KEY (`ep_id`)
) COMMENT='员工职位';
```

#### example sql operation based on generated codes

```java
class EmployeeRepository {
    
    public List<Employee> listAll() {
        EmployeeTable e = EmployeeTable.employee;
        DepartmentTable d = DepartmentTable.department;
        PositionTable p = PositionTable.position;
        EmpDeptTable ed = EmpDeptTable.empDept;
        EmpPostTable ep = EmpPostTable.empPost;
         
        //select e.* from employee e
        //inner join empDept ed on e.empId = ed.empId
        //inner join empPost ep on e.empId = ep.empId
        //inner join position p on ep.postId = p.postId and p.postId = 1
        //inner join dept d on ed.deptId = d.deptId and d.deptId = 1
        //where e.empNo in ('e001', 'e002', 'e003') and e.salary >= 5000.00
        //order by e.createDate desc
        //limit 10, 5
        return employeeMapper.select(e.query()
                .join(e.inner(ed)
                        .on(e.empId.eq(ed.empId)))
                .join(e.inner(ep)
                        .on(e.empId.eq(ep.empId)))
                .join(ep.inner(p)
                        .on(ep.postId.eq(p.postId))
                        .and(p.postId.eq(1)))
                .join(ed.inner(d)
                        .on(ed.deptId.eq(d.deptId))
                        .and(d.deptId.eq(1)))
                .where(e.empNo.in(Arrays.asList("e001", "e002", "e003")))
                .where(e.salary.ge(new BigDecimal(5000.00)))
                .orderBy(e.createDate.desc())
                .skip(10)
                .limit(5));
    }
}
```
	        
You can find out more in the source codes.