package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Ad;
import com.yellow.foxbuy.repositories.AdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public
class AdServiceImp implements AdService {

    private final AdRepository adRepository;


    @Autowired
    public AdServiceImp(AdRepository adRepository) {
        this.adRepository = adRepository;

    }

    @Override
    public void saveAd(Ad ad) {
        adRepository.save(ad);
    }

    @Override
    public Optional<Ad> findAdById(Long id) {
        return adRepository.findById(id);
    }

    @Override
    public void deleteAd(Ad ad) {
        adRepository.delete(ad);
    }

}


