package com.migu.schedule;


import com.migu.schedule.constants.ReturnCodeKeys;
import com.migu.schedule.info.MyTaskInfo;
import com.migu.schedule.info.TaskInfo;

import java.util.*;

/*
*类名和方法不能修改
 */
public class Schedule {
    Map<Integer, ServerNode> registerNode;
    Map<Integer, ServerNode> hangUpNode;

    LinkedList<MyTaskInfo> runningTaskList;
    LinkedList<MyTaskInfo> hungUpaskList;


    public int init() {
        // TODO 方法未实现
        if (registerNode == null) {
            registerNode = new HashMap<Integer, ServerNode>();
        }
        registerNode.clear();
        if (hangUpNode == null) {
            hangUpNode = new HashMap<Integer, ServerNode>();
        }
        hangUpNode.clear();

        if (runningTaskList == null) {
            runningTaskList = new LinkedList<MyTaskInfo>();
        }
        runningTaskList.clear();

        if (hungUpaskList == null) {
            hungUpaskList = new LinkedList<MyTaskInfo>();
        }
        hungUpaskList.clear();
        return ReturnCodeKeys.E001;
    }


    public int registerNode(int nodeId) {
        if (nodeId <= 0) {
            return ReturnCodeKeys.E004;
        }
        if (registerNode.containsKey(nodeId)) {
            return ReturnCodeKeys.E005;
        } else {
            ServerNode serverNode = new ServerNode();
            registerNode.put(nodeId, serverNode);
            return ReturnCodeKeys.E003;
        }
        // TODO 方法未实现

    }

    public int unregisterNode(int nodeId) {
        // TODO 方法未实现
//        注销成功，返回E006:服务节点注销成功。
//        如果服务节点编号小于等于0, 返回E004:服务节点编号非法。
//        如果服务节点编号未被注册, 返回E007:服务节点不存在。
        if (nodeId <= 0) {
            return ReturnCodeKeys.E004;
        }

        if (registerNode.containsKey(nodeId)) {
            hangUpNode.put(nodeId, registerNode.get(nodeId));
            registerNode.remove(nodeId);
            return ReturnCodeKeys.E006;
        } else {
            return ReturnCodeKeys.E007;
        }

    }


    public int addTask(int taskId, int consumption) {
        /*添加成功，返回E008任务添加成功。
        如果任务编号小于等于0, 返回E009:任务编号非法。
        如果相同任务编号任务已经被添加, 返回E010:任务已添加。*/
        if (taskId <= 0) {
            return ReturnCodeKeys.E009;
        }
        Iterator<MyTaskInfo> tmep = hungUpaskList.iterator();
        boolean exist = false;
        if (!exist && tmep.hasNext()) {
            if (tmep.next().getTaskId() == taskId) {
                exist = true;
            }
        }

        if (exist) {
            return ReturnCodeKeys.E010;
        } else {
            MyTaskInfo newTask = new MyTaskInfo(consumption);
            newTask.setTaskId(taskId);
            hungUpaskList.add(newTask);
            return ReturnCodeKeys.E008;
        }

        // TODO 方法未实现

    }


    public int deleteTask(int taskId) {
       /* 删除成功，返回E011:任务删除成功。
        如果任务编号小于等于0, 返回E009:任务编号非法。
        如果指定编号的任务未被添加, 返回E012:任务不存在。*/

        if (taskId <= 0) {
            return ReturnCodeKeys.E009;
        }

       if (!hungUpaskList.isEmpty()){
           Iterator<MyTaskInfo> tmep = hungUpaskList.iterator();
           boolean exist = false;
           if (!exist && tmep.hasNext()) {
               MyTaskInfo o = (MyTaskInfo)tmep.next();
               if (o.getTaskId() == taskId) {
                   exist = true;
                   hungUpaskList.remove(o);

                   return ReturnCodeKeys.E011;
               }
           }
       }else if (!runningTaskList.isEmpty()){
           Iterator<MyTaskInfo> tmep = runningTaskList.iterator();
           boolean exist = false;
           while (!exist && tmep.hasNext()) {
               MyTaskInfo o = (MyTaskInfo)tmep.next();
               if (o.getTaskId() == taskId) {
                   exist = true;
                   registerNode.get(o.getNodeId()).removeTask(o);
                   runningTaskList.remove(o);
                   return ReturnCodeKeys.E011;
               }
           }
       }

        // TODO 方法未实现
        return ReturnCodeKeys.E012;
    }


    public int scheduleTask(int threshold) {
        /*如果调度阈值取值错误，返回E002调度阈值非法。
        如果获得最佳迁移方案, 进行了任务的迁移,返回E013: 任务调度成功;
        如果所有迁移方案中，总会有任意两台服务器的总消耗率差值大于阈值。则认为没有合适的迁移方案,返回 E014:无合适迁移方案;*/
        if (threshold <= 0) {
            return ReturnCodeKeys.E002;
        }

        if (!hungUpaskList.isEmpty()) {
            doShechule(hungUpaskList,false);
            runningTaskList.addAll(hungUpaskList);
            hungUpaskList.clear();
        } else if (!runningTaskList.isEmpty()) {
            doShechule(runningTaskList,true);
        }
        if (!shechuleResult(threshold)) {
            return ReturnCodeKeys.E013;
        }else {
            return ReturnCodeKeys.E014;
        }

    }


        private void doShechule (LinkedList<MyTaskInfo> infos,boolean needClear) {

            Object[] keys = registerNode.keySet().toArray();

            if (needClear){
                Iterator<ServerNode> iterator = registerNode.values().iterator();
                while (iterator.hasNext()){
                    iterator.next().reset();
                }
            }

            Arrays.sort(keys);
            Object[] nodes = registerNode.values().toArray();
            Object[] sortInfos = infos.toArray();
            Arrays.sort(sortInfos);
            for (int i = sortInfos.length - 1; i >= 0; i--) {
                Arrays.sort(nodes);
                MyTaskInfo temp = (MyTaskInfo) sortInfos[i];
                ((ServerNode) nodes[0]).addTask(temp);
            }
            Arrays.sort(nodes);
            for (int j = 0; j < keys.length; j++) {
                ServerNode temp = (ServerNode) nodes[j];
                Iterator<MyTaskInfo> myTaskInfoIterator = temp.getInfos().iterator();
                while (myTaskInfoIterator.hasNext()) {
                    myTaskInfoIterator.next().setNodeId((Integer) keys[j]);
                }
                registerNode.put((Integer) (keys[j]), temp);

            }
        }

    public boolean shechuleResult(int threshold){
        boolean result = false;
        Object[] keys = registerNode.keySet().toArray();
        Arrays.sort(keys);
        Object[] nodes = registerNode.values().toArray();
        Arrays.sort(nodes);
        for (int i = 0; i < keys.length-1; i++) {
            for (int j = i+1; j < keys.length; j++) {
                if (((ServerNode) nodes[j]).getTotalConsume() - ((ServerNode) nodes[i]).getTotalConsume() > threshold) {
                    result = true;
                }
            }
        }

        return result;
    }


        public int queryTaskStatus (List < TaskInfo > tasks) {
            /*如果查询结果参数tasks为null，返回E016:参数列表非法
            如果查询成功, 返回E015: 查询任务状态成功;查询结果从参数Tasks返回。*/
            // TODO 方法未实现
            if (runningTaskList.size()>0){
                Iterator<MyTaskInfo> temp = runningTaskList.iterator();
                while (temp.hasNext()){
                    MyTaskInfo mti = temp.next();
                    TaskInfo ti = new TaskInfo();
                    ti.setNodeId(mti.getNodeId());
                    ti.setTaskId(mti.getTaskId());
                    tasks.add(ti);
                }
                return  ReturnCodeKeys.E015;
            }else {
                return ReturnCodeKeys.E016;
            }

        }

    }
