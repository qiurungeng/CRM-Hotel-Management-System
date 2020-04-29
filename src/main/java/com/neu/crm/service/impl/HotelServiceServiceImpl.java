package com.neu.crm.service.impl;

import com.neu.crm.bean.HotelService;
import com.neu.crm.mapper.HotelServiceMapper;
import com.neu.crm.service.HotelServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelServiceServiceImpl implements HotelServiceService {

    @Autowired
    HotelServiceMapper hotelServiceMapper;

    @Override
    public HotelService getHotelServiceById(Integer serviceId) {
        return hotelServiceMapper.selectByPrimaryKey(serviceId);
    }

    @Override
    public List<HotelService> getHotelServices() {
        return hotelServiceMapper.selectAll();
    }
}
