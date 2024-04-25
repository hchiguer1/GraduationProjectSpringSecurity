package ma.enset.securecapita.repository;

import ma.enset.securecapita.domain.Role;
import ma.enset.securecapita.domain.User;

import java.util.Collection;

public interface RoleRepository <T extends Role>{
    /* Basic Crud Operations*/
    T create (T data);
    Collection<T> list(int page, int pageSize);
    T get(Long id);
    T update(T data);
    Boolean delete (Long id);
    /* More Complex Crud Operations*/
    void addRoleToUser(Long userId,String roleName);
    Role getRoleByUserId(Long userId);
    Role getRoleByUserEmail( String email);
    void updateUserRole(Long userId,String roleName);

}
