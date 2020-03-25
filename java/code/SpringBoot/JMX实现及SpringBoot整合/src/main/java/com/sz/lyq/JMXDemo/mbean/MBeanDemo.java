package com.sz.lyq.JMXDemo.mbean;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class MBeanDemo {
    public static void main(String[] args) throws Exception {
        //mbean服务器
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();

        SimpleData simpleData = new SimpleData();

        ObjectName objectName = createObjectName(simpleData);

        //注册mbean
        mBeanServer.registerMBean(simpleData, objectName);

        Thread.sleep(Long.MAX_VALUE);

    }

    /**
     * 创建objectName
     * @param mbean
     * @return
     */
    private static ObjectName createObjectName(Object mbean) throws MalformedObjectNameException {
        Class<?> objectClass = mbean.getClass();
        String packageName = objectClass.getPackage().getName();
        String simpleName = objectClass.getSimpleName();
        return new ObjectName(packageName + ":type=" + simpleName);
    }
}
