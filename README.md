### RuleEngine
one of the best simple rule engine, easy to use, can define different format of the rule, such as xml, drools, database.

###使用方法

#1， 在POM.XML文件中添加下面的内容.

        <dependency>
            <groupId>com.github.hale-lee</groupId>
            <artifactId>RuleEngine</artifactId>
            <version>0.1.0</version>
        </dependency>


#2, 配置ruleEngine.properties文件

rule.reader=xml/drools/database

  -2.1 若选择xml格式的规则文件，那么rule.reader=xml，此时需要设置xml.rule.filename=ruleconfig.xml
     <br>
   -2.2 若选择将规则文件定义存放在数据库中，那么设置rule.reader=database，此时需要设置db.rule.table=表名  （存放规则定义的表格，其格式可以参考SQL文件夹下的rule-mysql.sql或rule-oracle.sql）同时需要配置或者引用现有框架的jdbc配置, RuleEngine支持直接的jdbc数据库，也支持druid的数据库连接池，还可以直接引用外部框架的的数据库链接，比如spring-mvc的数据库链接。
       <br>
   -2.3 若选择使用drools格式的规则文件，则设置rule.reader=drools，同时需要设置drools.rule.filename=sample.drl
       <br>

 #3，引用调用

   直接import EngineService类，生成EngineService对象，同时将需要校验的bean作为Object传入给EngineService对象的Start方法。
   如下所示：

		EngineService service = new EngineService();

		try {

			Student st = new Student();
			st.setAge(5);
			st.name = "tom";
			st.sex = 1;

			EngineRunResult result = service.start(st);
			System.out.println(result.getResult().getName());

			System.out.println(st.getAge());
		} catch (RuleEngineException e) {

			e.printStackTrace();
		}


     #4，编写规则
      
 <br>

        -4.1 若2.1选择了xml格式的规则，则需要配置xml.rule.filename项目，这个地方填写规则的文件名，需要指定为xml文件格式。
典型的规则项目为：

    <rule id="totallist" exe_class="" method="" parent="">
        <property name="content" value="客户身份证号码规则"/>
        <property name="result" value="RESULT.REJECTED" desc="拒绝"/>
        <property name="continue_flag" value="1"/>
        <property name="group_express" value="(blacklist || graylist)"/>
		<property name="priority" value="00010"/>
    </rule>

        -4.2 若2.2选择了drools格式的规则，则需要配置drools.rule.filename项目，这个地方填写规则的文件名，需要指定为drl文件格式。
典型的规则项目为：
      <br>

    rule "ageUp12"
	 salience 400
	 when
		$student: Student(age < 8)
		 /* antoher rule */
	 then
		System.out.println("I was called, my name is : " + $student.name);
		ageUp($student,12);
		//callOver($student);
	 end
 
	<br>
        -4.3 若2.3选择database格式的规则，则需要配置db.rule.table项目，这个地方填写规则的数据库表结构，其表生成的结构可以参考SQL目录下的2个文件。典型 的规则描述如下：
	       <br>

		item_no|content|exe_sql|exe_class|param_name|param_type|comparison_code|comparison_value|baseline|result|executor|priority|continue_flag|parent_item_no|group_express|remark|comments|enable_flag|create_time|update_time
	   11|黑名单|select count(1) as cnt from tl_blacklist where customer_no = ? and delete_flag = 1|customer_no|java.lang.String|01|==|0|PASSED|100|1|1|2018-02-26 12:40:15.000000|2018-02-26 12:40:18.000000

<br>
          &nbsp;&nbsp;&nbsp; &nbsp;数据库连接方式时，需要同时设置db.accesser，如果是直接使用druid的数据库连接池，可以设置成db.accesser=tech.kiwa.engine.utility.DirectDBAccesser，DirectDBAccesser提供了开关变量UseDruid，如果设置成true就是使用了druid连接池，如果设置成false则是直接地jdbc。
     <br> &nbsp;&nbsp;&nbsp;&nbsp;   如果使用Spring的数据库连接，可以设置成db.accesser=tech.kiwa.engine.utility.SpringDBAccesser.






https://github.com/Hale-Lee/RuleEngine/wiki

