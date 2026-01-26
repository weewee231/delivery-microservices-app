package dev.weewee.api.http.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAddressRequestDto {
    
    @NotBlank(message = "Street is required")
    @Size(max = 255)
    private String street;
    
    @NotBlank(message = "City is required")
    @Size(max = 100)
    private String city;
    
    @Size(max = 20)
    private String postalCode;
    
    @Size(max = 100)
    private String country;
    
    @Size(max = 50)
    private String apartment;
    
    @Size(max = 50)
    private String entrance;
    
    @Size(max = 10)
    private String floor;
    
    @Size(max = 500)
    private String comment;
    
    private boolean isDefault;
}

