package marketplace.services;

import marketplace.models.Role;
import marketplace.models.User;
import marketplace.repositories.RoleRepository;
import marketplace.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service("userService")
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public void saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        userRepository.save(user);

        Role role = new Role(user.getUsername(), "User");
        roleRepository.save(role);
    }

    public Boolean userExists(User user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            return true;
        }
        else {
            return false;
        }
    }
}
