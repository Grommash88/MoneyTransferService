package ru.grommash88.app.servise;

import java.util.List;
import java.util.Optional;
import ru.grommash88.app.model.Account;
import ru.grommash88.app.model.User;
import ru.grommash88.app.model.enums.ResultType;

public interface AccountsManagerService {

  Account createAccount(User user);

  Optional getAccount(Long id);

  Optional getAccount(String accNum);

  List<Account> getAccounts();

  ResultType replenishment(String accNum, String amount);

  ResultType withdrawal(String accNum, String amount);

  ResultType transfer(String from, String to, String amount);

}
