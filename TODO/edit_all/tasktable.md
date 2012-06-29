# 表格模型TopTaskModel<E extends Task>(必须为抽象类)(完成)

1. 构造(构造中调用showTasks(true)),构造参数:Today及其子类的对象(完成)
1. showTasks(调用getTasks),统计总时间,father如果为null显示"NULL"(完成)
1. abstract getTasks,返回值类型:TaskMap<E,? extends Task>(完成)
1. isCellEditable返回false(完成)
1. getClass返回String.class(完成)

# 表格组件TopTaskTable<E extends Task>(非抽象类,抽象也可)

1. 构造(完成)
    2. TopTaskTable的构造参数
        3. TaskDialog dialog
        3. int contentCol(渲染的行,事件中获得任务内容的列号)
        3. boolean canHighlight
        3. Updater updater
        3. TopTaskTable father
        3. TopTaskModel model
    2. 菜单 包含隐藏\显示已完成的任务的JCheckBox
    2. 构造方法中注册右键菜单,监听delete键,菜单按钮
    2. 渲染任务名那一栏
1. updateFromMem,updateFromFile
1. add remove modify, 其中调用model的getTasks
1. actionPerformed中调用add remove modify,最后还要刷新上层表格和顶层界面
1. 渲染(只有当日的任务能被渲染为红粗,也就是canHighlight只有today.day置true)

***

# 表格模型的特例

显示-今天(面目全非啊)(需另建一个类)

1. 构造方法中参数
    2. Today today
    2. Updater updater
    2. TopTaskTable father(用于setValue中刷新,updateFromMem就行)
1. 实现getTasks,返回today.day
1. 重写isCellEditable,满足一定条件返回true
1. 重写setValueAt,实现任务激活\完成,最后使用updater成员
1. 重写showTasks
1. 重写getClass

# 表格组件的特例

显示-今天(不需要再建一个类,因为没改构造方法,没有新的public方法)

1. 新的构造方法
    2. 调用父类的构造方法
    2. 调整每列的宽度,推荐用循环
1. 重写modify,remove,因为修改\删除的可能是当前正在执行的任
