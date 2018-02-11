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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.druid.util.StringUtils;

import tech.kiwa.engine.exception.RuleEngineException;
import tech.kiwa.engine.framework.OperatorFactory;
/**
 * @author Hale.Li
 * @since  2018-01-28
 * @version 0.1
 */
public class ConstraintCreator implements DroolsPartsCreator {


	private String content ;
	private List<Word> express = null;

	private Logger log = LoggerFactory.getLogger(ConstraintCreator.class);

	// 1 = 运算变量; 2 = 运算符;  3 = 双目运算符号;   4 = 单目运算符号 ; 5 = 左括号 ; 6 = 右括号
	private static enum TYPE { VARIABLE, OPERATOR,  BINOCULAR, MONOCULAR, LEFT_BRACKET, RIGHT_BRACKET };

	private static final String[] KEYWORDS = new String[] {"<=",">=", "==","!=","contains", "not contains",
							"memberof","not memberof", "matches","not matches","<",">" };

	private DroolsPartsCreator container = null;

    class Word {

    	public TYPE type ;
    	public String element;
    	public int level = 0;

    	public String toString(){
    		return "[type=" + String.valueOf(type) + " element = '" + element + "' level = " + String.valueOf(level) + "]";
    	}

    	public List<Word> subUnit;
    }


    class ExpressionUnit{

    	private ExpressionUnit left = null;
    	private ExpressionUnit right = null ;

    	private String operator = null;

    	private Word left_value = null;

    	private Word right_value = null;

    	private Word middle_value = null;

    	private boolean result_value = false;

    	private String name = null;

    	public boolean calculate(){

    		if("!".equals(operator)){
    			result_value =  !right.calculate();
    		}else if("&&".equals(operator)){
    			result_value =  left.calculate() && right.calculate();
    		}else if ("||".equals(operator)){
    			result_value =  left.calculate() || right.calculate();
    		}

    		return result_value;
    	}

    	public String toString(){

    		StringBuffer ret = new StringBuffer();

    		if(leftSubList != null){
    			ret.append("[");
    			ret.append(leftSubList.toString());
    			ret.append("] ");
    		}
    		ret.append(name);
    		ret.append(":");
    		ret.append(operator);
    		if(rightSubList != null){
    			ret.append(" [");
    			ret.append(rightSubList.toString());
    			ret.append("]");
    		}

    		return ret.toString();
    	}

    	public List<Word> leftSubList = null;
    	public List<Word> rightSubList = null;

    }

	private ConstraintCreator(){

	}

	public String toString(){
		return content;
	}



	public static ConstraintCreator create(String content, DroolsPartsCreator parent){

		if(StringUtils.isEmpty(content)){
			return null;
		}

		ConstraintCreator creator = new ConstraintCreator();

		creator.content = content;
		if(content.indexOf(':')> 0){
			creator.log.debug("unsupported gramma ':' in LHS.");
			return null;
		}
		creator.express = creator.parse(content);

		creator.express = creator.ChangeComma(creator.express);
		creator.express = creator.trimExpress(creator.express, -1);
		creator.container = parent;

		return creator;
	}



    /**
     * 解析出各个独立的单元。
     * @param contents
     * @return
     */
	private List<Word> parse(String content){

		List<Word> theStack  = new LinkedList<Word>();

		StringBuffer  element = new StringBuffer();
		int  level  = 0 ;
		Word unit = null;

		for(int iLoop = 0; iLoop < content.length(); iLoop++){

			char  alphabet = content.charAt(iLoop);
			int[]  pos = new int[]{iLoop,-1};

			if(isKeyword(content, pos)){
				String keyword = content.substring(iLoop, pos[1]);
				keyword = keyword.toLowerCase().trim();

				if(element.length() > 0 ){
					theStack.add(createVariable(element,level));
					element = new StringBuffer();
				}

				unit = new Word();
				unit.element = OperatorFactory.OPR_CODE.getCode(keyword);
				unit.level = level;
				unit.type = TYPE.OPERATOR;
				theStack.add(unit);

				//重新计算alphabet
				iLoop = pos[1];
				alphabet = content.charAt(iLoop);
			}

			switch (alphabet){
			case '(':			//单字节运算符
				if(element.length() > 0 ){
					theStack.add(createVariable(element,level));
				}
				unit = new Word();
				unit.element = String.valueOf(alphabet);
				unit.type  = TYPE.LEFT_BRACKET;
				level ++ ;			// ( 本身也属于下个level.

				unit.level = level;
				theStack.add(unit);

				element = new StringBuffer();
				break;
			case ')':
				if(element.length() > 0 ){
					theStack.add(createVariable(element,level));
				}
				unit = new Word();
				unit.element = String.valueOf(alphabet);
				unit.type  = TYPE.RIGHT_BRACKET;
				unit.level = level;
				theStack.add(unit);
				level -- ;					//// ) 本身也属于上一个level.
				element = new StringBuffer();
				break;
			case ',':
				if(element.length() > 0 ){
					theStack.add(createVariable(element,level));
				}
				unit = new Word();
				unit.element = String.valueOf(alphabet);
				unit.type  = TYPE.BINOCULAR;
				unit.level = level;
				theStack.add(unit);

				element = new StringBuffer();
				break;
			case '!':
				if(element.length() > 0 ){
					theStack.add(createVariable(element,level));
				}
				unit = new Word();
				unit.element = String.valueOf(alphabet);
				unit.type  = TYPE.MONOCULAR;
				unit.level = level;
				theStack.add(unit);
				element = new StringBuffer();
				break;
			case '&':
			case '|':
				//如果是2个||或者是2个&&，那么就是逻辑运算符
				if( content.length() >= iLoop+1 && content.charAt(iLoop+1) == alphabet ){
					if(element.length() > 0 ){
						theStack.add(createVariable(element,level));
					}
					unit = new Word();
					unit.element = String.valueOf(alphabet) + String.valueOf(alphabet);
					unit.type  = TYPE.BINOCULAR;
					unit.level = level;
					theStack.add(unit);
					iLoop++;
					element = new StringBuffer();
				}else{
					element.append(alphabet);
				}
				break;
			case ' ' :		//去除空格。
				if(element.length() > 0 ){
					Word word = createVariable(element,level);
					if(null != word){
						theStack.add(word);
						element = new StringBuffer();
					}
				}
				break;
			case '\n':		//去除换行。

				//扫尾的字符串也要添加进去。
				if(element.length() > 0){

					String end = element.toString();
					if(")".equals(end)){
						unit = new Word();
						unit.element = end;
						unit.type  = TYPE.RIGHT_BRACKET;
						unit.level = 1;				//必须是1
						theStack.add(unit);

					}else if("!".equals(end) || "(".equals(end) || "|".equals(end) || "&".equals(end)) {
						log.error("结尾的字符不能是关键字。");

					} else {
						theStack.add(createVariable(element,0));
						element = new StringBuffer();
					}
				}

				break;
			case '\r':		//去除回车。
				break;
			default:
				element.append(alphabet);
				break;
			}

		}

		//扫尾的字符串也要添加进去。
		if(element.length() > 0){

			String end = element.toString();
			if(")".equals(end)){
				unit = new Word();
				unit.element = end;
				unit.type  = TYPE.RIGHT_BRACKET;
				unit.level = 1;				//必须是1
				theStack.add(unit);

			}else if("!".equals(end) || "(".equals(end) || "|".equals(end) || "&".equals(end)) {
				log.error("结尾的字符不能是关键字。");

			} else {
				theStack.add(createVariable(element,0));
			}
		}

		return theStack;
	}

	private List<Word> ChangeComma(List<Word> wordList){

		LinkedList<Word> newList = new LinkedList<Word>();

		for(int iLoop =0; iLoop < wordList.size(); iLoop++){
			Word word = wordList.get(iLoop);

			if(",".equals(word.element)){

				int level = word.level;
				boolean bFound = false;
				for(int jLoop=iLoop; jLoop > 0; jLoop--){
					if(wordList.get(jLoop).level == level && "(".equals(wordList.get(jLoop).element)){

						Word element = new Word();
						element.element = "(";
						element.level = level;
						element.type  = TYPE.LEFT_BRACKET;
						newList.add(jLoop, element);
						bFound  = true;
						break;
					}
				}
				if(!bFound){

					Word element = new Word();
					element.element = "(";
					element.level = level;
					element.type  = TYPE.LEFT_BRACKET;
					newList.add(0, element);
				}

				Word element = new Word();
				element.element = ")";
				element.level = level;
				element.type  = TYPE.RIGHT_BRACKET;
				newList.add(element);

				word.element = "&&";
				newList.add(word);

				element = new Word();
				element.element = "(";
				element.level = level;
				element.type  = TYPE.LEFT_BRACKET;
				newList.add(element);

				bFound = false;
				for(int jLoop=iLoop; jLoop < wordList.size(); jLoop++){
					if(wordList.get(jLoop).level == level && ")".equals(wordList.get(jLoop).element)){

						element = new Word();
						element.element = ")";
						element.level = level;
						element.type  = TYPE.RIGHT_BRACKET;
						wordList.add(jLoop, element);
						bFound = true;
						break;
					}
				}
				if(!bFound){
					element = new Word();
					element.element = ")";
					element.level = level;
					element.type  = TYPE.RIGHT_BRACKET;
					wordList.add(element);
				}

			}else{
				newList.add(word);
			}
		}

		return newList;
	}


	/**
	 * 去除前后端无用的括号。
	 * @param list
	 * @param minLevel		-- 如果是-1，那么需要重新累计level值。
	 * @return
	 */
	private List<Word> trimExpress(List<Word> list, int minLevel){

		//无括号表达式。
		if(list.size() <=1 ){
			return list;
		}

		//开头不是左括号
		if(list.get(0).type != TYPE.LEFT_BRACKET){
			return list;
		}

		//开头是左括号，但是结尾不是右括号。
		if(list.get(0).type == TYPE.LEFT_BRACKET && list.get(list.size()-1).type != TYPE.RIGHT_BRACKET){
			return list;
		}

		//最少括号数目是0，就是中间有非括号的情况 () + ()。
		if(minLevel == 0){
			return list;
		}

		//未知的最小括号数目，重新取得。
		if(minLevel < 0 ){
			int tempLevel = Integer.MAX_VALUE;
			for(Word unit: list){
				if(tempLevel > unit.level){
					tempLevel = unit.level;
				}
			}

			minLevel = tempLevel;
		}

		//最少括号数目是0，就是中间有非括号的情况 () + ()。
		if(minLevel <=0){
			return list;
		}

		//依次拷贝到去除多余括号的数组中去。
		List<Word> newList = new ArrayList<Word>();
		for(int iLoop = 0; iLoop < list.size() ; iLoop++){
			Word unit = list.get(iLoop);
			if(iLoop < minLevel ){
				//非标准括号，可能是表达式不合格。
				if(unit.type != TYPE.LEFT_BRACKET){
					System.err.println("括号个数不匹配。");
					//throw new Exception("括号个数不匹配。");
				};
			}else if( iLoop >= list.size() - minLevel){
				if(unit.type != TYPE.RIGHT_BRACKET){
					//throw new Exception("括号个数不匹配。");
					System.err.println("括号个数不匹配。");
				}
			}else{
				unit.level = unit.level - minLevel;
				newList.add(unit);
			}
		}

		//list的内容也刷新一遍。
		//list.clear();
		//list.addAll(newList);

		return newList;
	}

	private boolean isKeyword(String content, int[] pos){

		boolean bRet = false;
		int start = pos[0];

		//if(start >= 1){
		//	char separator = content.charAt(start - 1);
		//	if(separator != ' ' && separator != '\t'){
			//	return bRet;
		//	}
		//}

		for(int jLoop =0; jLoop < KEYWORDS.length; jLoop++){
			String keyword = KEYWORDS[jLoop];

			if(content.length() < start+keyword.length() ){
				continue;
			}

			String sub = content.substring(start, start+keyword.length());
			sub = sub.trim();
			//char separator = content.charAt(start+ keyword.length() + 1);
			if(keyword.equalsIgnoreCase(sub) ){
				pos[1] = start + keyword.length();
				bRet =true;
				break;
			}
		}

		return bRet;
	}

	private Word createVariable(StringBuffer element, int level){

		Word unit = new Word();
		unit.element = element.toString();
		unit.type  = TYPE.VARIABLE;
		unit.level = level;

		return unit;
	}

	@Override
	public String toJavaString() {

		StringBuffer java = new StringBuffer();
		java.append("boolean bResult = false;\n");
		java.append("bResult = (");

		for(Word word : express){

			if(word.type == TYPE.VARIABLE){
				java.append("?.");
				java.append(word.element);
			}else{
				java.append(word.element);
			}
		}

		java.append(")");

		return java.toString();
	}

	public boolean executeExpress(Object object) throws RuleEngineException{

		ExpressionUnit unit = breakExpress(express, null, object);
		return unit.result_value;

	}


	private ExpressionUnit breakExpress(List<Word> list, ExpressionUnit current,Object object) throws RuleEngineException{

		list = trimExpress(list, -1);

		ExpressionUnit root = current;
		if(root == null){
			root = new ExpressionUnit();
		}

		int firstOr = -1, firstAnd = -1, firstNot = -1 ;
		for(int iLoop =0; iLoop < list.size(); iLoop++){
			Word unit = list.get(iLoop);
			//取得括号外的内容。括号中的内容不作为划分的信息。
			if(unit.level == 0){
				//双目运算符
				if(unit.type == TYPE.BINOCULAR){
					if(firstOr == -1 && "||".equals(unit.element)){
						firstOr = iLoop;
						break;
					}
					if(firstAnd == -1 && "&&".equals(unit.element)){
						firstAnd = iLoop;
					}
				}else if(unit.type == TYPE.MONOCULAR){
					if(firstNot == -1 && "!".equals(unit.element)){
						firstNot = iLoop;
					}
				}
			}
		}

		if(firstOr > 0){
			root.operator = list.get(firstOr).element;
			root.name = "OR";
			root.leftSubList = this.trimExpress(list.subList(0, firstOr), -1);
			root.left =  breakExpress(root.leftSubList, root.left,object);

			root.rightSubList = this.trimExpress(list.subList(firstOr + 1, list.size()), -1);
			root.right = breakExpress(root.rightSubList, root.right,object);

			root.calculate();
		}else if(firstAnd > 0){
			root.operator = list.get(firstAnd).element;
			root.name = "AND";
			root.leftSubList = this.trimExpress(list.subList(0, firstAnd), -1);
			root.left =  breakExpress(root.leftSubList, root.left,object);

			root.rightSubList = this.trimExpress(list.subList(firstAnd + 1, list.size()), -1);
			root.right = breakExpress(root.rightSubList, root.right,object);

			root.calculate();
		}else if( firstNot >=0){
			root.operator = list.get(firstNot).element;
			root.name = "NOT";
			root.leftSubList = null;
			root.rightSubList = this.trimExpress(list.subList(firstNot + 1, list.size()), -1);
			root.right = breakExpress(root.rightSubList, root.right,object);

			root.calculate();
		//不带运算符的情况。
		}else if(current == null && list.size() > 0){

			/*if(list.get(0).type == TYPE.OPERATOR){
				root.operator = list.get(0).element;
				root.name = "OPERATOR";

				if(container instanceof RuleCreator){
					RuleCreator rule = (RuleCreator) container;
					root.result_value = rule.operateCallback(root.left.operator, root.operator, root.right.operator);
				}else if(container instanceof QueryCreator){
					QueryCreator query = (QueryCreator)container;
					root.result_value = query.operateCallback(root.left.operator, root.operator, root.right.operator);
				}

			}else */if(list.size() == 1 && list.get(0).type == TYPE.VARIABLE){
				root.name = "VARIABLE";
				root.operator = list.get(0).element;
				if(list.get(0).element.equalsIgnoreCase("true")){
					root.result_value = true;
				}else{
					root.result_value = false;
				}

				root.left_value = list.get(0);
				root.right_value = list.get(0);

			} else if(list.size() == 3 &&  list.get(0).type == TYPE.VARIABLE &&
					list.get(1).type == TYPE.OPERATOR && list.get(2).type == TYPE.VARIABLE){

				root.left_value = list.get(0);
				root.middle_value = list.get(1);
				root.right_value = list.get(2);

				root.operator = list.get(1).element;
				root.name = "EXPRESS";

				if(container instanceof RuleCreator){
					RuleCreator rule = (RuleCreator) container;
					root.result_value = rule.operateCallback(root.left_value.element, root.middle_value.element, root.right_value.element,object);
				}else if(container instanceof QueryCreator){
					QueryCreator query = (QueryCreator)container;
					root.result_value = query.operateCallback(root.left_value.element,  root.middle_value.element, root.right_value.element,object);
				}
			}

		}

		return root;
	}

}
