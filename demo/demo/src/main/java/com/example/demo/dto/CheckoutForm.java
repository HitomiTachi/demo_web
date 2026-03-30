package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CheckoutForm {

    @NotBlank(message = "Vui lòng nhập họ tên người nhận")
    @Size(max = 120)
    private String recipientName;

    @NotBlank(message = "Vui lòng nhập số điện thoại")
    @Size(max = 20)
    private String phone;

    @NotBlank(message = "Vui lòng nhập địa chỉ giao hàng")
    @Size(max = 500)
    private String address;
}
