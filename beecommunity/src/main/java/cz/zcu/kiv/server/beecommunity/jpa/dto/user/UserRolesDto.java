package cz.zcu.kiv.server.beecommunity.jpa.dto.user;

import lombok.Builder;
import lombok.Data;

/**
 * Dto for admin page where admin can grant and revoke admin role and ban users
 */
@Data
@Builder
public class UserRolesDto {
    Long userId;
    String fullName;
    String email;
    boolean isUser;
    boolean isAdmin;
}
