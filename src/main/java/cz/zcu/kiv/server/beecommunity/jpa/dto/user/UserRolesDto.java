package cz.zcu.kiv.server.beecommunity.jpa.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto for admin page where admin can grant and revoke admin role and ban users
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRolesDto {
    Long userId;
    String fullName;
    String email;
    boolean isUser;
    boolean isAdmin;
}
