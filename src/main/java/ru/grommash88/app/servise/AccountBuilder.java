package ru.grommash88.app.servise;

import java.math.BigDecimal;
import java.math.BigInteger;
import ru.grommash88.app.model.Account;
import ru.grommash88.app.model.User;
import ru.grommash88.app.model.enums.AccountStatus;
import ru.grommash88.app.model.enums.AccountType;
import ru.grommash88.app.model.enums.CurrencyType;

public class AccountBuilder {

  static final BigInteger rubAccNumber = new BigInteger("8100000000");
  static long accCount;

  public static Account createAccount(User user) {
    Account createdAcc = Account.builder()
        .accStatus(AccountStatus.NORMAL)
        .accountType(AccountType.DEBIT)
        .accCurrency(CurrencyType.RUB)
        .accountNumber(String.valueOf(rubAccNumber.add(
            new BigInteger(Long.toString(++accCount)))))
        .balance(new BigDecimal("0.0"))
        .accHolder(user)
        .build();

    user.getAccounts().add(createdAcc);

    return createdAcc;
  }
}
