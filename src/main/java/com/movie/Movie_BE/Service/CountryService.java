package com.movie.Movie_BE.Service;


import com.movie.Movie_BE.Model.Country;
import com.movie.Movie_BE.Repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CountryService {
    @Autowired
    private CountryRepository countryRepository;

    public List<Country> getAllCountry (){
        return countryRepository.findAll();
    }

    public Optional<Country> getCountryById (Long id){
        return countryRepository.findById(id);
    }

    public Country createCountry (Country country){
        return countryRepository.save(country);
    }

    public void deleteCountry (Long id){
        countryRepository.deleteById(id);
    }

    public Country updateCountry (Long id, Country countryDetail){
        return countryRepository.findById(id).map(country -> {
            if (countryDetail.getName() != null){
                country.setName(country.getName());
            }
            if (countryDetail.getSlug() != null){
                country.setSlug(countryDetail.getSlug());
            }

            return countryRepository.save(country);
        }).orElseThrow(() -> new RuntimeException("Category not found with: "+id));
    }
}
