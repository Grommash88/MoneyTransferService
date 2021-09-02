package ru.grommash88.app.servise;

import java.util.ArrayList;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.grommash88.app.model.Account;
import ru.grommash88.app.model.User;
import ru.grommash88.app.model.enums.AccountStatus;
import ru.grommash88.app.model.enums.AccountType;
import ru.grommash88.app.model.enums.CurrencyType;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestAccountBuilder extends TestCase {

  private final User user = User.builder()
      .name("Имя")
      .surname("Фамилия")
      .mail("Почта")
      .login("Логин")
      .password("Пароль")
      .userId(1L)
      .accounts(new ArrayList<>())
      .build();

  private final AccountType EXPECTED_ACCOUNT_TYPE = AccountType.DEBIT;
  private final CurrencyType EXPECTED_CURRENCY_TYPE = CurrencyType.RUB;
  private final AccountStatus EXPECTED_ACCOUNT_STATUS = AccountStatus.NORMAL;


  @Test
  public void createAccountTest() {

    Account account = AccountBuilder.createAccount(user);

    Assert.assertEquals(EXPECTED_ACCOUNT_STATUS, account.getAccStatus());
    Assert.assertEquals(EXPECTED_CURRENCY_TYPE, account.getAccCurrency());
    Assert.assertEquals(EXPECTED_ACCOUNT_TYPE, account.getAccountType());
    Assert.assertTrue(account.getAccountNumber().matches("810\\d{7}"));
    Assert.assertEquals(0.0, account.getBalance().doubleValue(), 0.0);
    Assert.assertEquals(user, account.getAccHolder());
    Assert.assertTrue(user.getAccounts().size() > 0);

  }
}
