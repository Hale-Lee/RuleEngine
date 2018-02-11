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
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.util.StringUtils;

import tech.kiwa.engine.component.AbstractRuleItem;
import tech.kiwa.engine.entity.ItemExecutedResult;
import tech.kiwa.engine.entity.RESULT;
import tech.kiwa.engine.entity.RuleItem;
import tech.kiwa.engine.exception.RuleEngineException;
/**
 * @author Hale.Li
 * @since  2018-01-28
 * @version 0.1
 */
public class RuleCreator implements DroolsPartsCreator {

	private Logger log = LoggerFactory.getLogger(RuleCreator.class);

	private RuleItem item = new RuleItem();

	private  List<Condition> conditionList = new ArrayList<Condition>();

	private String command;

	public RuleItem getItem() {
		return item;
	}

	public class Condition{
		LocalCreator result;
		ConstraintCreator condition;
	}

	//private Object object = null;

	@Override
	public String toJavaString() {

		StringBuffer javaBuffer = new StringBuffer();

		javaBuffer.append(builder.getPackage().toJavaString());
		javaBuffer.append("\n");

		for(ImportCreator impt : builder.getImportList()){
			javaBuffer.append(impt.toJavaString());
		}

		for(DeclareCreator decl : builder.getDeclareList()){
			javaBuffer.append("import " + builder.getPackage().getName() +".");
			javaBuffer.append(decl.getName());
			javaBuffer.append(";\n");
		}

		javaBuffer.append("\n");
		javaBuffer.append("import ");
		javaBuffer.append(builder.getPackage().getName() +".");
		javaBuffer.append("DroolsFunctions");
		javaBuffer.append(";\n");


		javaBuffer.append("import ");
		javaBuffer.append("tech.kiwa.engine.component.AbstractCommand;\n");
		javaBuffer.append("import ");
		javaBuffer.append("tech.kiwa.engine.component.drools.DroolsPartsObject;\n");

		javaBuffer.append("import ");
		javaBuffer.append("tech.kiwa.engine.entity.ItemExecutedResult;\n");

		javaBuffer.append("import ");
		javaBuffer.append("tech.kiwa.engine.entity.RuleItem;\n");
/*
		for (Condition cond : conditionList){
			if(cond.result != null && !StringUtils.isEmpty(cond.result.getName()) ){

				javaBuffer.append("import ");
				javaBuffer.append(cond.result.getReference());
				javaBuffer.append(";\n");
			}
		}
*/
		javaBuffer.append("public class ");
		javaBuffer.append(item.getItemNo().substring(0, 1).toUpperCase());
		javaBuffer.append(item.getItemNo().substring(1));
		javaBuffer.append("Command");

		javaBuffer.append(" extends AbstractCommand implements DroolsPartsObject ");

		javaBuffer.append(" {").append("\n");
		javaBuffer.append("\n");

		for (Condition cond : conditionList){
			if(cond.result != null && !StringUtils.isEmpty(cond.result.getName()) ){
				javaBuffer.append("private ");
				javaBuffer.append(cond.result.getReference());
				javaBuffer.append(" ");
				javaBuffer.append(cond.result.getName());
				javaBuffer.append(";\n");
			}
		}
		javaBuffer.append("public void execute(RuleItem item, ItemExecutedResult result){\n");
		javaBuffer.append("DroolsFunctions fun = new DroolsFunctions();\n");;

		String[] lines = command.split("\\r\\n");
		for(int iLoop =0; iLoop < lines.length; iLoop++){
			String line = lines[iLoop].trim();

			if(line.endsWith(";")){
				line = line.substring(0, line.length()-1);
				line = line.trim();

				int pos = line.indexOf('(');
				if(line.endsWith(")") && pos > 0){

					String funName = line.substring(0,pos);
					funName = funName.trim();
					for(FunctionCreator fun: builder.getFunctionList()){
						if(fun.getFunctionName().equals(funName)){
							funName = "fun." + funName;
							//funName = builder.getPackage().getName() + "." + funName;
							break;
						}
					}

					line = funName + line.substring(pos);
				}

				line = line + ";\n";
			}

			javaBuffer.append(line);
		}

		//javaBuffer.append(command);

		javaBuffer.append("}\n");

		javaBuffer.append("public void SetObject(Object obj){\n");

		for (Condition cond : conditionList){
			if(cond.result != null && !StringUtils.isEmpty(cond.result.getName()) ){

				javaBuffer.append("if(obj instanceof ");
				javaBuffer.append(cond.result.getReference());
				javaBuffer.append(" ){ \n");
				javaBuffer.append(cond.result.getName());
				javaBuffer.append(" = (");
				javaBuffer.append(cond.result.getReference());
				javaBuffer.append(") obj ");
				javaBuffer.append(";\n");
				javaBuffer.append("}\n");
			}
		}

		javaBuffer.append("}\n");
		javaBuffer.append("\n");

		javaBuffer.append("}");
		return javaBuffer.toString();

	}


    /**
     * small function.
     * @param rule
     * @param start
     * @return
     */
    private int getNextSection(String rule, int start){

    	for(int iLoop = start; iLoop < rule.length(); iLoop++){

			char  alphabet = rule.charAt(iLoop);

			if(alphabet == ' ' || alphabet == '\t' || alphabet == '\r' || alphabet == '\n'){

				return iLoop;
			}else{

			}
		}

    	return rule.length();

    }

    private void parse(String content){

		StringBuffer  element = new StringBuffer();

		content = content.trim();
		content = content + " ";

		int nextPos = 0;
		for(int iLoop = 0; iLoop < content.length(); iLoop++){

			char  alphabet = content.charAt(iLoop);
			//分割字符串，去除空格，制表符
			if(alphabet != ' ' && alphabet != '\t' && alphabet != '\r' && alphabet != '\n'){
				element.append(alphabet);
				continue;
			}

			String word = element.toString();
			if(StringUtils.isEmpty(word)){
				continue;
			}
			element = new StringBuffer();

			switch(word){
			case "salience" :
				nextPos = this.getNextSection(content, iLoop+1);
				String priority = content.substring(iLoop, nextPos);
				priority = priority.trim();
				item.setPriority(priority);
				iLoop = nextPos;
				break;
			case "rule" :
				nextPos = this.getNextSection(content, iLoop+1);
				String itemNo = content.substring(iLoop, nextPos);
				itemNo = itemNo.trim();
				if(itemNo.startsWith("\"") && itemNo.endsWith("\"")){
					itemNo = itemNo.substring(1, itemNo.length()-1);
				}
				item.setItemNo(itemNo);

				iLoop = nextPos;
				break;

			case "when" :
				for(int jLoop = iLoop +1; jLoop < content.length(); jLoop++){

					nextPos = this.getNextSection(content, jLoop);
					String next  = content.substring(jLoop, nextPos );
					next = next.trim();
					if(!StringUtils.isEmpty(next)){
						jLoop = nextPos +1;
						if(next.equals("then")){
							String exeClass = content.substring(iLoop+1, nextPos - "then".length() );
							exeClass  = exeClass.trim();
							item.setExeClass(exeClass);
							iLoop = nextPos - "then".length() -1;
							break;
						}
					}
				}

				break;
			case "then" :
				for(int jLoop = iLoop +1; jLoop < content.length(); jLoop++){

					nextPos = this.getNextSection(content, jLoop);
					String next  = content.substring(jLoop, nextPos);
					next = next.trim();
					if(!StringUtils.isEmpty(next)){
						jLoop = nextPos +1;
						if(next.equals("end")){
							command = content.substring(iLoop+1, nextPos - "end".length() );
							command  = command.trim();
							iLoop = nextPos - "end".length() -1;
							break;
						}
					}
				}
				break;
			case "no-loop":

				nextPos = this.getNextSection(content, iLoop+1);
				String next  = content.substring(iLoop, nextPos);
				next = next.trim();
				if(next.equals("true")){
					item.setContinueFlag(String.valueOf(ItemExecutedResult.CONTINUE));
				}else{
					item.setContinueFlag(String.valueOf(ItemExecutedResult.LOOP));
				}
				iLoop = nextPos;
				break;
			case "activation-group":
				nextPos = this.getNextSection(content, iLoop+1);
				next  = content.substring(iLoop, nextPos);
				item.setParentItemNo(next);
				iLoop = nextPos;
				break;
			case "enable":
				nextPos = this.getNextSection(content, iLoop+1);
				next  = content.substring(iLoop, nextPos);
				next = next.trim();
				if(next.equals("false")){
					item.setEnableFlag(false);
				}else{
					item.setEnableFlag(true);
				}
				iLoop = nextPos;
				break;
			case "dialect":
				nextPos = this.getNextSection(content, iLoop+1);
				next  = content.substring(iLoop, nextPos);
				if(!next.equals("java")){
					// TODO unsupported.
					log.debug("unsupported language:" + word );
				}
				iLoop = nextPos;
				break;
			case "auto-focus":
			case "date-effective":
			case "date-expires":
			case "ruleflow-group":
			case "lock-on-active":
			case "duration":
			case "agenda-group":
				// TODO unsupported.
				log.debug("unsupported key word:" + word );
				break;
			case "end":
			case "":
			case " ":
			case "\r":
			case "\t":
			case "\n":
				break;
			default:
				break;
			}

		}
    }

    private RuleCreator(){

    }

    private void parseCondition(){

    	String content = item.getExeClass().trim();

    	String[] lines = content.split("\\n|\\r");
    	for(int iLoop= 0; iLoop < lines.length; iLoop++){

    		if(!StringUtils.isEmpty(lines[iLoop])){

	    		String line = lines[iLoop].trim();

	    		int pos = line.indexOf(':');
	    		//String[] sections = line.split(":");

	    		String other = null;
	    		LocalCreator local  = new LocalCreator();
	    		if(pos > 0){
	    			String first = line.substring(0, pos);

	        		local.setName(first.trim());
	        		if(local.getName().startsWith("\"") && local.getName().endsWith("\"")){
	        			local.setName(local.getName().substring(1, local.getName().length()-1));
	        		}
	        		other = line.substring(pos+1);
	        	}else{
	        		other = line;
	        	}

        		pos = other.indexOf('(');
        		local.setReference(other.substring(0, pos));

        		String constraint = other.substring(pos);
        		Condition cond = new Condition();
        		cond.condition = ConstraintCreator.create(constraint,this);
        		cond.result = local;

	    		conditionList.add(cond);
    		}
    	}

    	item.setExeClass("tech.kiwa.engine.component.impl.DroolsRuleExecutor");

    }

    private void parseCommand(){

    	String className = builder.getPackage().getName()  +".";
    	className +=  item.getItemNo().substring(0, 1).toUpperCase();
    	className += item.getItemNo().substring(1);
    	className +="Command";

    	item.setExecutor(className);
    	item.setResult(String.valueOf(RESULT.PASSED.getValue()));
    	item.setContinueFlag(String.valueOf(ItemExecutedResult.CONTINUE));
    }


    public boolean runCondition(Object object) throws RuleEngineException{

    	String typeClass ;
    	boolean bRet = false;

    	//this.object = object;
    	for(Condition cond : conditionList){

    		typeClass = cond.result.getReference();
    		if("eval".equals(typeClass)){
    			bRet = cond.condition.executeExpress(object);
				if(bRet){
					cond.result.setValue(object);
				}
    			continue;
    		}

    		if(!typeClass.contains(".")){
    			boolean bFound = false;
    			for (ImportCreator impt :builder.getImportList()){
    				if(typeClass.equals(impt.getSimpleName())){
    					typeClass = impt.getFullName();
    					bFound =true;
    					break;
    				}
    			}
    			if(!bFound){
    				typeClass = builder.getPackage().getName() + "." + typeClass;
    			}
    		}
    		if(object.getClass().getName().equals(typeClass)){

				bRet = cond.condition.executeExpress(object);
				if(bRet){
					cond.result.setValue(object);
				}
    		}else{
    			log.debug("condition type is not matched.");
    		}

    	}

    	return bRet;
    }

    public List<LocalCreator> getResultList(){

    	List<LocalCreator> retList = new ArrayList<LocalCreator>();

		for (Condition cond : conditionList){
			if(cond.result != null && !StringUtils.isEmpty(cond.result.getName()) ){
				retList.add(cond.result);
			}
		}

		return retList;
    }

    public boolean operateCallback(String leftValue , String comparisonCode ,String baseValue, Object object) throws RuleEngineException{

    	boolean bRet = false;

		try {

			Class<?> retClass = object.getClass();
			Field  field = null;
			try{
				 field = retClass.getField(leftValue);
			}catch(NoSuchFieldException e1){

			}
			Object value  = null;
			if(field!=null ){
				field.setAccessible(true);
				value = field.get(object);
			}else {

				String methodName = "get" ;
				if( leftValue.length() >= 1){
					methodName = methodName + leftValue.substring(0,1).toUpperCase() + leftValue.substring(1);
				}else{
					methodName = methodName + leftValue.toUpperCase();
				}
				Method method = retClass.getMethod(methodName,(Class<?>[])null);
				if(method != null ){
					value = method.invoke(object, (Object[]) null);
				}
			}

			if(value != null){
				bRet = AbstractRuleItem.comparisonOperate(value.toString(), comparisonCode, baseValue);
			}
		} catch (IllegalAccessException |  SecurityException | NoSuchMethodException | IllegalArgumentException | InvocationTargetException e  ) {

			throw new RuleEngineException(e.getCause());
		}

		return bRet;
    }

    private DroolsBuilder builder = null;

    public DroolsBuilder getBuilder(){
    	return builder;
    }

	public static RuleCreator create(String content, DroolsBuilder builder){


		if(StringUtils.isEmpty(content)){
			return null;
		}

		if(!content.startsWith("rule")){
			return null;
		}

		RuleCreator creator = new RuleCreator();
		creator.builder = builder;
		creator.parse(content);
		creator.parseCondition();
		creator.parseCommand();
		creator.item.setAttach(creator);

		return creator;
	}

}
