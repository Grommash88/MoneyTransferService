package ru.grommash88.app.servise.impl;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.grommash88.app.dto.UserRequestDto;
import ru.grommash88.app.model.User;
import ru.grommash88.app.repo.UsersRepo;
import ru.grommash88.app.servise.ConverterUserRequestDtoToUser;
import ru.grommash88.app.servise.UserManagerService;

@Service
@RequiredArgsConstructor
public class UserManagerServiceImpl implements UserManagerService {

  private final UsersRepo usersRepo;

  @Override
  public void createUser(UserRequestDto userRequestDto) {
    usersRepo.save(ConverterUserRequestDtoToUser.convert(userRequestDto));
  }

  @Override
  public User getUser(Long id) {
    Optional<User> optionalUser = usersRepo.findById(id);
    return optionalUser.get();
  }
}
