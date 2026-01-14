package com.gerp.platform.feishu.server.config;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * @author huangmeng
 * @date 2022/1/26 9:12
 */
@Component
public class IpConvert extends ClassicConverter implements ApplicationContextAware {
    private static String ip = "0";

    public IpConvert() {
    }

    public String convert(ILoggingEvent event) {
        return ip;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        InetAddress[] allByName = new InetAddress[0];

        try {
            allByName = InetAddress.getAllByName(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException var4) {
            var4.printStackTrace();
        }

        Object[] array = Arrays.stream(allByName).filter((inetAddress) -> {
            return inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress();
        }).map((x) -> {
            return x.getHostAddress();
        }).toArray();
        if (array != null && array.length > 0) {
            ip = array[0].toString();
        }

    }
}
