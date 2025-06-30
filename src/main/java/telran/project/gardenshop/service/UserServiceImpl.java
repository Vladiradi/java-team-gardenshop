package telran.project.gardenshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import telran.project.gardenshop.dto.UserRequestDto;
import telran.project.gardenshop.dto.UserResponseDto;
import telran.project.gardenshop.entity.User;
import telran.project.gardenshop.enums.Role;
import telran.project.gardenshop.exception.UserNotFoundException;
import telran.project.gardenshop.mapper.UserMapper;
import telran.project.gardenshop.repository.UserRepository;
import telran.project.gardenshop.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    public class UserServiceImpl implements UserService {
        private final UserRepository repository;
        private final PasswordEncoder passwordEncoder;
        private final UserMapper userMapper;

        @Override
        public UserResponseDto create(UserRequestDto dto) {
            User user = userMapper.toEntity(dto);
            user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
            user.setRole(dto.getRole() != null ? dto.getRole() : Role.USER);
            return userMapper.toDto(repository.save(user));
        }

        @Override
        public List<UserResponseDto> getAll() {
            return repository.findAll()
                    .stream()
                    .map(userMapper::toDto)
                    .collect(Collectors.toList());
        }

        @Override
        public UserResponseDto getById(Long id) {
            User user = repository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(id));
            return userMapper.toDto(user);
        }

        @Override
        public void delete(Long id) {
            if (!repository.existsById(id)) {
                throw new UserNotFoundException(id);
            }
            repository.deleteById(id);
        }
    }
    // Итого:
//Конвертация вся в UserMapper, нет нужды в converter.
//
//passwordEncoder.encode(...) — остался в service, т.к. зависит от Spring Security.
//
//@Mapper(componentModel = "spring") делает маппер доступным как бин.