package ru.grommash88.app.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.grommash88.app.model.Account;
import ru.grommash88.app.model.User;
import ru.grommash88.app.model.enums.AccountType;
import ru.grommash88.app.model.enums.CurrencyType;
import ru.grommash88.app.model.enums.ResultType;
import ru.grommash88.app.servise.AccountsManagerService;
import ru.grommash88.app.servise.UserManagerService;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/accounts/")
@Api(value = "Accounts Controller", description = "Api для работы со счетами")
public class AccountController {

  private final AccountsManagerService accountsManagerService;

  private final UserManagerService userManagerService;

  @PostMapping
  @ApiOperation(value = "Создание счета", response = ResponseEntity.class)
  @ApiResponses(value = {
      @ApiResponse(code = 201, message = "Счет успешно создан и добавлен в базу данных."),
      @ApiResponse(code = 400, message =
          "Вы не можете открыть несколько дебетовых счетов в одинаковой валюте")
  })
  public ResponseEntity<String> createAcc(
      @ApiParam(value = "id клиента открывающего счет.", required = true, example = "15")
      @RequestBody long id) {
    User user = userManagerService.getUser(id);
    //ResponseEntity responseEntity = new ResponseEntity(HttpStatus.OK);

    switch (user.getAccounts().size()) {
      case 0:
        String accNum = accountsManagerService.createAccount(user).getAccountNumber();
        return ResponseEntity.status(HttpStatus.CREATED)
            .body("Счет №" + accNum + " успешно создан и добавлен в базу данных.");
      default:
        for (Account acc : user.getAccounts()) {

          if (acc.getAccCurrency().equals(CurrencyType.RUB) && acc.getAccountType()
              .equals(AccountType.DEBIT)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Вы не можете открыть несколько дебетовых счетов в одинаковой валюте");
          } else {
            accNum = accountsManagerService.createAccount(user).getAccountNumber();
            return ResponseEntity.status(HttpStatus.CREATED)
                .body("Счет №" + accNum + " успешно создан и добавлен в базу данных.");
          }
        }
    }
    return null;
  }

  @GetMapping
  @ApiOperation(value = "Получение списка счетов", response = ResponseEntity.class)
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Список счетов успешно получен."),
      @ApiResponse(code = 400, message = "Нет открытых счетов.")
  })
  public ResponseEntity getAccounts() {
    if (accountsManagerService.getAccounts().size() > 0) {
      return new ResponseEntity(accountsManagerService.getAccounts(), HttpStatus.OK);
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Нет созданных счетов");
  }

  @GetMapping(value = "{id}")
  @ApiOperation(value = "Получение счета по id", response = ResponseEntity.class)
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Счет успешно получен."),
      @ApiResponse(code = 404, message = "Счет с таким id не существует.")
  })
  public ResponseEntity getAccount(
      @ApiParam(value = "id получаемого счета.", required = true, example = "100")
      @PathVariable Long id) {
    if (accountsManagerService.getAccount(id).isPresent()) {
      return new ResponseEntity(accountsManagerService.getAccount(id).get(), HttpStatus.OK);
    }
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Счета с таким id не существует.");
  }

  @PatchMapping(value = "{from}/transfer/{to}/{amount}")
  @ApiOperation(value = "Перевод средств со счета на другой счет.", response = ResponseEntity.class)
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Перевод успешно выполнен."),
      @ApiResponse(code = 404, message =
          "Счет отправителя и/или счет получателя не существует, проверьте корректность реквизитов"),
      @ApiResponse(code = 405, message = "Статус счета отличен от NORMAL, операция не возможна."),
      @ApiResponse(code = 400, message = "Недостаточно средств для выполнения перевода.")
  })
  public ResponseEntity transfer(
      @ApiParam(value = "Номер счета источника перевода.", required = true, example = "8100000100")
      @PathVariable String from,
      @ApiParam(value = "Номер счета получателя.", required = true, example = "8100000111")
      @PathVariable String to,
      @ApiParam(value = "Сумма перевода.", required = true, example = "500.00")
      @PathVariable String amount) {
    ResultType result = accountsManagerService.transfer(from, to, amount);

    switch (result) {
      case ACCOUNT_DOES_NOT_EXIST:
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                "Счет отправителя и/или счет получателя не существует, "
                    + "проверьте корректность реквизитов");

      case INSUFFICIENT_FUNDS:
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body("Недостаточно средств для выполнения перевода.");

      case THE_STATUS_OF_THE_ACCOUNT_IS_NOT_NORMAL:
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
            .body("Статус счета отличен от NORMAL, операция не возможна.");

      default:
        return ResponseEntity.status(HttpStatus.OK).body("Перевод успешно выполнен.");

    }
  }

  @PutMapping("{id}/add/{amount}")
  @ApiOperation(value = "Пополнение счета, по его номеру.", response = ResponseEntity.class)
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Счет успешно пополнен."),
      @ApiResponse(code = 404, message = "Счет с таким id не существует.")
  })
  public ResponseEntity replenishment(
      @ApiParam(value = "Номер пополняемого счета.", required = true, example = "8100000100")
      @PathVariable String id,
      @ApiParam(value = "Сумма пополнения.", required = true, example = "1000.00")
      @PathVariable String amount) {
    ResultType result = accountsManagerService.replenishment(id, amount);

    if (result == ResultType.ACCOUNT_DOES_NOT_EXIST) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body("Счет с номером " + id + " не существует");
    }
    return ResponseEntity.status(HttpStatus.OK)
        .body("Счет №" + id + ".\nУспешно пополнен на " + amount + " rub.");
  }

  @PutMapping(value = "{id}/withdrawal/{amount}")
  @ApiOperation(value = "Снятие наличных со счета, по его номеру.", response = ResponseEntity.class)
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Наличные успешно сняты."),
      @ApiResponse(code = 404, message = "Счет с таким id не существует."),
      @ApiResponse(code = 400, message = "Недостаточно средств."),
      @ApiResponse(code = 405, message = "Статус счета отличен от NORMAL, операция не возможна.")
  })
  public ResponseEntity withdrawal(
      @ApiParam(value = "Номер счета с которого осуществляется снятие наличных.", required = true,
          example = "8100000100")
      @PathVariable String id,
      @ApiParam(value = "Сумма снятия.", required = true, example = "1000.00")
      @PathVariable String amount) {
    ResultType result = accountsManagerService.withdrawal(id, amount);

    switch (result) {
      case INSUFFICIENT_FUNDS:
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Недостаточно средств.");

      case ACCOUNT_DOES_NOT_EXIST:
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body("Счет с таким номером не существует.");

      case THE_STATUS_OF_THE_ACCOUNT_IS_NOT_NORMAL:
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
            .body("Статус счета отличен от NORMAL, операция не возможна.");

      default:
        Account account = (Account) accountsManagerService.getAccount(id).get();
        return ResponseEntity.status(HttpStatus.OK)
            .body(amount + " rub сняты со счета № " + id + "\nБаланс: " + account.getBalance());
    }
  }
}
