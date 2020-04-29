package com.neu.crm.service.impl;

import com.neu.crm.bean.*;
import com.neu.crm.mapper.HotelRoomMapper;
import com.neu.crm.mapper.HotelServiceMapper;
import com.neu.crm.service.ClientAccommodationService;
import com.neu.crm.service.ClientBaseInfoService;
import com.neu.crm.service.ClientConsumeService;
import com.neu.crm.service.TradeRecordService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.LinkedList;
import java.util.List;

@Service
public class TradeRecordServiceImpl implements TradeRecordService {

    @Autowired
    ClientBaseInfoService baseInfoService;
    @Autowired
    ClientAccommodationService accommodationService;
    @Autowired
    ClientConsumeService consumeService;
    @Autowired
    HotelRoomMapper hotelRoomMapper;
    @Autowired
    HotelServiceMapper hotelServiceMapper;


    @Override
    public List<TradeRecord> getClientTradeRecords(int clientId) {
        List<TradeRecord> tradeRecords=new LinkedList<>();

        List<ClientAccommodationInfo> accommodationInfos= accommodationService.getClientAccommodationInfosByClientId(clientId);
        List<ClientConsumeInfo> consumeInfos=consumeService.getClientConsumeInfosByClientId(clientId);

        //添加该用户所有住宿记录
        for (ClientAccommodationInfo accommodationInfo : accommodationInfos) {
            Example example=new Example(HotelRoom.class);
            example.createCriteria().andEqualTo("id",accommodationInfo.getRoomId());
            HotelRoom hotelRoom = hotelRoomMapper.selectOneByExample(example);

            TradeRecord record=new TradeRecord();
            record.setState(accommodationInfo.getState());
            record.setDate(accommodationInfo.getDate());
            record.setName(hotelRoom.getCategory()+":"+accommodationInfo.getDayCount()+"天");
            record.setExpense(accommodationInfo.getDayCount()*hotelRoom.getPrice());
            record.setType("住宿");

            tradeRecords.add(record);
        }

        //添加该用户所有历史消费记录
        for (ClientConsumeInfo consumeInfo : consumeInfos) {
            HotelService hotelService = hotelServiceMapper.selectByPrimaryKey(consumeInfo.getServiceId());
            TradeRecord record=new TradeRecord();
            record.setName(hotelService.getName());
            record.setExpense(hotelService.getPrice()*consumeInfo.getCount());
            record.setDate(consumeInfo.getDate());
            record.setState(consumeInfo.getState());
            record.setType("服务消费");

            tradeRecords.add(record);
        }

        return tradeRecords;
    }
}
