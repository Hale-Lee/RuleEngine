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
public class PackageCreator implements DroolsPartsCreator {

	/* (non-Javadoc)
	 * @see tech.kiwa.engine.component.drools.DroolsPartsCreator#toJavaString()
	 */

	private String packageName = null;

	@Override
	public String toJavaString() {

		StringBuffer javaBuffer = new StringBuffer();
		javaBuffer.append("package ");
		javaBuffer.append(packageName);
		javaBuffer.append(";\n");

		return javaBuffer.toString();
	}

	public String getName(){
		return  packageName;
	}

	private PackageCreator(){

	}

	@SuppressWarnings("unused")
	private DroolsBuilder builder = null;

	public static PackageCreator create(String content, DroolsBuilder builder){

		if(StringUtils.isEmpty(content)){
			return null;
		}

		if(!content.startsWith("package")){
			return null;
		}

		content = content.trim();

		PackageCreator creator = new PackageCreator();
		creator.builder = builder;

    	String[] sections = content.split("\\s+|\\t|\\n|\\r");
    	if(sections.length >=2){

    		if(sections[1].endsWith(";")){
    			sections[1]  = sections[1].substring(0, sections[1].length()-1);
    		}
    		creator.packageName =  sections[1].trim();
    	}else{

    		creator.packageName = null;
    	}

    	return creator;
    }

}
