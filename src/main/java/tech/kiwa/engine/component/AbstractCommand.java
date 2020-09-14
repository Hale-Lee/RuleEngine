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

import tech.kiwa.engine.entity.ItemExecutedResult;
import tech.kiwa.engine.entity.RuleItem;
/**
 * 命令接口，如果需要自定义Rule地执行部分，那么该部分必须从这个父类继承。
 * @author Hale.Li
 * @since  2018-01-28
 * @version 0.1
 */
public abstract class AbstractCommand {

	public abstract void execute(RuleItem item, ItemExecutedResult result);

	public abstract void SetObject(Object obj);

}
