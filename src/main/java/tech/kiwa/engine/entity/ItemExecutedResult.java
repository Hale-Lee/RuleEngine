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
public class ItemExecutedResult {

	public static final int CONTINUE = 1;
	public static final int LOOP = 2;
	public static final int BROKEN = 3;

    private RESULT result =RESULT.EMPTY;
    private String remark;

    private boolean returnValue ;

    public boolean getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(boolean returnValue) {
		this.returnValue = returnValue;
	}

	private int continueFlag = CONTINUE;			//默认可以继续

    public RESULT getResult() {
        return result;
    }

    public void setResult(RESULT result) {

    	this.result = result;
    	if(this.result == RESULT.WAIT){
    		continueFlag = BROKEN;
    	}
    	//continueFlag =  ( this.result != RESULT.WAIT);		//非中断状态
    }

    public void setResult(String result) {

    	int iResult = Integer.parseInt(result);
    	this.setResult(iResult);

    }

    public void setResult(int result) {

    	this.result.setValue(result);
    	if(this.result == RESULT.WAIT){
    		continueFlag = BROKEN;
    	}
    	//continueFlag =  ( this.result != RESULT.WAIT);		//非中断状态
    }


    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean canBeContinue(){

    	return continueFlag == CONTINUE;
    }

    public void setContinue(int contin){
    	this.continueFlag = contin;
    }

    public boolean shouldLoop(){

    	return continueFlag == LOOP;

    }
}

