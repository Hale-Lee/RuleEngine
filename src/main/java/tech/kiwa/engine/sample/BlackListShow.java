package tech.kiwa.engine.sample;

import tech.kiwa.engine.component.AbstractCommand;
import tech.kiwa.engine.entity.ItemExecutedResult;
import tech.kiwa.engine.entity.RuleItem;

public class BlackListShow extends AbstractCommand {

	private Object obj = null;
	@Override
	public void execute(RuleItem item, ItemExecutedResult result) {
		// TODO Auto-generated method stub
		System.out.print(obj.toString() + "该用户已经在黑名单了。");
	}

	@Override
	public void SetObject(Object obj) {
		// TODO Auto-generated method stub
		this.obj = obj;
	}

}
