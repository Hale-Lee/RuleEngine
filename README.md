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
    
    https://github.com/Hale-Lee/RuleEngine/wiki
