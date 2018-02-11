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
public class ImportCreator implements DroolsPartsCreator {

	String fullName;
	String simpleName;

	public String getFullName() {
		return fullName;
	}


	public String getSimpleName() {
		return simpleName;
	}


	public String toString() {
		return "import " + fullName + ";\n";
	}


	@Override
	public String toJavaString() {
		return "import " + fullName + ";\n";
	}

	private ImportCreator(){

	}

	@SuppressWarnings("unused")
	private DroolsBuilder builder = null;

	public static ImportCreator create(String content, DroolsBuilder builder){

		if(StringUtils.isEmpty(content)){
			return null;
		}

		if(!content.startsWith("import")){
			return null;
		}

		content = content.trim();
		ImportCreator creator = new ImportCreator();
		creator.builder = builder;

    	String[] sections = content.split("\\s+|\\t|\\n|\\r");
    	if(sections.length >=2){
    		creator.fullName = sections[1].trim();
    		if(creator.fullName.endsWith(";")){
    			creator.fullName = creator.fullName.substring(0,creator.fullName.length()-1);
    		}

    		int pos = creator.fullName.lastIndexOf('.');
    		if(pos > 0){
    			creator.simpleName = creator.fullName.substring(pos+1);
    		}
    	}

    	return creator;
    }
}
