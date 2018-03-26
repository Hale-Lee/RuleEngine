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

package tech.kiwa.engine.component;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tech.kiwa.engine.entity.RESULT;
import tech.kiwa.engine.entity.RuleItem;
import tech.kiwa.engine.exception.RuleEngineException;


/**
 * @author Hale.Li
 * @since  2018-01-28
 * @version 0.1
 */
public abstract class AbstractRuleReader {

	public abstract List<RuleItem> readRuleItemList() throws RuleEngineException ;
	public abstract Long getRuleItemCount() throws RuleEngineException;
	public abstract RuleItem getRuleItem(String ruleId) throws RuleEngineException;

	protected List<RuleItem> ruleItemCache = new ArrayList<RuleItem>();

	private static Logger log = LoggerFactory.getLogger(AbstractRuleReader.class);

	/**
	 * Sort the same level rule item, and filter the items not in the same level.
	 * @param itemList  排序的对象列表
	 * @param parentItem  父级规则
	 * @return  排好序的规则列表
	 */
	public List<RuleItem> sortItem(List<RuleItem> itemList, String parentItem){

		List<RuleItem> retItemList = new ArrayList<RuleItem>();
		List<RuleItem> tempItemList = new ArrayList<RuleItem>();

		//同一层级的item，
		for(RuleItem item: itemList){
			if(StringUtils.isEmpty(parentItem)){

				if(StringUtils.isEmpty(item.getParentItemNo())){
					tempItemList.add(item);
				}

			}else if(parentItem.equals(item.getParentItemNo())){
				tempItemList.add(item);
			}
		}

		while(!tempItemList.isEmpty()){
			/*
			int minIndex  = queryMiniumPriority(tempItemList);
			retItemList.add(tempItemList.get(minIndex));
			tempItemList.remove(minIndex);
			*/
			int maxIndex  = queryMaxiumPriority(tempItemList);
			retItemList.add(tempItemList.get(maxIndex));
			tempItemList.remove(maxIndex);
		}

		return retItemList;

	}

	/**
	 * 在规则列表中查找最小的优先级
	 * @param itemList  查找的对象列表
	 * @return 最小的优先级
	 */
	@SuppressWarnings("unused")
	private int queryMiniumPriority( List<RuleItem> itemList){

		int priority = Integer.MAX_VALUE;
		int minIndex = Integer.MAX_VALUE;

		for(int iLoop = 0 ;iLoop < itemList.size(); iLoop++){

			try{
				int current  =  Integer.valueOf(itemList.get(iLoop).getPriority());

				if( priority > current ){
					priority = current;
					minIndex = iLoop;
				}
			}catch (java.lang.NumberFormatException e){
				log.debug(e.getLocalizedMessage());
			}
		}

		return minIndex;

	}

	/**
	 * 在规则列表中查找最大的优先级
	 * @param itemList  查找的对象列表
	 * @return  最大的优先级
	 */
	private int queryMaxiumPriority( List<RuleItem> itemList){

		int priority = Integer.MIN_VALUE;
		int minIndex = Integer.MIN_VALUE;

		for(int iLoop = 0 ;iLoop < itemList.size(); iLoop++){

			try{
				int current  =  Integer.valueOf(itemList.get(iLoop).getPriority());

				if( priority < current ){
					priority = current;
					minIndex = iLoop;
				}
			}catch (java.lang.NumberFormatException e){
				log.debug(e.getLocalizedMessage());
			}
		}

		return minIndex;

	}

	/**
	 * 查找同层级的规则列表
	 * @param itemList     要查找的对象
	 * @param parentItemNo  父级规则
	 * @return 同层级的规则列表
	 */
	public List<RuleItem> filterItem(List<RuleItem> itemList, String parentItemNo){

		List<RuleItem> newItemList = new ArrayList<RuleItem>();

		for(int iLoop = 0 ;iLoop < itemList.size(); iLoop++){

			RuleItem  item= itemList.get(iLoop);
			if(StringUtils.isEmpty(parentItemNo)){
				if(StringUtils.isEmpty(item.getParentItemNo())){
					newItemList.add(item);
				}
			}else{
				if(parentItemNo.equalsIgnoreCase(item.getParentItemNo())){
					newItemList.add(item);
				}
			}
		}

		return newItemList;

	}

	/**
	 * 检查规则的格式是否正确
	 * @param item          规则定义体
	 * @return true -- 合法 false　-- 不合法
	 */
	public boolean preCompile(RuleItem item){

		boolean bRet = true;
		if(StringUtils.isEmpty(item.getItemNo())){
			log.debug("ruleId cannot be empty.");
			bRet = false;
		}

		// single normal item.
		if(StringUtils.isEmpty(item.getParentItemNo()) && StringUtils.isEmpty(item.getGroupExpress())){

			if(StringUtils.isEmpty(item.getPriority())){
				log.debug("priority cannot be empty and must be numberic if it's free-running rule. ruleid ={}" , item.getItemNo());
				bRet = false;
			}

			if(!StringUtils.isNumeric(item.getPriority())){
				log.debug("priority cannot be empty and must be numberic if it's free-running rule. ruleid ={}" , item.getItemNo());
				bRet = false;
			}

			if(!StringUtils.isNumeric(item.getContinueFlag())){
				log.debug("continue flag cannot be empty and must be numberic if it's free-running rule. ruleid ={}" , item.getItemNo());
				bRet = false;
			}

			if(!StringUtils.isNumeric(item.getResult())){
				RESULT result = RESULT.EMPTY ;
				if(!result.parse(item.getResult())){
					log.debug("result cannot be empty and must be numberic if it's free-running rule. ruleid ={}" , item.getItemNo());
					bRet = false;
				}
			}

			if(StringUtils.isEmpty(item.getExeClass()) && StringUtils.isEmpty(item.getExeSql())){
				log.debug("either exe_class or exe_sql must be inputted. ruleid ={}" , item.getItemNo());
				bRet = false;
			}

			if(StringUtils.isEmpty(item.getBaseline())){
				log.debug("baseline must be inputted. ruleid ={}" , item.getItemNo());
				bRet = false;
			}

			if(StringUtils.isEmpty(item.getComparisonCode())){
				log.debug("comparison code must be inputted. ruleid ={}" , item.getItemNo());
				bRet = false;
			}

			if(StringUtils.isNotEmpty(item.getExeSql())){
				String sql = item.getExeSql();
				if(sql.contains("?")){
					if(StringUtils.isEmpty(item.getParamName())){

						log.debug("param name must be inputted since your exe_sql has parameters. ruleid ={}" , item.getItemNo());
						bRet = false;
					}
				}
			}
		}
		// group check, parent group.
		if( StringUtils.isNotEmpty(item.getGroupExpress()) ){

			if(StringUtils.isEmpty(item.getPriority())){
				log.debug("priority cannot be empty and must be numberic if it's free-running rule. ruleid ={}" , item.getItemNo());
				bRet = false;
			}

			if(!StringUtils.isNumeric(item.getPriority())){
				log.debug("priority cannot be empty and must be numberic if it's free-running rule. ruleid ={}" , item.getItemNo());
				bRet = false;
			}

			if(!StringUtils.isNumeric(item.getContinueFlag())){
				log.debug("continue flag cannot be empty and must be numberic if it's free-running rule. ruleid ={}" , item.getItemNo());
				bRet = false;
			}

			if(!StringUtils.isNumeric(item.getResult())){
				RESULT result = RESULT.EMPTY ;
				if(!result.parse(item.getResult())){
					log.debug("result cannot be empty and must be numberic if it's free-running rule. ruleid ={}" , item.getItemNo());
					bRet = false;
				}
			}

		}

		//sub item check, simple rule , without complex express.
		if(StringUtils.isNotEmpty(item.getParentItemNo())){

			if(StringUtils.isEmpty(item.getExeClass()) && StringUtils.isEmpty(item.getExeSql())){
				log.debug("either java_class or exe_sql must be inputted. ruleid ={}" , item.getItemNo());
				bRet = false;
			}

			if(StringUtils.isEmpty(item.getBaseline())){
				log.debug("baseline must be inputted. ruleid ={}" , item.getItemNo());
				bRet = false;
			}

			if(StringUtils.isEmpty(item.getComparisonCode())){
				log.debug("comparison code must be inputted. ruleid ={}" , item.getItemNo());
				bRet = false;
			}

			if(StringUtils.isNotEmpty(item.getExeSql())){
				String sql = item.getExeSql();
				if(sql.contains("?")){
					if(StringUtils.isEmpty(item.getParamName())){

						log.debug("param name must be inputted since your exe_sql has parameters. ruleid ={}" , item.getItemNo());
						bRet = false;
					}
				}
			}
		}

		return bRet;
	}

	/**
	 * 缓存清除的接口。
	 */
	public void clearRuleItemCache(){

		synchronized(ruleItemCache){
			ruleItemCache.clear();
		}

	}

	public void updateRuleItem(RuleItem item){

		for(int iLoop =0; iLoop <  ruleItemCache.size(); iLoop++){

			RuleItem element = ruleItemCache.get(iLoop);

			if(element.getItemNo().equalsIgnoreCase(item.getItemNo())){
				synchronized(ruleItemCache){
					ruleItemCache.set(iLoop, item);
				}
			}
		}
	}
}
