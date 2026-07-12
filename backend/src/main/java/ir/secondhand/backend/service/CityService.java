package ir.secondhand.backend.service;

import ir.secondhand.backend.dto.request.CityRequest;
import ir.secondhand.backend.dto.response.CityResponse;
import ir.secondhand.backend.entity.City;
import ir.secondhand.backend.exception.DuplicateResourceException;
import ir.secondhand.backend.exception.ResourceNotFoundException;
import ir.secondhand.backend.repository.CityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CityService {

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public List<CityResponse> getAllCities() {
        return cityRepository.findAll().stream()
                .map(CityResponse::fromEntity)
                .toList();
    }

    @Transactional
    public CityResponse createCity(CityRequest request) {
        if (cityRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("این شهر قبلا ثبت شده است.");
        }
        City city = new City(request.getName());
        cityRepository.save(city);
        return CityResponse.fromEntity(city);
    }

    @Transactional
    public void deleteCity(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("این شهر پیدا نشد."));
        cityRepository.delete(city);
    }
}
