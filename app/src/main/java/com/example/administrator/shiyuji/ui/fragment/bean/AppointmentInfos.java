package com.example.administrator.shiyuji.ui.fragment.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2019/10/8.
 */

public class AppointmentInfos implements Serializable{

    private List<AppointmentInfo> appointmentInfos;

    public AppointmentInfos() {
    }

    public List<AppointmentInfo> getAppointmentInfos() {
        return appointmentInfos;
    }

    public void setAppointmentInfos(List<AppointmentInfo> appointmentInfos) {
        this.appointmentInfos = appointmentInfos;
    }
}
