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
public class EngineRunResult {

	private RESULT result  = RESULT.EMPTY;

	private String result_desc;

	private String sequence;

	public RESULT getResult() {
		return result;
	}

	public void setResult(RESULT result) {
		this.result = result;
	}

    public void setResult(String result) {

    	boolean bRet = this.result.parse(result);
    	if(!bRet){
    		try {
				this.result.setValue(Integer.parseInt(result));
			} catch (NumberFormatException e) {

			}
    	}
    }

    public void setResult(int result) {

    	this.result.setValue(result);
    }

	public String getResult_desc() {
		return result_desc;
	}

	public void setResult_desc(String result_desc) {

		this.result_desc = result_desc;
		this.result.parse(result_desc);

	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}


}
