package com.example.apartmentsalesmanagementsystem.service;

import com.example.apartmentsalesmanagementsystem.entity.Apartment;
import com.example.apartmentsalesmanagementsystem.entity.ApartmentStatus;
import com.example.apartmentsalesmanagementsystem.entity.User;
import com.example.apartmentsalesmanagementsystem.repository.ApartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ApartmentService {
    
    @Autowired
    private ApartmentRepository apartmentRepository;
    
    @Autowired
    private ImageStorageService imageStorageService;
    
    public Apartment saveApartment(Apartment apartment) {
        return apartmentRepository.save(apartment);
    }
    
    public Apartment updateApartment(Apartment apartment) {
        return apartmentRepository.save(apartment);
    }
    
    public Optional<Apartment> findById(Long id) {
        return apartmentRepository.findById(id);
    }
    
    public List<Apartment> findAllApartments() {
        return apartmentRepository.findAll();
    }
    
    public List<Apartment> findAvailableApartments() {
        return apartmentRepository.findByStatus(ApartmentStatus.AVAILABLE);
    }
    
    public Page<Apartment> findAvailableApartments(Pageable pageable) {
        return apartmentRepository.findAvailableApartments(pageable);
    }
    
    public List<Apartment> findApartmentsByOwner(User owner) {
        return apartmentRepository.findByOwner(owner);
    }
    
    public List<Apartment> findApartmentsByOwnerAndStatus(User owner, ApartmentStatus status) {
        return apartmentRepository.findByOwnerAndStatus(owner, status);
    }
    
    public List<Apartment> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return apartmentRepository.findByPriceRange(minPrice, maxPrice);
    }
    
    public List<Apartment> findByCity(String city) {
        return apartmentRepository.findByCity(city);
    }
    
    public List<Apartment> findByMinBedrooms(Integer minBedrooms) {
        return apartmentRepository.findByMinBedrooms(minBedrooms);
    }
    
    public List<Apartment> searchApartments(String searchTerm) {
        return apartmentRepository.searchApartments(searchTerm);
    }
    
    public void deleteApartment(Long id) {
        apartmentRepository.deleteById(id);
    }
    
    public void updateApartmentStatus(Long id, ApartmentStatus status) {
        Optional<Apartment> apartmentOpt = apartmentRepository.findById(id);
        if (apartmentOpt.isPresent()) {
            Apartment apartment = apartmentOpt.get();
            apartment.setStatus(status);
            apartmentRepository.save(apartment);
        }
    }
    
    public long countApartmentsByStatus(ApartmentStatus status) {
        return apartmentRepository.countByStatus(status);
    }
    
    public long countApartmentsByOwner(User owner) {
        return apartmentRepository.countByOwner(owner);
    }
    
    public long getTotalApartments() {
        return apartmentRepository.count();
    }
    
    public long getAvailableApartmentsCount() {
        return apartmentRepository.countByStatus(ApartmentStatus.AVAILABLE);
    }
    
    public long getSoldApartmentsCount() {
        return apartmentRepository.countByStatus(ApartmentStatus.SOLD);
    }
    
    public long getUnderContractApartmentsCount() {
        return apartmentRepository.countByStatus(ApartmentStatus.UNDER_CONTRACT);
    }
    
    public List<Apartment> getAvailableApartments() {
        return findAvailableApartments();
    }
    
    /**
     * Save apartment with image upload support
     */
    public Apartment saveApartmentWithImage(Apartment apartment, MultipartFile imageFile) throws IOException {
        // Handle image upload if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = imageStorageService.storeImage(imageFile);
            apartment.setImageUrl(imageUrl);
        }
        
        return apartmentRepository.save(apartment);
    }
    
    /**
     * Update apartment with image upload support
     */
    public Apartment updateApartmentWithImage(Apartment apartment, MultipartFile imageFile) throws IOException {
        // Handle image upload if provided
        if (imageFile != null && !imageFile.isEmpty()) {
            // Delete old image if it exists and is an uploaded file
            if (apartment.getImageUrl() != null && apartment.getImageUrl().startsWith("/uploads/")) {
                imageStorageService.deleteImage(apartment.getImageUrl());
            }
            
            // Store new image
            String imageUrl = imageStorageService.storeImage(imageFile);
            apartment.setImageUrl(imageUrl);
        }
        
        return apartmentRepository.save(apartment);
    }
    
    /**
     * Delete apartment and its associated image
     */
    public void deleteApartmentWithImage(Long id) {
        Optional<Apartment> apartmentOpt = apartmentRepository.findById(id);
        if (apartmentOpt.isPresent()) {
            Apartment apartment = apartmentOpt.get();
            
            // Delete associated image if it's an uploaded file
            if (apartment.getImageUrl() != null && apartment.getImageUrl().startsWith("/uploads/")) {
                imageStorageService.deleteImage(apartment.getImageUrl());
            }
            
            apartmentRepository.deleteById(id);
        }
    }
}
