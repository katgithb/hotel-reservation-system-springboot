package com.zenhotel.hrs_api.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "booking_references")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingReference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Reference No is required")
    @Column(unique = true, nullable = false)
    private String referenceNo;

    public BookingReference(String referenceNo) {
        this.referenceNo = referenceNo;
    }
    
}
