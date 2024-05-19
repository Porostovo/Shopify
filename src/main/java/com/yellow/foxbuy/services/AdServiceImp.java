package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Ad;
import com.yellow.foxbuy.models.DTOs.AdDTO;
import com.yellow.foxbuy.models.DTOs.AdResponseDTO;
import com.yellow.foxbuy.models.User;
import com.yellow.foxbuy.repositories.AdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.*;

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
    public Ad findAdByIdNoOptional(Long id) {
        Optional<Ad> optAd = findAdById(id);
        if (optAd.isPresent()) return optAd.get();
        return null;
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
    public List<Ad> findAllByUsername(String username){
        List<Ad> result = adRepository.findAllByUserUsername(username);
        return result;
    }

    @Override
    public void updateAd(List<Ad> adList, Boolean cond) {
        for (Ad ad:adList){
            ad.setHidden(cond);
            adRepository.save(ad);
        }
    }

    @Override
    public List<Ad> getHiddenAds(User user) {
        return adRepository.findAllByUserAndHiddenIsTrue(user);
    }

    @Override
    public boolean isHidden(Ad ad) {
        return ad.isHidden();
    }

    @Override
    public List<AdResponseDTO> searchAds(String search) {
        String[] searchWords = search.split("\\s+");
        List<Ad> result = new ArrayList<>();
        for (String word : searchWords) {
            result.addAll(adRepository.findAllByTitleOrDescriptionContainingAnyIgnoreCase(word));
        }
        List<Ad> uniqueAds = result.stream().distinct().toList();
        List<AdResponseDTO> foundAds = new ArrayList<>();
        for (Ad ad : uniqueAds) {
            foundAds.add(loadAdResponseDTO(ad));
        }
        return foundAds;
    }

    @Override
    public List<Ad> findAllByUserID(UUID uuid) {
        return null;
    }

    @Override
    public List<AdResponseDTO> findAllByCategoryId(Long id) {
        List<Ad> adList = adRepository.findAllByCategoryIdAndHiddenIsFalse(id);
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

        List<Ad> adList = adRepository.findByCategoryIdAndHiddenIsFalse(id, pageable).getContent();

        System.out.println(adList);
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

    @Override
    public boolean checkIfAdExists(User user, AdDTO adDTO) {

        // Check if a similar advertisement already exists for the user
        Optional<Ad> existingAd = adRepository.findByUserAndTitleAndDescriptionAndPriceAndZipcode(
                user,
                adDTO.getTitle(),
                adDTO.getDescription(),
                adDTO.getPrice(),
                adDTO.getZipcode()
        );
        return existingAd.isPresent();
    }


    private static AdResponseDTO loadAdResponseDTO(Ad ad) {
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


