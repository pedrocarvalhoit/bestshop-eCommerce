package com.bestshop.admin.user;

import com.bestshop.admin.FileUploadUtil;
import com.bestshop.common.entity.Role;
import com.bestshop.common.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class UserService {
    public static final int USERS_PER_PAGE = 4;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRespository roleRespository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> listAll(){
        return (List<User>) userRepository.findAll(Sort.by("firstName").ascending());
    }

    public Page<User> listByPage(int pageNum, String sortField, String sortDir, String keyword){
        Sort sort = Sort.by(sortField);
        sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
        Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);

        if(keyword != null){
            return userRepository.findAll(keyword, pageable);
        }

        return userRepository.findAll(pageable);
    }

    public List<Role> listRoles(){
        return (List<Role>) roleRespository.findAll();
    }

    public User save(User user) {
        boolean isUpdatingUser = (user.getId() != null);

        if (isUpdatingUser) {
            User existingUser = userRepository.findById(user.getId()).get();

            if (user.getPassword().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            } else {
                encodePassword(user);
            }

        } else {
            encodePassword(user);
        }

        return userRepository.save(user);
    }

    public void deleteById(Integer id) throws UserNotFoundException {
        Long countById = userRepository.countById(id);
        if (countById==null || countById == 0){
            throw new UserNotFoundException("Could not find any user with id " + id);
        }

        userRepository.deleteById(id);
    }

    private void encodePassword(User user){
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
    }

    public boolean isEmailUnique(Integer id, String email) {
        User userByEmail = userRepository.getUserByEmail(email);

        if (userByEmail == null) return true;

        boolean isCreatingNew = (id == null);

        if (isCreatingNew) {
            if (userByEmail != null) return false;
        } else {
            if (userByEmail.getId() != id) {
                return false;
            }
        }

        return true;
    }

    public User get(Integer id) throws UserNotFoundException {
        try{
            return userRepository.findById(id).get();
        }catch (NoSuchElementException ex){
            throw new UserNotFoundException("Could not find any user with id " + id);
        }
    }

    public void updateEnabledStatu(Integer id, boolean enabled){
        userRepository.updateEnabledStatus(id, enabled);
    }

    public User findByUsername(String email){
        return userRepository.getUserByEmail(email);
    }


    public void updateDetails(User loggedUser, User userData) {
        loggedUser.setFirstName(userData.getFirstName());
        loggedUser.setLastName(userData.getLastName());
        if (userData.getPassword() == null || userData.getPassword().isEmpty()) {
            userData.setPassword(loggedUser.getPassword());
        }else {
            loggedUser.setPassword(userData.getPassword());
            encodePassword(loggedUser);
        }
        if (userData.getPhotos() == null || userData.getPhotos().isEmpty()) {
            userData.setPhotos(loggedUser.getPhotos());
        }else {
            loggedUser.setPhotos(userData.getPhotos());
        }

    }

    public void savePhoto(User user, MultipartFile multipartFile) throws IOException {
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        user.setPhotos(fileName);

        String upoadDir = "user-photos/" + user.getId();
        FileUploadUtil.cleanDir(upoadDir);
        FileUploadUtil.saveFile(upoadDir, fileName, multipartFile);
    }
}
