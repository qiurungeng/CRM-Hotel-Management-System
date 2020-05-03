package com.neu.crm.service.impl;

import com.neu.crm.bean.ClientAccommodationInfo;
import com.neu.crm.bean.ClientConsumeInfo;
import com.neu.crm.bean.HotelRoom;
import com.neu.crm.bean.HotelService;
import com.neu.crm.mapper.ClientConsumeInfoMapper;
import com.neu.crm.service.ClientConsumeService;
import com.neu.crm.service.HotelServiceService;
import com.neu.crm.service.StatisticsInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class ClientConsumeServiceImpl implements ClientConsumeService {

    @Autowired
    ClientConsumeInfoMapper clientConsumeInfoMapper;
    @Autowired
    HotelServiceService hotelServiceService;
    @Autowired
    StatisticsInfoService statisticsInfoService;

    @Override
    public List<ClientConsumeInfo> getClientAccommodationInfos() {
        return clientConsumeInfoMapper.selectAll();
    }

    @Override
    public void addClientConsumeInfo(ClientConsumeInfo clientConsumeInfo) {
        if (clientConsumeInfo.getState()){
            //确认为已付款才能累加到酒店累计收入中
            HotelService hotelService = hotelServiceService.getHotelServiceById(clientConsumeInfo.getServiceId());
            statisticsInfoService.addClientConsumeIncome(hotelService.getPrice()*clientConsumeInfo.getCount());
        }
        clientConsumeInfoMapper.insertSelective(clientConsumeInfo);
    }

    @Override
    public void modifyClientConsumeInfo(ClientConsumeInfo clientConsumeInfo) {
        //更新该笔订单收入对于酒店累计收益的影响
        ClientConsumeInfo db = clientConsumeInfoMapper.selectByPrimaryKey(clientConsumeInfo);
        if (!db.getServiceId().equals(clientConsumeInfo.getServiceId())||
                !db.getCount().equals(clientConsumeInfo.getCount())||
                !db.getState().equals(clientConsumeInfo.getState())){

            HotelService dbService = hotelServiceService.getHotelServiceById(db.getServiceId());
            HotelService newService = hotelServiceService.getHotelServiceById(clientConsumeInfo.getServiceId());
            double dbServiceIncome=db.getState()?dbService.getPrice()*db.getCount():0;
            double newServiceIncome=clientConsumeInfo.getState()?newService.getPrice()*clientConsumeInfo.getCount():0;

            statisticsInfoService.addClientConsumeIncome(newServiceIncome-dbServiceIncome);
        }
        //更新该订单
        clientConsumeInfoMapper.updateByPrimaryKeySelective(clientConsumeInfo);
    }

    @Override
    public List<ClientConsumeInfo> getClientConsumeInfosByClientId(int clientId) {
        ClientConsumeInfo clientConsumeInfo=new ClientConsumeInfo();
        clientConsumeInfo.setClientId(clientId);
        return clientConsumeInfoMapper.select(clientConsumeInfo);
    }

    @Override
    public void deleteClientConsumeInfoByClientId(Integer clientId) {
        if (clientId!=null){
            ClientConsumeInfo clientConsumeInfo=new ClientConsumeInfo();
            clientConsumeInfo.setClientId(clientId);
            //删除所有与该用户相关的历史账单前，进行收入冲正
            List<ClientConsumeInfo> records = clientConsumeInfoMapper.select(clientConsumeInfo);
            for (ClientConsumeInfo record : records) {
                if (record.getState()){
                    HotelService hotelService=hotelServiceService.getHotelServiceById(record.getServiceId());
                    statisticsInfoService.addClientConsumeIncome( - (hotelService.getPrice()*clientConsumeInfo.getCount()));
                }
            }
            //删除所有与该用户相关的历史账单
            clientConsumeInfoMapper.delete(clientConsumeInfo);
        }
    }

    @Override
    public void deleteClientConsumeInfoById(Integer id) {
        if (id!=null){
            ClientConsumeInfo clientConsumeInfo = clientConsumeInfoMapper.selectByPrimaryKey(id);
            if (clientConsumeInfo.getState()){
                HotelService hotelService = hotelServiceService.getHotelServiceById(clientConsumeInfo.getServiceId());
                statisticsInfoService.addClientConsumeIncome( - (hotelService.getPrice()*clientConsumeInfo.getCount()));
            }
            clientConsumeInfoMapper.deleteByPrimaryKey(id);
        }
    }
}
