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

import com.alibaba.druid.util.StringUtils;
/**
 * @author Hale.Li
 * @since  2018-01-28
 * @version 0.1
 */
public class GlobalCreator  implements DroolsPartsCreator {

	private String name = null;
	private  String reference = null;

	private Object value = null;


	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getReference() {
		return reference;
	}

	@Override
	public String toJavaString() {
		StringBuffer sbf = new StringBuffer();
		sbf.append("public static ");
		sbf.append(reference);
		sbf.append(" ");
		sbf.append(name);
		sbf.append(" ;\n");

		return sbf.toString();
	}

	private GlobalCreator(){

	}

	@SuppressWarnings("unused")
	private DroolsBuilder builder = null;

	public static GlobalCreator create(String content, DroolsBuilder builder ){

		if(StringUtils.isEmpty(content)){
			return null;
		}

		if(!content.startsWith("global")){
			return null;
		}

		content = content.trim();
		GlobalCreator creator = new GlobalCreator();
		creator.builder = builder;

    	String[] sections = content.split("\\s+|\\t|\\n|\\r");
    	if(sections.length >=3){

    		creator.name = sections[2].trim();
    		creator.reference = sections[1].trim();
    	}

    	return creator;
    }
}
