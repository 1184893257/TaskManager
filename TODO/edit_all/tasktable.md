# ���ģ��TopTaskModel<E extends Task>(����Ϊ������)(���)

1. ����(�����е���showTasks(true)),�������:Today��������Ķ���(���)
1. showTasks(����getTasks),ͳ����ʱ��,father���Ϊnull��ʾ"NULL"(���)
1. abstract getTasks,����ֵ����:TaskMap<E,? extends Task>(���)
1. isCellEditable����false(���)
1. getClass����String.class(���)

# ������TopTaskTable<E extends Task>(�ǳ�����,����Ҳ��)

1. ����(���)
    2. TopTaskTable�Ĺ������
        3. TaskDialog dialog
        3. int contentCol(��Ⱦ����,�¼��л���������ݵ��к�)
        3. boolean canHighlight
        3. Updater updater
        3. TopTaskTable father
        3. TopTaskModel model
    2. �˵� ��������\��ʾ����ɵ������JCheckBox
    2. ���췽����ע���Ҽ��˵�,����delete��,�˵���ť
    2. ��Ⱦ��������һ��
1. updateFromMem,updateFromFile
1. add remove modify, ���е���model��getTasks
1. actionPerformed�е���add remove modify,���Ҫˢ���ϲ���Ͷ������
1. ��Ⱦ(ֻ�е��յ������ܱ���ȾΪ���,Ҳ����canHighlightֻ��today.day��true)

***

# ���ģ�͵�����

��ʾ-����(��Ŀȫ�ǰ�)(����һ����)

1. ���췽���в���
    2. Today today
    2. Updater updater
    2. TopTaskTable father(����setValue��ˢ��,updateFromMem����)
1. ʵ��getTasks,����today.day
1. ��дisCellEditable,����һ����������true
1. ��дsetValueAt,ʵ�����񼤻�\���,���ʹ��updater��Ա
1. ��дshowTasks
1. ��дgetClass

# ������������

��ʾ-����(����Ҫ�ٽ�һ����,��Ϊû�Ĺ��췽��,û���µ�public����)

1. �µĹ��췽��
    2. ���ø���Ĺ��췽��
    2. ����ÿ�еĿ��,�Ƽ���ѭ��
1. ��дmodify,remove,��Ϊ�޸�\ɾ���Ŀ����ǵ�ǰ����ִ�е���
