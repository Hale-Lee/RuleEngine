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

package tech.kiwa.engine.component.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tech.kiwa.engine.component.AbstractCommand;
import tech.kiwa.engine.component.AbstractRuleItem;
import tech.kiwa.engine.component.drools.DroolsBuilder;
import tech.kiwa.engine.component.drools.LocalCreator;
import tech.kiwa.engine.component.drools.RuleCreator;
import tech.kiwa.engine.entity.ItemExecutedResult;
import tech.kiwa.engine.entity.RESULT;
import tech.kiwa.engine.entity.RuleItem;
import tech.kiwa.engine.exception.RuleEngineException;

/**
 * @author Hale.Li
 * @since  2018-01-28
 * @version 0.1
 */
public class DroolsRuleExecutor extends AbstractRuleItem {

	private Logger log = LoggerFactory.getLogger(this.getClass());

	/* (non-Javadoc)
	 * @see tech.kiwa.engine.component.AbstractRuleItem#doCheck(tech.kiwa.engine.entity.RuleItem)
	 */
	@Override
	public ItemExecutedResult doCheck(RuleItem item) throws RuleEngineException {


		RuleCreator creator =  (RuleCreator) item.getAttach();
		DroolsBuilder builder = creator.getBuilder();
		builder.compile();

		boolean bRet =  creator.runCondition(this.object);

		ItemExecutedResult  checkResult = new ItemExecutedResult();

		//缺省认为是 passed
		checkResult.setResult(RESULT.EMPTY);	//通过
		checkResult.setRemark(RESULT.EMPTY.getName());
		checkResult.setContinue(ItemExecutedResult.CONTINUE);

		checkResult.setReturnValue(bRet);
		if(bRet){
			checkResult.setResult(item.getResult());
			checkResult.setRemark(checkResult.getResult().getName());
			checkResult.setContinue( Integer.parseInt(item.getContinueFlag()));
		}else{
			// add false result return.
		}

		this.executeCommand(item, checkResult);

		return checkResult;
	}


	private void executeCommand(RuleItem item, ItemExecutedResult result){

		if(!result.getReturnValue()){
			return;
		}

		try {

			if(StringUtils.isNotEmpty(item.getExecutor())){

				RuleCreator rule  = (RuleCreator)item.getAttach();

				DroolsBuilder builder = rule.getBuilder();

				builder.compile();


				AbstractCommand command = (AbstractCommand)builder.createObject(item.getExecutor(), rule.toJavaString());

				//if(command != null){
					for(LocalCreator result_value: rule.getResultList()){
						if(result_value != null && result_value.getValue() != null){
							command.SetObject(result_value.getValue());
						}
					}


					command.execute(item, result);
				//}
			}

		} catch (Exception e) {
			log.debug(e.getMessage());
			e.printStackTrace();
		}
	}

}
