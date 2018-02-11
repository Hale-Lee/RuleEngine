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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.alibaba.druid.util.StringUtils;

import tech.kiwa.engine.component.drools.ImportCreator;
/**
 * @author Hale.Li
 * @since  2018-01-28
 * @version 0.1
 */
public class DeclareCreator  implements DroolsPartsCreator {

	private String name;
	private Map<String, String> attributes;

	public String getName() {
		return name;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	private DroolsBuilder builder = null;

	private DeclareCreator(){

	}
	public static DeclareCreator create(String content, DroolsBuilder builder){



		if(StringUtils.isEmpty(content)){
			return null;
		}

		if(!content.startsWith("declare")){
			return null;
		}

		content = content.trim();

		DeclareCreator creator = new DeclareCreator();
		creator.builder = builder;

    	String[] lines = content.split("\\n|\\r");
    	String title = lines[0].trim();

    	String[] sections = title.split("\\s+|\\t|\\(|,|\\)");


    	if(sections.length >= 2){

    		creator.name = sections[1];

    		creator.attributes = new HashMap<String, String>();
    		for(int iLoop =1 ; iLoop < lines.length -1 ; iLoop++){

    			String line = lines[iLoop];
    			if(StringUtils.isEmpty(line)){
    				continue;
    			}
    			line = line.trim();
    			if(line.endsWith(";")){
    				line = line.substring(0, line.length()-1);
    			}
    			sections = line.split(":");
    			//ASSERT sections.length = 2;
    			if(creator.attributes == null){
    				creator.attributes = new HashMap<String, String>();
    			}
    			creator.attributes.put(sections[0].trim(), sections[1].trim());
    		}
    	}

    	return creator;

	}

	public String toJavaString(){

		StringBuffer javaBuffer = new StringBuffer();
		javaBuffer.append(builder.getPackage().toJavaString());
		for(ImportCreator impt : builder.getImportList()){
			javaBuffer.append(impt.toJavaString());
		}

		javaBuffer.append("import tech.kiwa.engine.component.drools.DeclareInterface;\n");

		javaBuffer.append("\n");
		javaBuffer.append("public class ").append(name);
		javaBuffer.append(" implements DeclareInterface");
		javaBuffer.append(" {").append("\n");
		javaBuffer.append("\n");

		for(GlobalCreator global : builder.getGlobalList()){
			javaBuffer.append(global.toJavaString());
		}
		javaBuffer.append("\n");

		Set<String> nameSet = attributes.keySet();
		for(String name : nameSet){
			String type = attributes.get(name);
			javaBuffer.append("public ");
			javaBuffer.append(type);
			javaBuffer.append(" ");
			javaBuffer.append(name);
			javaBuffer.append(";\n");
		}
		javaBuffer.append("\n");
		//Implement the getValue function.
		javaBuffer.append("  public Object getValue(String name){ \n");
		javaBuffer.append("\n");
		javaBuffer.append("  Object value = null;\n");
		for(String name : nameSet){
			javaBuffer.append("  if(name.equals(\""+name+"\")){\n");
			javaBuffer.append("     value = this."+ name +";\n");
			javaBuffer.append("  }\n");
		}

		javaBuffer.append("\n");
		javaBuffer.append("  return value;\n");

		javaBuffer.append("  }");
		javaBuffer.append("\n");

		//Implement the setValue function.
		javaBuffer.append("  public void setValue(String name, Object value){ \n");
		javaBuffer.append("\n");
		for(String name : nameSet){
			String type = attributes.get(name);
			javaBuffer.append("  if(name.equals(\""+name+"\")){\n");
			javaBuffer.append("     this."+ name +" = ("+ type +") value;\n");
			javaBuffer.append("  }\n");
		}

		javaBuffer.append("\n");
		javaBuffer.append("  return ;\n");

		javaBuffer.append("  }");
		javaBuffer.append("\n");

		javaBuffer.append("}");

		return javaBuffer.toString();
	}

	public DeclareInterface createObject(){

		DeclareInterface declare = (DeclareInterface) builder.createObject(this.name, this.toJavaString());

		return declare;
	}


}
