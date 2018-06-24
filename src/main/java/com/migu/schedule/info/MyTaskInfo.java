package com.migu.schedule.info;

public class MyTaskInfo extends TaskInfo implements Comparable{
    private int consumption;//消耗量

    public MyTaskInfo(int consumption ) {
        this.consumption = consumption;
    }

    public int getConsumption() {
        return consumption;
    }

    public void setConsumption(int consumption) {
        this.consumption = consumption;
    }

    @Override
    public String toString() {
        return super.toString() + "[consumption="  + consumption + "]";
    }

    public int compareTo(Object o) {
        return getConsumption() - ((MyTaskInfo) o).getConsumption() != 0
                ? getConsumption() - ((MyTaskInfo) o).getConsumption()
                : this.getTaskId() - ((MyTaskInfo) o).getTaskId();
    }
}
