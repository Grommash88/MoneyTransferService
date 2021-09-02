package ru.grommash88.app.servise;

import ru.grommash88.app.dto.UserRequestDto;
import ru.grommash88.app.model.User;

public class ConverterUserRequestDtoToUser {

  public static User convert(UserRequestDto userRequestDto) {
    return User.builder()
        .name(userRequestDto.getName())
        .surname(userRequestDto.getSurname())
        .mail(userRequestDto.getMail())
        .login(userRequestDto.getLogin())
        .password(userRequestDto.getPassword())
        .build();
  }
}
