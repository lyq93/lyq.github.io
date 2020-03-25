package com.sz.lyq.JMXDemo.mbean;

import javax.management.*;

import java.util.concurrent.atomic.AtomicLong;

import static javax.management.AttributeChangeNotification.ATTRIBUTE_CHANGE;

public class SimpleData extends NotificationBroadcasterSupport implements SimpleDataMBean, NotificationListener,NotificationFilter
{

    private String data;

    private final AtomicLong sequenceNumber = new AtomicLong();

    public SimpleData() {
        this.addNotificationListener(this,this,null);
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public void setData(String data) {
        String oldData = this.data;
        this.data = data;

        Notification notification = new AttributeChangeNotification(this,
                sequenceNumber.incrementAndGet(),
                System.currentTimeMillis(),
                "old value:" + oldData + "change to new value:" + this.data,
                "data",
                String.class.getName(),
                oldData,
                this.data);

        sendNotification(notification);
    }

    @Override
    public String display() {
        return data;
    }

    public MBeanNotificationInfo[] getNotificationInfo() {
        return new MBeanNotificationInfo[]{
                new MBeanNotificationInfo(new String[]{ATTRIBUTE_CHANGE},"Data change notification","数据改变通知")
        };
    }

    @Override
    public boolean isNotificationEnabled(Notification notification) {
        //只关心attributeChange的通知并且属性名是data的
        if(AttributeChangeNotification.class.isAssignableFrom(notification.getClass())) {
            AttributeChangeNotification attributeChangeNotification = AttributeChangeNotification.class.cast(notification);
            if("data".equals(attributeChangeNotification.getAttributeName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void handleNotification(Notification notification, Object handback) {
        AttributeChangeNotification attributeChangeNotification = (AttributeChangeNotification) notification;
        String oldValue = (String)attributeChangeNotification.getOldValue();
        String newValue = (String)attributeChangeNotification.getNewValue();
        System.out.println("the notification of data's oldValue:" + oldValue + "newValue:" + newValue);
    }
}
