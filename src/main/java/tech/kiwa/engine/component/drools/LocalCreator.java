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

/**
 * @author Hale.Li
 * @since  2018-01-28
 * @version 0.1
 */
public class LocalCreator implements DroolsPartsCreator {


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

	public void setName(String name) {
		if(null != name){
			this.name = name.trim();
		}
	}

	public void setReference(String reference) {
		if(null != reference){
			this.reference = reference.trim();
		}
	}


	@Override
	public String toJavaString() {
		StringBuffer sbf = new StringBuffer();
		sbf.append("private ");
		sbf.append(reference);
		sbf.append(" ");
		sbf.append(name);
		sbf.append(" ;\n");

		return sbf.toString();
	}

	public LocalCreator(String name, String reference){
		if(null != name){
			this.name = name.trim();
		}
		if(null != reference){
			this.reference = reference.trim();
		}
    }

	public LocalCreator(){
    }
}
