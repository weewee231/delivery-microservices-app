package dev.weewee.api.http.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    private Long id;
    private String street;
    private String city;
    private String postalCode;
    private String country;
    private String apartment;
    private String entrance;
    private String floor;
    private String comment;
    private boolean isDefault;
}

