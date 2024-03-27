package com.yellow.foxbuy.services;

import com.yellow.foxbuy.models.Ad;
import com.yellow.foxbuy.models.DTOs.AdResponseDTO;
import com.yellow.foxbuy.repositories.AdRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
        List<Ad> adList = adRepository.findAllByCategoryId(id).stream()
                .skip(offset)
                .limit(pageSize)
                .toList();

        for (Ad ad : adList) {
            userAds.add(loadAdResponseDTO(ad));
        }
        return userAds;
    }

    @Override
    public int getTotalPages(List<AdResponseDTO> adResponseDTOList) {
        return (int) Math.ceil((double) adResponseDTOList.size() / 10.0);
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
