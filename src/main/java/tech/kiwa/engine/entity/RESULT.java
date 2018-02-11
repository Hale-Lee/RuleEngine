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

package tech.kiwa.engine.entity;
/**
 * @author Hale.Li
 * @since  2018-01-28
 * @version 0.1
 */
public enum RESULT {

	EMPTY(0), PASSED(1), CONCERNED(2), REJECTED(3) , WAIT(4), SELFDEFINE(5);

	RESULT(){

	}
	private RESULT(int value){
		this.setValue(value);
	}

	private int value = 0;
	public void setValue(int value){
		switch (value){
		case 1:
			defaultDesc= "PASSED";
			break;
		case 2:
			defaultDesc= "CONCERNED";
			break;
		case 3:
			defaultDesc= "REJECTED";
			break;
		case 4:
			defaultDesc= "WAIT";
			break;
		case 5:
			defaultDesc= "SELFDEFINE";
			break;
		default:
			break;
		}

		this.value = value;
	}

	public int compare(RESULT target){
		return this.value - target.value;
	}

	public static RESULT valueOf(int value) {

		RESULT result = RESULT.EMPTY;
		result.setValue(value);
		return result;

	}

/*
	@Override
	public int compareTo(RESULT o){
		return 0;
	}
*/
	public int getValue(){
		return value;
	}

	private String defaultDesc = "";
	public String getName() {
		return defaultDesc;
	}

	public void setName(String defaultDesc) {
		this.defaultDesc = defaultDesc;
		this.parse(defaultDesc);
	}

	public boolean parse(String value ){

		boolean bRet = true;
		value = value.toUpperCase();
		switch (value){
		case "PASSED":
		case "RESULT.PASSED":
			this.value = 1;
			this.defaultDesc = "PASSED";
			break;
		case "CONCERNED":
		case "RESULT.CONCERNED":
			this.value = 2;
			this.defaultDesc= "CONCERNED";
			break;
		case "REJECTED":
		case "RESULT.REJECTED":
			this.value = 3;
			this.defaultDesc = "REJECTED";
			break;
		case "WAIT":
		case "RESULT.WAIT":
			this.value = 4;
			this.defaultDesc="WAIT";
			break;
		case "SELFDEFINE":
		case "RESULT.SELFDEFINE":
			this.value = 5;
			this.defaultDesc = "SELFDEFINE";
			break;
		default:
			bRet = false;
			break;
		}

		return bRet;
	}

}