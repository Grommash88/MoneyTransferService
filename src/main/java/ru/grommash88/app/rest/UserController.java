package ru.grommash88.app.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.grommash88.app.dto.UserRequestDto;
import ru.grommash88.app.servise.UserManagerService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/users")
public class UserController {

  private final UserManagerService userManagerService;

  @PostMapping
  public void createUser(@RequestBody UserRequestDto userRequestDto) {
    userManagerService.createUser(userRequestDto);
  }
}
