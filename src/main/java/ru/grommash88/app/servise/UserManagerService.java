package ru.grommash88.app.servise;

import ru.grommash88.app.dto.UserRequestDto;
import ru.grommash88.app.model.User;

public interface UserManagerService {

  void createUser(UserRequestDto userRequestDto);

  User getUser(Long id);
}
