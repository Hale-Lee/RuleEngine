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

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeansException;

import tech.kiwa.engine.entity.ItemExecutedResult;
import tech.kiwa.engine.entity.RuleItem;
import tech.kiwa.engine.exception.EmptyResultSetException;
import tech.kiwa.engine.exception.RuleEngineException;
import tech.kiwa.engine.framework.OperatorFactory;
import tech.kiwa.engine.utility.SpringContextHelper;

/**
 * @author Hale.Li
 * @since 2018-01-28
 * @version 0.1
 */
public abstract class AbstractRuleItem {

	protected Object object;

	public void setObject(Object object) {
		this.object = object;
	}

	/**
	 * 获取SpringMVC中的Service对象，如果未使用SpringMVC，则返回null.
	 *
	 * @param seriveName
	 *            Service名称，
	 * @return 返回的Serivce对象。
	 */
	protected Object getService(String seriveName) {

		Object objRet = null;

		try {
			objRet = SpringContextHelper.getBean(seriveName);
		} catch (BeansException e) {

		}

		return objRet;
	}

	/**
	 * 获取SpringMVC中的Service对象。 如果未使用SpringMVC，则返回null.
	 *
	 * @param <T> 参数类型的模板类
	 * @param requiredType
	 *        Serivce的类型，即具体的类。
	 * @return 返回的Serivce对象。
	 */
	protected <T> T getService(Class<T> requiredType) {

		T objRet = null;
		try {
			objRet = SpringContextHelper.getBean(requiredType);
		} catch (BeansException e) {

		}
		return objRet;

	}

	public abstract ItemExecutedResult doCheck(RuleItem item) throws RuleEngineException;

	/**
	 * analyze the data of when condition.
	 *
	 * @param resultSet
	 *            返回的结果集，从数据库读出来的
	 * @param item
	 *            规则定义体
	 * @return 运行是成功还是失败 true or false.
	 * @throws EmptyResultSetException
	 *             结果集是空时的的返回．
	 * @throws RuleEngineException
	 *             其他的异常
	 */
	protected boolean analyze(Map<String, Object> resultSet, RuleItem item)
			throws EmptyResultSetException, RuleEngineException {

		String retValue = null;

		//
		if (resultSet.containsKey("cnt")) {

			retValue = resultSet.get("cnt").toString();

		} else if (resultSet.size() == 1) {

			Set<String> keySet = (Set<String>) resultSet.keySet();
			for (String key : keySet) {

				if (null == resultSet.get(key)) {
					retValue = null;
				} else if (resultSet.get(key) instanceof String) {
					retValue = (String) resultSet.get(key);
				} else {
					retValue = resultSet.get(key).toString();
				}

			}

		} else if (resultSet.size() > 1) {
			// read the first data.
			Set<String> keySet = (Set<String>) resultSet.keySet();
			for (String key : keySet) {

				if (null == resultSet.get(key)) {
					retValue = null;
				} else if (resultSet.get(key) instanceof String) {
					retValue = (String) resultSet.get(key);
				} else {
					retValue = resultSet.get(key).toString();
				}
				break;
			}

			// 如果数据返回为空，那么默认为审核通过
		} else if (resultSet.isEmpty()) {

			throw new EmptyResultSetException("resultset is empty.");
			// return true;
		} else {
			throw new RuleEngineException("unknow resultset.");
		}

		boolean bRet = false;

		// try {
		bRet = comparisonOperate(retValue, item.getComparisonCode(), item.getBaseline());
		// } catch (RuleEngineException e) {
		// throw e;
		// }

		return bRet;
	}

	/**
	 * 比较运算操作，将执行的结果和RuleItem中的baseline作比较。
	 *
	 * @param subject
	 *            比较对象（运行结果）
	 * @param comparisonCode
	 *            比较操作符号，在OperationFactory中定义。
	 * @param baseline
	 *            比较基线，用于比较的对象。
	 * @return 根据ComparisonCode运行的结果。 true or flase。
	 * @throws RuleEngineException
	 *             参数不合法，或者比较操作符不合法。
	 */
	public static boolean comparisonOperate(String subject, String comparisonCode, String baseline)
			throws RuleEngineException {

		boolean bRet = false;

		if (null == subject || null == baseline || null == comparisonCode) {
			throw new RuleEngineException("null pointer error of subject or baseline or comparison code.");
		}

		BigDecimal bdSubject = null;
		BigDecimal object = null;

		switch (comparisonCode) {

		case OperatorFactory.OPR_CODE.EQUAL:
			try {
				bdSubject = new BigDecimal(subject);
				object = new BigDecimal(baseline);
				bRet = (bdSubject.compareTo(object) == 0);
			} catch (Exception e) {
				bRet = subject.equals(baseline);
			}
			break;
		case OperatorFactory.OPR_CODE.GREATER:
			try {
				bdSubject = new BigDecimal(subject);
				object = new BigDecimal(baseline);
				bRet = (bdSubject.compareTo(object) > 0);
			} catch (Exception e1) {
				bRet = (subject.compareTo(baseline) > 0);
			}
			break;
		case OperatorFactory.OPR_CODE.LESS:
			try {
				bdSubject = new BigDecimal(subject);
				object = new BigDecimal(baseline);
				bRet = (bdSubject.compareTo(object) < 0);
			} catch (Exception e1) {
				bRet = (subject.compareTo(baseline) < 0);
			}
			break;
		case OperatorFactory.OPR_CODE.NOT_EQUAL:
			try {
				bdSubject = new BigDecimal(subject);
				object = new BigDecimal(baseline);
				bRet = (bdSubject.compareTo(object) != 0);
			} catch (Exception e) {
				bRet = !subject.equals(baseline);
			}
			break;
		case OperatorFactory.OPR_CODE.GREATER_EQUAL:
			try {
				bdSubject = new BigDecimal(subject);
				object = new BigDecimal(baseline);
				bRet = (bdSubject.compareTo(object) >= 0);
			} catch (Exception e) {
				bRet = (subject.compareTo(baseline) >= 0);
			}
			break;
		case OperatorFactory.OPR_CODE.LESS_EQUAL:
			try {
				bdSubject = new BigDecimal(subject);
				object = new BigDecimal(baseline);
				bRet = (bdSubject.compareTo(object) <= 0);
			} catch (Exception e) {
				bRet = (subject.compareTo(baseline) <= 0);
			}
			break;
		case OperatorFactory.OPR_CODE.INCLUDE:
			bRet = subject.contains(baseline);
			break;
		case OperatorFactory.OPR_CODE.NOT_INCLUDE:
			bRet = !subject.contains(baseline);
			break;
		case OperatorFactory.OPR_CODE.INCLUDED_BY:
			bRet = baseline.contains(subject);
			break;
		case OperatorFactory.OPR_CODE.NOT_INCLUDED_BY:
			bRet = !baseline.contains(subject);
			break;
		case OperatorFactory.OPR_CODE.EQUAL_IGNORE_CASE:
			bRet = subject.equalsIgnoreCase(baseline);
			break;
		case OperatorFactory.OPR_CODE.NOT_EQUAL_IGNORE_CASE:
			bRet = !subject.equalsIgnoreCase(baseline);
			break;
		case OperatorFactory.OPR_CODE.MATCH:
			bRet = subject.matches(baseline);
			break;
		case OperatorFactory.OPR_CODE.UNMATCH:
			bRet = !subject.matches(baseline);
			break;
		default:
			bRet = extendComparisonOperate(subject, comparisonCode, baseline);
			break;
		}

		return bRet;
	}

	private static boolean extendComparisonOperate(String subject, String comparisonCode, String baseline)
			throws RuleEngineException {

		OperatorFactory optMgr = OperatorFactory.getInstance();
		return optMgr.runOperator(subject, comparisonCode, baseline);

	}
}
