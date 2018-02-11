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

import java.io.Serializable;
import java.util.Date;

import tech.kiwa.engine.framework.OperatorFactory;
/**
 * @author Hale.Li
 * @since  2018-01-28
 * @version 0.1
 */

public class RuleItem implements Serializable {

	private static final long serialVersionUID = -4129428406038157150L;

	private String itemNo;
	private String content;
	private String exeSql;
	private String exeClass;
	private String paramName;
	private String paramType;
	private String comparisonCode;
	private String comparisonValue;
	private String baseline;
	private String result;
	private String executor;
	private String priority;
	private String continueFlag;
	private String parentItemNo;
	private String groupExpress;
	private Object attach;
	private String comments;
	private String enableFlag;
	private Date createTime;
	private Date updateTime;


	public void setMappedValue(String name, String value){
		switch(name.toLowerCase()){
		case "itemno":
		case "ruleid":
				this.itemNo = value;
			break;
		case "auditdesc":
		case "content":
			this.content = value;
			break;
		case "exe_sql":
			this.exeSql = value;
			break;
		case "param_name":
			this.paramName = value;
			break;
		case "param_type":
			this.paramType = value;
			break;
		case "java_class":
		case "exe_class":
			this.exeClass = value;
			break;
		case "executor":
		case "command":
			this.executor = value;
			break;
		case "comments":
			this.comments = value;
			break;
		case "attach":
			this.attach = value;
			break;
		case "logic_key":
		case "comparison_code":
			this.comparisonCode = value;
			if(null != value && comparisonValue == null){
				this.comparisonValue  = OperatorFactory.OPR_CODE.getValue(value);
			}
			break;
		case "logic_value":
		case "comparison_value":
			this.comparisonValue = value;
			if(null != value && comparisonCode == null){
				this.comparisonCode  = OperatorFactory.OPR_CODE.getCode(value);
			}
			break;
		case "baseline":
			this.baseline = value;
			break;
		case "result_key":
		case "result":
			this.result = value;
			break;

		case "priority":
			this.priority = value;
			break;
		case "enable_flag":
			this.enableFlag = value;
			break;
		case "continue_flag":
			this.continueFlag = value;
			break;
		case "parent_item_no":
			this.parentItemNo = value;
			break;
		case "group_express":
		case "parent_express":
			this.groupExpress = value;
			break;
		default:
			break;
		}
	}

	public Object getValue(String name){

		Object value = null;
		if(name.equals("itemno")){
			value = this.itemNo;
		}

		return value;
	}

	public String getItemNo() {
		return itemNo;
	}
	public void setItemNo(String itemNo) {
		this.itemNo = (itemNo == null ? null : itemNo.trim());
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = (content == null ? null : content.trim());
	}
	public String getExeSql() {
		return exeSql;
	}
	public void setExeSql(String exeSql) {
		this.exeSql = (exeSql == null ? null : exeSql.trim());
	}
	public String getExeClass() {
		return exeClass;
	}
	public void setExeClass(String exeClass) {
		this.exeClass = (exeClass == null ? null : exeClass.trim());
	}
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = (paramName == null ? null : paramName.trim());
	}
	public String getParamType() {
		return paramType;
	}
	public void setParamType(String paramType) {
		this.paramType = (paramType == null ? null : paramType.trim());
	}
	public String getComparisonCode() {
		return comparisonCode;
	}
	public void setComparisonCode(String comparisonCode) {
		this.comparisonCode = (comparisonCode == null ? null : comparisonCode.trim());
	}
	public String getComparisonValue() {
		return comparisonValue;
	}
	public void setComparisonValue(String comparisonValue) {
		this.comparisonValue = (comparisonValue == null ? null : comparisonValue.trim());
	}
	public String getBaseline() {
		return baseline;
	}
	public void setBaseline(String baseline) {
		this.baseline = (baseline == null ? null : baseline.trim());
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = (result == null ? null : result.trim());
	}
	public String getExecutor() {
		return executor;
	}
	public void setExecutor(String executor) {
		this.executor = (executor == null ? null : executor.trim());
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = (priority == null ? null : priority.trim());
	}
	public String getContinueFlag() {
		return continueFlag;
	}
	public void setContinueFlag(String continueFlag) {
		this.continueFlag = (continueFlag == null ? null : continueFlag.trim());
	}
	public String getParentItemNo() {
		return parentItemNo;
	}
	public void setParentItemNo(String parentItemNo) {
		this.parentItemNo = (parentItemNo == null ? null : parentItemNo.trim());
	}
	public String getGroupExpress() {
		return groupExpress;
	}
	public void setGroupExpress(String groupExpress) {
		this.groupExpress = (groupExpress == null ? null : groupExpress.trim());
	}
	public Object getAttach() {
		return attach;
	}
	public void setAttach(Object attachment) {
		if(attachment instanceof String){
			this.attach = (attachment == null ? null : ((String)attachment).trim());
		}else{
			this.attach = attachment;
		}
	}

	public void setRemark(String remark) {
		this.attach = (remark == null ? null : remark.trim());
	}

	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = (comments == null ? null : comments.trim());
	}
	public String getEnableFlag() {
		return enableFlag;
	}
	public void setEnableFlag(String enableFlag) {
		this.enableFlag = (enableFlag == null ? null : enableFlag.trim());
	}

	public void setEnableFlag(boolean enableFlag) {
		this.enableFlag = (enableFlag  ? "1" : "2");
	}

	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}


}
