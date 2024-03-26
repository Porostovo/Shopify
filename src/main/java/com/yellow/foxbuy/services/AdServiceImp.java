package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Ad;
import com.yellow.foxbuy.models.DTOs.AdResponseDTO;
import com.yellow.foxbuy.repositories.AdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AdServiceImp implements AdService{

    private final AdRepository adRepository;
    private CategoryService categoryService;

    @Autowired
    public AdServiceImp(AdRepository adRepository, CategoryService categoryService) {
        this.adRepository = adRepository;
        this.categoryService = categoryService;
    }


    @Override
    public Ad saveAd(Ad ad) {
        return adRepository.save(ad);
    }

    @Override
    public Optional<Ad> findAdById(Long id) {
        return adRepository.findById(id);
    }

    @Override
   public void deleteAd(Ad ad) {
        adRepository.delete(ad);
    }

    @Override
    public AdResponseDTO findById(Long id) {
        Optional<Ad> ad = adRepository.findById(id);
        AdResponseDTO adDTO = new AdResponseDTO();
        adDTO.setId(ad.get().getId());
        adDTO.setTitle(ad.get().getTitle());
        adDTO.setDescription(ad.get().getDescription());
        adDTO.setPrice(ad.get().getPrice());
        adDTO.setZipcode(ad.get().getZipcode());
        adDTO.setCategoryID(ad.get().getCategory().getId());
        return adDTO;
    }

    @Override
    public boolean existsById(Long id) {
        return adRepository.findAll().stream().anyMatch(ad -> Objects.equals(ad.getId(), id));
    }
}
