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

import java.util.HashMap;

import tech.kiwa.engine.component.AbstractComparisonOperator;
import tech.kiwa.engine.exception.RuleEngineException;


/**
 * @author Hale.Li
 * @since  2018-01-28
 * @version 0.1
 */
public class OperatorFactory implements FactoryMethod{

	private static OperatorFactory instance = new OperatorFactory();

	private static HashMap<String, AbstractComparisonOperator> operatorMap = new HashMap<String, AbstractComparisonOperator>();

	public final  static class OPR_CODE {

		public static final String EQUAL = "01" ;
		public static final String GREATER = "02" ;
		public static final String LESS = "03" ;
		public static final String NOT_EQUAL = "04" ;
		public static final String GREATER_EQUAL = "05" ;
		public static final String LESS_EQUAL = "06" ;
		public static final String INCLUDE = "07" ;
		public static final String NOT_INCLUDE = "08" ;
		public static final String INCLUDED_BY = "09" ;
		public static final String NOT_INCLUDED_BY = "10" ;
		public static final String STRING_EQUAL = "11" ;
		public static final String NOTSTRING_EQUAL = "12" ;
		public static final String EQUAL_IGNORE_CASE = "13" ;
		public static final String NOT_EQUAL_IGNORE_CASE = "14" ;
		public static final String MATCH = "15" ;
		public static final String UNMATCH = "16" ;

		// alias
		public static final String CONTAINS = INCLUDE;
		public static final String NOT_CONTAINS = NOT_INCLUDE;
		public static final String MEMBER_OF = INCLUDED_BY;
		public static final String NOT_MEMBER_OF = NOT_INCLUDED_BY;

		private static final String RESERVED_CODES[] = new String[] {EQUAL,GREATER,LESS,NOT_EQUAL,GREATER_EQUAL,LESS_EQUAL,INCLUDE,NOT_INCLUDE,
				INCLUDED_BY,NOT_INCLUDED_BY,STRING_EQUAL,NOTSTRING_EQUAL,EQUAL_IGNORE_CASE,NOT_EQUAL_IGNORE_CASE,MATCH,UNMATCH,"17","18","19","20"};

		private static final String RESERVED_VALUES[] = new String[] {"EQUAL","GREATER","LESS","NOT_EQUAL","GREATER_EQUAL","LESS_EQUAL","INCLUDE",
				"NOT_INCLUDE","INCLUDED_BY","NOT_INCLUDED_BY","STRING_EQUAL","NOTSTRING_EQUAL","EQUAL_IGNORE_CASE",
				"NOT_EQUAL_IGNORE_CASE","MATCH,UNMATCH","17","18","19","20"};

		private static final String RESERVIED_ALIAS_CODES[] = new String[]{CONTAINS,NOT_CONTAINS,MEMBER_OF,NOT_MEMBER_OF,MATCH,UNMATCH};
		private static final String RESERVIED_ALIAS_VALUES[] = new String[]{"CONTAINS","NOT CONTAINS","MEMBEROF","NOT MEMBEROF","MATCHES", "NOT MATCHES"};

		private static final String RESERVED_LOGIC_ALIAS_VALUES[] = new String[]{"==",">","<","!=",">=","<="};

		public static boolean isReserved(String code){

			for(int iLoop =0 ; iLoop < RESERVED_CODES.length; iLoop++){
				if(RESERVED_CODES[iLoop].equals(code)){
					return true;
				}
			}
			return false;
		}

		public static String getCode(String value){

			String temp = null;
			for(int iLoop = 0; iLoop < RESERVED_VALUES.length; iLoop++){
				if(RESERVED_VALUES[iLoop].equalsIgnoreCase(value)){
					temp = RESERVED_CODES[iLoop];
					break;
				}
			}

			if(null == temp){
				for(int iLoop = 0; iLoop < RESERVIED_ALIAS_VALUES.length; iLoop++){
					if(RESERVIED_ALIAS_VALUES[iLoop].equalsIgnoreCase(value)){
						temp = RESERVIED_ALIAS_CODES[iLoop];
						break;
					}
				}
			}

			if(null == temp){
				for(int iLoop = 0; iLoop < RESERVED_LOGIC_ALIAS_VALUES.length; iLoop++){
					if(RESERVED_LOGIC_ALIAS_VALUES[iLoop].equalsIgnoreCase(value)){
						temp = RESERVED_CODES[iLoop];
						break;
					}
				}
			}

			return temp;
		}

		public static String getValue(String code){

			String temp = null;
			for(int iLoop = 0; iLoop < RESERVED_CODES.length; iLoop++){
				if(RESERVED_CODES[iLoop].equalsIgnoreCase(code)){
					temp = RESERVED_VALUES[iLoop];
					break;
				}
			}

			return temp;
		}
	}



	private OperatorFactory(){

	}

	public static OperatorFactory getInstance(){
		return instance;
	}

	public void acceptRegister( Component opt){
		AbstractComparisonOperator operator = (AbstractComparisonOperator)opt;
		operatorMap.put(operator.getComparisonCode(), operator);
	}

	public boolean runOperator(String subject, String comparison_code , String baseline) throws RuleEngineException{

		AbstractComparisonOperator opter = operatorMap.get(comparison_code);
		if(null == opter){
			throw new RuleEngineException("null pointer error on comparison operate execute.");
		}

		return opter.run(subject, baseline);
	}


}
