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

package tech.kiwa.engine.component.drools;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.util.StringUtils;

import tech.kiwa.engine.component.AbstractRuleItem;
import tech.kiwa.engine.exception.RuleEngineException;

/**
 * @author Hale.Li
 * @since  2018-01-28
 * @version 0.1
 */
public class QueryCreator implements DroolsPartsCreator {

	private String name;
	private String content;
	private Map<String, String> params = null;
	private LocalCreator result = null;
	private ConstraintCreator condition = null;

	private Logger log = LoggerFactory.getLogger(QueryCreator.class);

	//private Object object = null;
	private DroolsBuilder builder = null;

	public ConstraintCreator getCondition() {
		return condition;
	}

	public DroolsBuilder getBuilder(){
		return builder;
	}

	public String getName() {
		return name;
	}

	public String getContent() {
		return content;
	}


	public Map<String, String> getParams() {
		return params;
	}

	public LocalCreator getResult() {
		return result;
	}

	private QueryCreator(){

	}



	public static  QueryCreator create(String content, DroolsBuilder builder){

		if(StringUtils.isEmpty(content)){
			return null;
		}

		if(!content.startsWith("query")){
			return null;
		}

    	content = content.trim();

    	QueryCreator creator = new QueryCreator();
    	creator.builder = builder;

    	String[] lines = content.split("\\n|\\r");
    	String title = lines[0].trim();

    	String[] sections = title.split("\\s+|\\t|\\(|,|\\)");

    	if(sections.length >= 2){

    		creator.name = sections[1];
    		if(creator.name.startsWith("\"") && creator.name.endsWith("\"")){
    			creator.name = creator.name.substring(1, creator.name.length()-1);
    		}

    		if(title.endsWith(")")){
    			creator.params = new HashMap<String, String>();

    			String type = null, name =null;
    			for(int iLoop = 2; iLoop < sections.length; iLoop ++){
    				if(StringUtils.isEmpty(sections[iLoop])){
    					continue;
    				}
    				if(type == null){
    					type = sections[iLoop];
    				}else{
    					name = sections[iLoop];
    				}
    				if(type != null && name != null){
    					creator.params.put(name,type);
    					type = null;
    					name = null;
    				}
    			}
    		}

    		int pos = title.length() + "\\n".length();

    		creator.content = content.substring(pos );
    		creator.content  = creator.content.trim();
    		if(creator.content.endsWith("end")){
    			creator.content = creator.content.substring(0, creator.content.length()-"end".length());
    			creator.content  = creator.content.trim();
    		}

    		if(creator.content.contains(":")){
    			sections = creator.content.split(":");
    			creator.result = new LocalCreator();
    			creator.result.setName(sections[0].trim());
    			pos = sections[1].indexOf("(");
    			//sections = sections[1].split("\\(");
    			creator.result.setReference(sections[1].substring(0, pos));

    			if( sections[1].endsWith(")")){
    				 sections[1] =  sections[1].substring(pos, sections[1].length() );
    				 sections[1] = sections[1].trim();
    			}
    			creator.condition =  ConstraintCreator.create(sections[1], creator);
    		}
    	}

    	return creator;

    }

	public boolean run(Object object) throws RuleEngineException {

		boolean bRet = false;

		//this.object = object;

		String className = result.getReference();

		if (object.getClass().getName().equals(className)) {

			try {
				bRet = condition.executeExpress(object);
			} catch (RuleEngineException e) {
				log.debug(e.getMessage());
				throw e;
			}

		}

		return bRet;
	}

    public boolean operateCallback(String leftValue , String comparisonCode ,String baseValue, Object object) throws RuleEngineException{

    	boolean bRet = false;

		try {

			Class<?> retClass = object.getClass();
			Field  field = retClass.getDeclaredField(leftValue);
			Object value  = null;
			if(field!=null && field.isAccessible()){
				value = field.get(object);
			}else {
				String methodName = "get"+ leftValue.substring(0,1).toUpperCase() + leftValue.substring(1);
				Method method = retClass.getMethod(methodName,(Class<?>[])null);
				if(method != null ){
					value = method.invoke(object, (Object[]) null);
				}
			}

			if(value != null){
				bRet = AbstractRuleItem.comparisonOperate(value.toString(), comparisonCode, baseValue);
			}
		} catch (IllegalAccessException | NoSuchFieldException | SecurityException | NoSuchMethodException | IllegalArgumentException | InvocationTargetException e  ) {

			throw new RuleEngineException(e.getCause());
		}

		return bRet;
    }

	@Override
	public String toJavaString() {

		StringBuffer java = new StringBuffer();
		java.append("boolean bResult = false;\n");
		java.append("bResult = (");

		String constraint = condition.toJavaString();
		constraint.replace("?", result.getName());

		java.append(constraint);

		java.append(")");

		return java.toString();

	}
}
