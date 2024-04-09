package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Ad;
import com.yellow.foxbuy.models.DTOs.AdResponseDTO;
import com.yellow.foxbuy.repositories.AdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class AdServiceImp implements AdService {

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
        return adRepository.findById(id).isPresent();
    }

    @Override
    public List<AdResponseDTO> findAllByUser(String username) {
        List<Ad> adList = adRepository.findAllByUserUsername(username);
        List<AdResponseDTO> userAds = new ArrayList<>();
        for (Ad ad : adList) {
            userAds.add(loadAdResponseDTO(ad));
        }
        return userAds;
    }

    @Override
    public List<AdResponseDTO> findAllByCategoryId(Long id) {
        List<Ad> adList = adRepository.findAllByCategoryId(id);
        List<AdResponseDTO> userAds = new ArrayList<>();
        for (Ad ad : adList) {
            userAds.add(loadAdResponseDTO(ad));
        }
        return userAds;
    }

    @Override
    public List<AdResponseDTO> listAdsByPageAndCategory(Integer page, Long id) {
        int pageSize = 10;
        int offset = (page - 1) * pageSize;
        List<AdResponseDTO> userAds = new ArrayList<>();
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        List<Ad> adList = adRepository.findByCategoryId(id, pageable).getContent();

        for (Ad ad : adList) {
            userAds.add(loadAdResponseDTO(ad));
        }
        return userAds;
    }

    @Override
    public int getTotalPages(Long categoryId) {
        long totalAds = adRepository.countByCategoryId(categoryId);
        return (int) Math.ceil((double) totalAds / 10.0);
    }

    private static AdResponseDTO loadAdResponseDTO (Ad ad){
        AdResponseDTO adDTO = new AdResponseDTO();
        adDTO.setId(ad.getId());
        adDTO.setTitle(ad.getTitle());
        adDTO.setDescription(ad.getDescription());
        adDTO.setPrice(ad.getPrice());
        adDTO.setZipcode(ad.getZipcode());
        adDTO.setCategoryID(ad.getCategory().getId());
        return adDTO;
    }
}


