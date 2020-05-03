package com.neu.crm.service.impl;

import com.neu.crm.bean.ClientAccommodationInfo;
import com.neu.crm.bean.HotelRoom;
import com.neu.crm.bean.StatisticsInfo;
import com.neu.crm.mapper.ClientAccommodationInfoMapper;
import com.neu.crm.mapper.HotelRoomMapper;
import com.neu.crm.service.ClientAccommodationService;
import com.neu.crm.service.StatisticsInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class ClientAccommodationServiceImpl implements ClientAccommodationService {

    @Autowired
    ClientAccommodationInfoMapper clientAccommodationInfoMapper;
    @Autowired
    StatisticsInfoService statisticsInfoService;
    @Autowired
    HotelRoomMapper hotelRoomMapper;

    public List<ClientAccommodationInfo> getClientAccommodationInfos() {
        return clientAccommodationInfoMapper.selectAll();
    }

    @Override
    public void addClientAccommodation(ClientAccommodationInfo clientAccommodationInfo) {
        if (clientAccommodationInfo.getState()){
            //若该记录已付款，将此次收入累加到酒店历史累计收入
            HotelRoom hotelRoom = hotelRoomMapper.selectByPrimaryKey(clientAccommodationInfo.getRoomId());
            statisticsInfoService.addClientAccommodationIncome(hotelRoom.getPrice()*clientAccommodationInfo.getDayCount());
        }
        clientAccommodationInfoMapper.insertSelective(clientAccommodationInfo);
    }

    @Override
    public void updateClientAccommodationByClientId(ClientAccommodationInfo clientAccommodationInfo) {
        //更新该笔住宿订单收入对于酒店累计收益的影响
        ClientAccommodationInfo db = clientAccommodationInfoMapper.selectByPrimaryKey(clientAccommodationInfo);
        if (!db.getRoomId().equals(clientAccommodationInfo.getRoomId())||
                !db.getDayCount().equals(clientAccommodationInfo.getDayCount())||
                !db.getState().equals(clientAccommodationInfo.getState())){

            HotelRoom dbRoom = hotelRoomMapper.selectByPrimaryKey(db.getRoomId());
            HotelRoom newRoom = hotelRoomMapper.selectByPrimaryKey(clientAccommodationInfo.getRoomId());
            double dbRoomIncome=db.getState() ? dbRoom.getPrice()*db.getDayCount() : 0.0;
            double newRoomIncome=clientAccommodationInfo.getState() ? newRoom.getPrice()*clientAccommodationInfo.getDayCount() : 0.0;

            statisticsInfoService.addClientAccommodationIncome(newRoomIncome-dbRoomIncome);
        }
        //更新该记录
        clientAccommodationInfoMapper.updateByPrimaryKeySelective(clientAccommodationInfo);
    }

    @Override
    public List<ClientAccommodationInfo> getClientAccommodationInfosByClientId(int clientId) {
        Example example=new Example(ClientAccommodationInfo.class);
        example.createCriteria().andEqualTo("clientId",clientId);
        return clientAccommodationInfoMapper.selectByExample(example);
    }

    @Override
    public void deleteClientAccommodationInfoById(Integer id) {
        if (id!=null){
            ClientAccommodationInfo query = clientAccommodationInfoMapper.selectByPrimaryKey(id);
            if (query.getState()){
                HotelRoom hotelRoom = hotelRoomMapper.selectByPrimaryKey(query.getRoomId());
                statisticsInfoService.addClientAccommodationIncome(-(hotelRoom.getPrice()*query.getDayCount()));
            }
            clientAccommodationInfoMapper.deleteByPrimaryKey(id);
        }
    }

    @Override
    public void deleteClientAccommodationInfoByClientId(Integer clientId) {
        if (clientId!=null){
            ClientAccommodationInfo clientAccommodationInfo=new ClientAccommodationInfo();
            clientAccommodationInfo.setClientId(clientId);
            //删除所有与该用户相关的历史账单前，进行收入冲正
            List<ClientAccommodationInfo> records = clientAccommodationInfoMapper.select(clientAccommodationInfo);
            for (ClientAccommodationInfo record : records) {
                if (record.getState()){
                    HotelRoom hotelRoom = hotelRoomMapper.selectByPrimaryKey(record.getRoomId());
                    statisticsInfoService.addClientAccommodationIncome(-(hotelRoom.getPrice()*record.getDayCount()));
                }
            }
            //删除所有与该用户相关的历史账单
            clientAccommodationInfoMapper.delete(clientAccommodationInfo);
        }
    }

}
