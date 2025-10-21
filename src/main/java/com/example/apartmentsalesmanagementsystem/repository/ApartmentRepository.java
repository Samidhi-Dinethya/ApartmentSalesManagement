package com.example.apartmentsalesmanagementsystem.repository;

import com.example.apartmentsalesmanagementsystem.entity.Apartment;
import com.example.apartmentsalesmanagementsystem.entity.ApartmentStatus;
import com.example.apartmentsalesmanagementsystem.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ApartmentRepository extends JpaRepository<Apartment, Long> {
    
    List<Apartment> findByStatus(ApartmentStatus status);
    
    List<Apartment> findByOwner(User owner);
    
    List<Apartment> findByOwnerAndStatus(User owner, ApartmentStatus status);
    
    @Query("SELECT a FROM Apartment a WHERE a.price BETWEEN :minPrice AND :maxPrice")
    List<Apartment> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    
    @Query("SELECT a FROM Apartment a WHERE a.city = :city")
    List<Apartment> findByCity(@Param("city") String city);
    
    @Query("SELECT a FROM Apartment a WHERE a.bedrooms >= :minBedrooms")
    List<Apartment> findByMinBedrooms(@Param("minBedrooms") Integer minBedrooms);
    
    @Query("SELECT a FROM Apartment a WHERE a.title LIKE %:searchTerm% OR a.description LIKE %:searchTerm% OR a.address LIKE %:searchTerm% OR a.city LIKE %:searchTerm%")
    List<Apartment> searchApartments(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT a FROM Apartment a WHERE a.status = 'AVAILABLE' ORDER BY a.createdAt DESC")
    Page<Apartment> findAvailableApartments(Pageable pageable);
    
    @Query("SELECT COUNT(a) FROM Apartment a WHERE a.status = :status")
    long countByStatus(@Param("status") ApartmentStatus status);
    
    @Query("SELECT COUNT(a) FROM Apartment a WHERE a.owner = :owner")
    long countByOwner(@Param("owner") User owner);
}
