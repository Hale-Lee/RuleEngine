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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tech.kiwa.engine.component.AbstractRuleReader;
import tech.kiwa.engine.component.drools.DroolsBuilder;
import tech.kiwa.engine.entity.RuleItem;
import tech.kiwa.engine.utility.PropertyUtil;


/**
 * @author Hale.Li
 * @since  2018-01-28
 * @version 0.1
 */
public class DroolsRuleReader extends AbstractRuleReader {


/*
	private static String[] RULE_KEYWORDS = new String[] {"salience",
			"when","then","end","agenda-group","auto-focus", "no-loop",
			"activation-group","dialect","date-effective","date-exptires","lock-on-active","duration"};
*/
	private Logger log = LoggerFactory.getLogger(DefaultRuleExecutor.class);

	private DroolsBuilder builder = null;

    private String removeComments(String contents){


    	StringBuffer  element = new StringBuffer();

		for(int iLoop = 0; iLoop < contents.length(); iLoop++){

			char  alphabet = contents.charAt(iLoop);
			String keywords = "";
			if(iLoop> 0){
				char preAlpha = contents.charAt(iLoop-1);
				keywords = ""+ preAlpha + alphabet;
			}else{
				keywords = ""+ alphabet;
			}

			int jLoop = iLoop;
			switch(keywords){

				case "//":
					element.deleteCharAt(element.length()-1);
				case "#":

					//读到行末
					for(jLoop = iLoop +1; jLoop < contents.length(); jLoop++){
						alphabet = contents.charAt(jLoop);

						if(alphabet == '\n'){
							element.append(alphabet);
							iLoop = jLoop;
							break;
						}
					}


					break;

				case "/*":
					element.deleteCharAt(element.length()-1);
					for(jLoop = iLoop +1; jLoop < contents.length(); jLoop++){
						alphabet = contents.charAt(jLoop);
						//element.append(alphabet);
						String compare = "" + contents.charAt(jLoop-1) + alphabet;
						if(compare.equals("*/")){
							iLoop = jLoop;
							break;
						}
					}
					break;

				default:
					element.append(alphabet);
					break;
			} // end case

		} // end first for.

		return element.toString();
    }

    private List<String> analyzeTopPhase(String contents){

    	StringBuffer  element = new StringBuffer();

    	List<String> thePhases  = new ArrayList<String>();
		for(int iLoop = 0; iLoop < contents.length(); iLoop++){

			char  alphabet = contents.charAt(iLoop);

			//去除空行。
			if(element.length() == 0 && (alphabet == '\r' || alphabet =='\n')){
				continue;
			}else{
				element.append(alphabet);
			}

			int jLoop = iLoop;
			switch(element.toString()){
				case "package ":
				case "package\t":
				case "import ":
				case "import\t":
				case "globals ":
				case "globals\t":
				case "global ":
				case "global\t":
					if(iLoop >= element.length()){

						char preAlpha = contents.charAt(iLoop-element.length());
						if(preAlpha != ' ' && preAlpha != '\t' && preAlpha != '\r' && preAlpha != '\n'){
							break;
						}
					}
					//这里没有break.
					//读到行末
					for(jLoop = iLoop +1; jLoop < contents.length(); jLoop++){
						alphabet = contents.charAt(jLoop);
						element.append(alphabet);
						if(alphabet == '\n'){
							thePhases.add(element.toString().trim());
							iLoop = jLoop;
							element = new StringBuffer();
							break;
						}
					}

					break;
				case "function ":
				case "function\t":
					//防止前黏连。
					if(iLoop >= element.length()){
						char preAlpha = contents.charAt(iLoop-element.length());
						if(preAlpha != ' ' && preAlpha != '\t' && preAlpha != '\r' && preAlpha != '\n'){
							break;
						}
					}
					int level = 1 , count = 0;
					for(jLoop = iLoop +1; jLoop < contents.length(); jLoop++){
						alphabet = contents.charAt(jLoop);
						element.append(alphabet);
						if(alphabet == '{' ){
							count ++;
							level = count;
						}
						if(alphabet == '}'){
							level --;
						}
						if(level == 0){
							thePhases.add(element.toString().trim());
							iLoop = jLoop;
							element = new StringBuffer();
							break;
						}
					}

					break;
				case "query ":
				case "query\t":
				case "declare ":
				case "declare\t":
				case "rule ":
				case "rule\t":
				case "rule\r":
				case "rule\n":
					//防止前黏连。
					if(iLoop >= element.length()){
						char preAlpha = contents.charAt(iLoop-element.length());
						if(preAlpha != ' ' && preAlpha != '\t' && preAlpha != '\r' && preAlpha != '\n'){
							break;
						}
					}

					//查找对应的end关键字
					for(jLoop = iLoop +1; jLoop < contents.length(); jLoop++){
						alphabet = contents.charAt(jLoop);
						element.append(alphabet);

						//防止前黏连
						if(contents.charAt(jLoop-3) == ' ' || contents.charAt(jLoop-3) == '\n'
						   ||contents.charAt(jLoop-3) == '\r'||contents.charAt(jLoop-3) == '\t'){
							//防止后黏连
							if(jLoop == contents.length() -1 || contents.charAt(jLoop+1) == '\r'
									|| contents.charAt(jLoop+1) == ' '|| contents.charAt(jLoop+1) == '\t'
									|| contents.charAt(jLoop+1) == '\n'){

								String compare = "" +  contents.charAt(jLoop-2) + contents.charAt(jLoop-1) + alphabet;
								if(compare.equals("end")){
									thePhases.add(element.toString().trim());
									iLoop = jLoop;
									element = new StringBuffer();
									break;
								}
							}
						}
					}
					break;
				default:
					if(alphabet == '\r' || alphabet =='\n'){
						log.debug("unknown keyword:" + element.toString());
						element = new StringBuffer();
					}
					break;
			} // end case

		} // end first for.

		return thePhases;
    }





/*
    private List<Condition> parseCondition(RuleItem rule){

    	String content = rule.getExeClass();
    	if(StringUtils.isEmpty(content)){
    		return null;
    	}
    	content = content.trim();

    	String[] lines = content.split("\\n|\\r");

    	List<Query> queryList = new ArrayList<Query>();

    	rule.setRemark(queryList);

    	for(int iLoop =0 ; iLoop < lines.length; iLoop ++){
	    	String line = lines[iLoop].trim();


	    	Query query = new Query();
	    	String[] sections = line.split(":");
	    	Local local = new Local();

	    	if(sections.length >= 2){
	    		local.name = sections[0];

				int pos = sections[1].indexOf("(");
				//sections = sections[1].split("\\(");
				local.reference = sections[1].substring(0, pos);

				if( sections[1].endsWith(")")){
					 sections[1] =  sections[1].substring(pos+1, sections[1].length()-1 );
					 sections[1] = sections[1].trim();
				}
				query.content = sections[1];

	    	}else{

				int pos = line.indexOf("(");
				//sections = sections[1].split("\\(");
				local.reference = line.substring(0, pos);

				if( line.endsWith(")")){
					line =  line.substring(pos+1, line.length()-1 );
					line = line.trim();
				}
				query.content = line;
				rule.setExeClass(line);
	    	}

	    	query.result = local;
	    	queryList.add(query);

    	}

    	return null;
    }

*/
    public static void main(String[] args) {

    	DroolsRuleReader reader = new DroolsRuleReader();
    	reader.readFile();
    }

    private void readFile(){


		String ruleEngineFile = PropertyUtil.getProperty("drools.rule.filename");
		if(!ruleEngineFile.startsWith(File.separator)){
			File dir = new File(PropertyUtil.class.getClassLoader().getResource("").getPath());
			ruleEngineFile = dir + File.separator + ruleEngineFile;
		}
		File file = new File(ruleEngineFile);

    	byte[] fileContent = new byte[(int) file.length()];

    	try {
    		FileInputStream fisRule = new FileInputStream(file);
    		fisRule.read(fileContent);
    		fisRule.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    	String noComments = removeComments(new String(fileContent));

    	//System.out.println("-------------------------------------------");
    	List<String> phases = analyzeTopPhase(noComments);
    	if(builder == null){
    		 builder = new DroolsBuilder();
    	}
    	builder.build(phases);
    }



	@Override
	public List<RuleItem> readRuleItemList() {
		if(builder == null){
			readFile();
		}
		return builder.getRuleItemList();
	}

	@Override
	public Long getRuleItemCount() {

		if(builder == null){
			readFile();
		}
		int count = builder.getRuleItemList().size();
		return new Long(count);

	}

	@Override
	public RuleItem getRuleItem(String ruleId) {

		if(builder == null){
			readFile();
		}

		List<RuleItem> list = builder.getRuleItemList();
		for(RuleItem item :list){
			if(item.getItemNo().equals(ruleId)){
				return item;
			}
		}

		return null;
	}

	public void convertToXml(String drlFile, String xmlFile){

	}

}
