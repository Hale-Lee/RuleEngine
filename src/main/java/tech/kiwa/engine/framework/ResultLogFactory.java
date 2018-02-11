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

package tech.kiwa.engine.framework;

import java.util.ArrayList;
import java.util.List;

import tech.kiwa.engine.component.AbstractResultLogRecorder;
import tech.kiwa.engine.entity.ItemExecutedResult;
import tech.kiwa.engine.entity.RuleItem;
import tech.kiwa.engine.exception.RuleEngineException;


/**
 * @author Hale.Li
 * @since  2018-01-28
 * @version 0.1
 */
public class ResultLogFactory implements FactoryMethod{

	private static ResultLogFactory instance = new ResultLogFactory();

	private static List<AbstractResultLogRecorder> logList = new ArrayList< AbstractResultLogRecorder>();

	private ResultLogFactory(){

	}

	public static ResultLogFactory getInstance(){
		return instance;
	}

	public void acceptRegister(Component logger){

		logList.add((AbstractResultLogRecorder)logger);
	}

	public boolean writeLog(Object object, RuleItem item , ItemExecutedResult result) throws RuleEngineException{

		boolean bRet = true;
		for(AbstractResultLogRecorder logger:logList ){
			bRet = bRet && logger.writeLog(object, item, result);
		}

		return bRet;
	}


}
