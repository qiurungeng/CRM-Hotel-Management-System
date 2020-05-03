package com.neu.crm.service.impl;

import com.neu.crm.bean.HotelRoom;
import com.neu.crm.mapper.HotelRoomMapper;
import com.neu.crm.service.HotelRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelRoomServiceImpl implements HotelRoomService {
    @Autowired
    HotelRoomMapper hotelRoomMapper;
    @Override
    public List<HotelRoom> getAllHotelRoom() {
        return hotelRoomMapper.selectAll();
    }
}
