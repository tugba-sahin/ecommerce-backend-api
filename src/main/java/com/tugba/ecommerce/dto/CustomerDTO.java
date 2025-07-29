package com.tugba.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDTO {
    private Long id;
    @NotBlank(message = "First name cannot be blank")
    @Size(max = 100, message = "First name cannot exceed 100 characters")
    private String firstName;
    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 100, message = "Last name cannot exceed 100 characters")
    private String lastName;
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;
    // Adresler, müşteri oluşturulduğunda zorunlu değil, sonradan eklenebilir.
    // DTO'da listeyi tutmak isteyebilirsiniz, ama şu anki akışta belki direk adres ID'leri yeterlidir.
    // List<AddressDTO> addresses; // Eğer customer ile birlikte adresleri de döndürmek isterseniz ekleyin
    // CartDTO cart; // Eğer customer ile birlikte sepeti de döndürmek isterseniz ekleyin
}