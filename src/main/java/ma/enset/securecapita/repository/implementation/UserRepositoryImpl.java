package ma.enset.securecapita.repository.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.enset.securecapita.domain.Role;
import ma.enset.securecapita.domain.User;
import ma.enset.securecapita.domain.UserPrincipal;
import ma.enset.securecapita.exception.ApiException;
import ma.enset.securecapita.repository.RoleRepository;
import ma.enset.securecapita.repository.UserRepository;
import ma.enset.securecapita.rowmapper.UserRowMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.*;

import static java.util.Map.of;
import static java.util.Objects.requireNonNull;
import static ma.enset.securecapita.enumeration.RoleType.ROLE_USER;
import static ma.enset.securecapita.enumeration.VerificationType.ACCOUNT;
import static ma.enset.securecapita.query.UserQuery.*;

@Repository
@RequiredArgsConstructor
@Slf4j

public class UserRepositoryImpl implements UserRepository<User>, UserDetailsService {

    private final NamedParameterJdbcTemplate jdbc;
    private final RoleRepository <Role> roleRepository;
    private final BCryptPasswordEncoder encoder;

    @Override
    public User create(User user) {
        //check the email is unique
        if(getEmailCount(user.getEmail().trim().toLowerCase())>0)
            throw new ApiException("Email already in use. Please use a different email and try again.");
        //save new user
        try{
            KeyHolder holder = new GeneratedKeyHolder();
            SqlParameterSource parameters = getSqlParameterSource(user);
            jdbc.update(INSERT_USER_QUERY,parameters,holder);

            user.setId(requireNonNull(holder.getKey()).longValue());
            //add role to the user
            roleRepository.addRoleToUser(user.getId(),ROLE_USER.name());
            //send verification Url
            String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(),ACCOUNT.getType());
            //Save URL in verification table
            jdbc.update(INSERT_ACCOUNT_VERIFICATION_URL_QUERY,Map.of("userId",user.getId(),"url",verificationUrl));
            //send email to user with verification URL
            //emailService.sendVerificationUrl(user.getFirstName(),user.getEmail(),verificationUrl,ACCOUNT);
            user.setEnabled(false);
            user.setNotLocked(true);
            //Return the newly created user
            return user;
            //if any errors, throw exception with proper message
       } catch(Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An error occur. Please try again." +exception.getMessage());
        }
    }

    @Override
    public Collection list(int page, int pageSize) {
        return List.of();
    }

    @Override
    public User get(Long id) {
        return null;
    }

    @Override
    public User update(User data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }

    private int getEmailCount(String email) {
        return jdbc.queryForObject(COUNT_USER_EMAIL_QUERY, Map.of("email",email), Integer.class);
    }

    private SqlParameterSource getSqlParameterSource(User user) {
        return new MapSqlParameterSource()
                .addValue("first_name",user.getFirstName())
                .addValue("last_name",user.getLastName())
                .addValue("email",user.getEmail())
                .addValue("password",encoder.encode(user.getPassword()));

    }
    private String getVerificationUrl(String key, String type) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/verify/"+ type +"/"+key).toUriString();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = getUserByEmail(email);
        if(user == null){
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");

        }else{
            log.info("User found in the database: {}", email);

            return new UserPrincipal(user,roleRepository.getRoleByUserId(user.getId()).getPermission());
        }

    }
    @Override
    public User getUserByEmail(String email) {
        try{
            User user=jdbc.queryForObject(SELECT_USER_BY_EMAIL_QUERY,Map.of("email",email),new UserRowMapper());
            return user;
        }catch (EmptyResultDataAccessException exception){
            throw new ApiException("No User found by email:" +email);
        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An error occur. Please try again." +exception.getMessage());
        }
    }

}
