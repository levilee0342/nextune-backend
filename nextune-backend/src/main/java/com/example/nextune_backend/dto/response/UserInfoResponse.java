package com.example.nextune_backend.dto.response;
import com.example.nextune_backend.entity.enums.RoleName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoResponse {
    private String userId;
    private String name;
    private String avatar;
    private String email;
    private RoleName role;
}
