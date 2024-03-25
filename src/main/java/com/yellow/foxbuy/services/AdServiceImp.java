package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Ad;
import com.yellow.foxbuy.repositories.AdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdServiceImp implements AdService{

    private final AdRepository adRepository;

@Autowired
    public AdServiceImp(AdRepository adRepository) {
        this.adRepository = adRepository;
    }

    @Override
    public Ad saveAd(Ad ad) {
        return adRepository.save(ad);
    }
}
