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

package tech.kiwa.engine.component;

import tech.kiwa.engine.exception.RuleEngineException;
import tech.kiwa.engine.framework.Component;
import tech.kiwa.engine.framework.OperatorFactory;

/**
 * @author Hale.Li
 * @since  2018-01-28
 * @version 0.1
 */
public abstract class AbstractComparisonOperator implements Component {

	private String comparisonCode ;

	// auto register.
	public AbstractComparisonOperator( String comparison_code) throws Exception{


		comparisonCode = comparison_code;
		this.register();

	}

	public void register() throws RuleEngineException{

		OperatorFactory optMgr = OperatorFactory.getInstance();

		if(OperatorFactory.OPR_CODE.isReserved(comparisonCode) ){
			throw new RuleEngineException("cannot use reserved logic key.");
		}

		optMgr.acceptRegister(this);
	}

	public abstract boolean run(String subject, String baseline);

	public String getComparisonCode(){
		return comparisonCode;
	}

}
