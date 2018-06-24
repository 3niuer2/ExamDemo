package com.migu.schedule;

import com.migu.schedule.info.MyTaskInfo;

import java.util.LinkedList;

public class ServerNode implements Comparable{
    int totalConsume;
    private LinkedList<MyTaskInfo> infos;

    public ServerNode() {
        totalConsume = 0;
        infos = new LinkedList<MyTaskInfo>();
    }

    public void addTask(MyTaskInfo info){
        infos.add(info);
        totalConsume+= info.getConsumption();
    }

    public void removeTask(MyTaskInfo info){
        totalConsume-=info.getConsumption();
        infos.remove(info);
    }

    public LinkedList<MyTaskInfo> getInfos() {
        return infos;
    }

    public void setInfos(LinkedList<MyTaskInfo> infos) {
        this.infos = infos;
    }

    public int getTotalConsume() {
        return totalConsume;
    }

    public int compareTo(Object o) {
        return this.totalConsume - ((ServerNode) o).totalConsume != 0
                ? this.totalConsume - ((ServerNode) o).totalConsume
                : this.infos.size() - ((ServerNode) o).infos.size();
    }

    public void reset(){
        infos.clear();
        totalConsume = 0;
    }

}
