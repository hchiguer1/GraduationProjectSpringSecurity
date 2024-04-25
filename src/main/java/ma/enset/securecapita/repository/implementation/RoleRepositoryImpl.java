package ma.enset.securecapita.repository.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.securecapita.domain.Role;
import ma.enset.securecapita.exception.ApiException;
import ma.enset.securecapita.repository.RoleRepository;
import ma.enset.securecapita.rowmapper.RoleRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static ma.enset.securecapita.enumeration.RoleType.ROLE_USER;
import static ma.enset.securecapita.query.RoleQuery.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RoleRepositoryImpl implements RoleRepository<Role> {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Role create(Role data) {
        return null;
    }

    @Override
    public Collection<Role> list(int page, int pageSize) {
        return List.of();
    }

    @Override
    public Role get(Long id) {
        return null;
    }

    @Override
    public Role update(Role data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }

    @Override
    public void addRoleToUser(Long user_id, String roleName) {
        log.info("Adding role {} to user {}", roleName, user_id);
        try{
            Role role = jdbc.queryForObject(SELECT_ROLE_BY_NAME_QUERY,Map.of("name", roleName),new RoleRowMapper());
            Long role_id=requireNonNull(role).getId();
            jdbc.update(INSERT_ROLE_TO_USER_QUERY,Map.of("user_id", user_id, "role_id", role_id));
        } catch(EmptyResultDataAccessException exception){
            throw new ApiException("No role found by name: "+ROLE_USER.name());
        } catch(Exception exception){
            throw new ApiException("An error occurred. Please try again."+exception.getMessage());
        }

    }

    @Override
    public Role getRoleByUserId(Long userId) {
        log.info("Getting Role for user id {}", userId);
        System.out.println("***************************");
        try {
            System.out.println("-------->");
            Role role=jdbc.queryForObject(SELECT_ROLE_BY_ID_QUERY, Map.of("id", userId), new RoleRowMapper());
            log.info("Role {} for user id {} permission {}", requireNonNull(role).getName(),userId, role.getPermission() );
           return requireNonNull(role);
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("No role found by id: " + userId);
        } catch (Exception exception) {
            throw new ApiException("An error occurred. Please try again." + exception.getMessage());
        }
    }
    @Override
    public Role getRoleByUserEmail(String email) {
        return null;
    }

    @Override
    public void updateUserRole(Long userId, String roleName) {

    }
}
