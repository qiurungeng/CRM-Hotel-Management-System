package com.neu.crm.service;

import com.neu.crm.bean.HotelService;

import java.util.List;

public interface HotelServiceService {
    HotelService getHotelServiceById(Integer serviceId);

    List<HotelService> getHotelServices();
}
