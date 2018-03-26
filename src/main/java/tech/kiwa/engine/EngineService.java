//Copyright Hale [hale2000@163.com]
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.

package tech.kiwa.engine;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tech.kiwa.engine.component.AbstractRuleItem;
import tech.kiwa.engine.component.AbstractRuleReader;
import tech.kiwa.engine.component.impl.ComplexRuleExecutor;
import tech.kiwa.engine.component.impl.DBRuleReader;
import tech.kiwa.engine.component.impl.DefaultRuleExecutor;
import tech.kiwa.engine.component.impl.DroolsRuleReader;
import tech.kiwa.engine.component.impl.XMLRuleReader;
import tech.kiwa.engine.entity.EngineRunResult;
import tech.kiwa.engine.entity.ItemExecutedResult;
import tech.kiwa.engine.entity.RESULT;
import tech.kiwa.engine.entity.RuleItem;
import tech.kiwa.engine.exception.RuleEngineException;
import tech.kiwa.engine.framework.ResultLogFactory;
import tech.kiwa.engine.sample.Student;
import tech.kiwa.engine.utility.PropertyUtil;

/**
 * @author Hale.Li
 * @since  2018-01-28
 * @version 0.1
 */
public class EngineService {


	private AbstractRuleReader itemService = loadService();

	private Logger log = LoggerFactory.getLogger(EngineService.class);

	private String seq = null;

	@SuppressWarnings("unchecked")
	private AbstractRuleReader loadService(){

		if(itemService != null){
			return itemService;
		}

		AbstractRuleReader reader = null;

		String serviceName = PropertyUtil.getProperty("rule.reader");
		switch(serviceName.toLowerCase()){
		case "database":
			reader = new DBRuleReader();
			break;
		case "xml":
			reader = new XMLRuleReader();
			break;
		case "drools":
			reader = new DroolsRuleReader();
			break;
		default:
			Class<AbstractRuleReader> ServiceClass;
			try {
				ServiceClass = (Class<AbstractRuleReader>) Class.forName(serviceName);
				reader = ServiceClass.newInstance();
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				log.debug(e.getMessage());
			}

			break;
		}

		itemService = reader;
		return reader;
	}

	public EngineRunResult start(Object object ) throws RuleEngineException{
		return this.start(object, null);
	}

	public EngineRunResult start(Map<String,Object> object ) throws RuleEngineException{
		return this.start((String)object.get("Id"), null);
	}

	@SuppressWarnings("unchecked")
	public EngineRunResult start(Object object , String sequence) throws RuleEngineException{

		List<RuleItem>	itemList  =  itemService.readRuleItemList();

		itemList = itemService.filterItem(itemList, null);
		itemList = itemService.sortItem(itemList, null);

		this.seq = sequence;

		EngineRunResult ret_Result = new EngineRunResult();
		ret_Result.setResult(RESULT.PASSED);
		ret_Result.setResult_desc("PASSED");

		for(RuleItem item : itemList){

			log.debug("started to execute the rule item check. item = {}, ObjectId ={}", item.getItemNo(), object);

			if(StringUtils.isNotEmpty(item.getGroupExpress())){

				ComplexRuleExecutor executor = new ComplexRuleExecutor();
				executor.setObject(object);
				ItemExecutedResult result  =  null;
				//如果是重复执行.
				do{
					result  = executor.doCheck(item);
					if(result == null) break;

					seq = this.writeExecutedLog(object,item, result);
					if(result.getResult().compare(ret_Result.getResult()) > 0 ){
						ret_Result.setResult(result.getResult());
						ret_Result.setResult_desc(result.getRemark());
					}
					ret_Result.setSequence(seq);
					if(!result.canBeContinue()){
						break;
					}

				}while(result != null && result.shouldLoop());

				if(!result.canBeContinue()){
					break;
				}


			}else{

				String className= item.getExeClass();

				Class<AbstractRuleItem>  auditClass = null;
				AbstractRuleItem auditInstance = null;

				if(StringUtils.isNotEmpty(className)){
					try {
						//auditInstance = CglibCaller.newInstance(AbstractRuleItem.class);

						auditClass = (Class<AbstractRuleItem>) Class.forName(className);
						if(null != auditClass){
							auditInstance = auditClass.newInstance();
						}
					} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
						throw new RuleEngineException(e.getMessage());
					}
				}

				if(null == auditInstance){
					auditInstance =  new DefaultRuleExecutor();
				}

				//直接地调用doCheck的函数。
				auditInstance.setObject(object);

				ItemExecutedResult result = null;
				//如果是重复执行.
				do{

					//CglibCaller caller = new CglibCaller();

					//caller.intercept(caller, method, args, caller);

					result  = auditInstance.doCheck(item);
					if(result == null){
						break;
					}
					seq = this.writeExecutedLog(object,item, result);
					if(result.getResult().compare(ret_Result.getResult()) > 0 ){
						ret_Result.setResult(result.getResult());
						ret_Result.setResult_desc(result.getRemark());
					}
					ret_Result.setSequence(seq);

					if(!result.canBeContinue()){
						break;
					}
				} while(result != null && result.shouldLoop());

				if(!result.canBeContinue()){
					break;
				}

			}
		}

		this.afterExecuted(ret_Result);

		return ret_Result;
	}

	protected void afterExecuted(EngineRunResult result){

	}

	protected String writeExecutedLog(Object object, RuleItem item , ItemExecutedResult result ) throws RuleEngineException{


		if(StringUtils.isEmpty(seq)){
			seq = String.valueOf(System.currentTimeMillis());
			seq = seq +  String.valueOf(new Random(1000).nextInt());
		}

		try {
			ResultLogFactory.getInstance().writeLog(object, item, result);
		} catch (RuleEngineException e) {
			log.debug("write log error.");
			throw e;
		}

		return seq;
	}



	public static void main(String[] args){

		EngineService service = new EngineService();

		try {

			for(int iLoop =0 ; iLoop < 1000; iLoop++){
				Student st = new Student();
				st.setAge(5);
				st.name = "tom";
				st.sex = 1;

				EngineRunResult result = service.start(st);
				System.out.println(result.getResult().getName());

				System.out.println(st.getAge());
			}
		} catch (RuleEngineException e) {

			e.printStackTrace();
		}

	}
}




