package com.neu.crm;

import com.neu.crm.bean.ClassificationDataSource;
import com.neu.crm.bean.ClientBaseInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NoSpringBootTest {
    @Test
    void testOr(){
        System.out.println(1|0);
        System.out.println(2|0);
        System.out.println(2|1);
        System.out.println(3|1);
        System.out.println(3|2);
        System.out.println(3&2);
        System.out.println(3&1);
        System.out.println(2&1);
    }

    @Test
    void testBeanUtils(){
        ClientBaseInfo clientBaseInfo = new ClientBaseInfo();
        clientBaseInfo.setClientId(1);
        clientBaseInfo.setBirthday("1998-10-16");
        ClassificationDataSource classificationDataSource = new ClassificationDataSource();
        BeanUtils.copyProperties(clientBaseInfo,classificationDataSource);
        System.out.println(classificationDataSource);
    }

    @Test
    void testSimpleDateFormat() throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = simpleDateFormat.parse("1998-10-16");
        System.out.println(date);
        Calendar c=Calendar.getInstance();
        System.out.println(c.get(Calendar.YEAR));
        c.setTime(date);
        System.out.println(c.get(Calendar.YEAR));

        System.out.println("______________________________");
        Calendar calendar=Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        int birthYear=calendar.get(Calendar.YEAR);
    }
}
