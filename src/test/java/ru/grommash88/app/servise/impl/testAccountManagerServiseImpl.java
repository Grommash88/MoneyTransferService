package ru.grommash88.app.servise.impl;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.grommash88.app.model.Account;
import ru.grommash88.app.model.User;
import ru.grommash88.app.model.enums.AccountStatus;
import ru.grommash88.app.model.enums.AccountType;
import ru.grommash88.app.model.enums.CurrencyType;
import ru.grommash88.app.model.enums.ResultType;
import ru.grommash88.app.repo.AccRepo;

@RunWith(SpringRunner.class)
@SpringBootTest
public class testAccountManagerServiseImpl extends TestCase {

  @Autowired
  private AccountManagerServiceImpl accountManagerServise;


  @MockBean
  AccRepo accRepo;

  private final User user = User.builder()
      .name("Имя")
      .surname("Фамилия")
      .mail("Почта")
      .login("Логин")
      .password("Пароль")
      .userId(1L)
      .accounts(new ArrayList<>())
      .build();

  @Test
  public void createAccountTest() {

    Account account = accountManagerServise.createAccount(user);

    Mockito.verify(accRepo, Mockito.times(1)).save(account);

    Assert.assertNotNull(account);
    Assert.assertTrue(user.getAccounts().size() > 0);
    Assert.assertEquals(CurrencyType.RUB, account.getAccCurrency());
    Assert.assertEquals(AccountStatus.NORMAL, account.getAccStatus());
    Assert.assertEquals(AccountType.DEBIT, account.getAccountType());
    Assert.assertNotNull(account.getAccHolder());
    Assert.assertEquals(user, account.getAccHolder());
    Assert.assertNotNull(account.getBalance());
    Assert.assertNotNull(account.getAccountNumber());
  }

  @Test
  public void getAccountTest() {
    Long id = 1L;
    Account account = new Account();
    account.setAccId(id);

    Mockito.doReturn(Optional.of(account)).when(accRepo).findById(id);

    Optional<Account> optionalAccount = accountManagerServise.getAccount(id);

    Mockito.verify(accRepo, Mockito.times(1)).findById(ArgumentMatchers.anyLong());

    Assert.assertTrue(optionalAccount.isPresent());
    Assert.assertNotNull(optionalAccount.get());
    Assert.assertEquals(id, optionalAccount.get().getAccId());
  }

  @Test
  public void getAccountFailTest() {

    Long id = 1L;

    Mockito.doReturn(Optional.empty()).when(accRepo).findById(id);

    Optional<Account> optionalAccount = accountManagerServise.getAccount(id);

    Mockito.verify(accRepo, Mockito.times(1))
        .findById(ArgumentMatchers.anyLong());

    Assert.assertFalse(optionalAccount.isPresent());
  }

  @Test
  public void getAccountsTest() {

    List<Account> accountList = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      accountList.add(new Account());
    }

    Mockito.doReturn(accountList).when(accRepo).findAll();

    List<Account> resultList = accountManagerServise.getAccounts();

    Mockito.verify(accRepo, Mockito.times(1)).findAll();

    Assert.assertEquals(accountList.size(), resultList.size());
    Assert.assertNotNull(resultList);
  }

  @Test
  public void getAccountsFailTest() {

    Mockito.doReturn(new ArrayList<Account>()).when(accRepo).findAll();

    List<Account> resultList = accountManagerServise.getAccounts();

    Mockito.verify(accRepo, Mockito.times(1)).findAll();

    Assert.assertEquals(0, resultList.size());
  }

  @Test
  public void replenishmentTest() {
    String accNum = "8100000100";
    String moneyAmount = "500";

    Account account = new Account();
    account.setAccountNumber(accNum);
    account.setBalance(new BigDecimal("0.0"));

    Mockito.doReturn(Optional.of(account)).when(accRepo).findByAccountNumber(accNum);

    ResultType resultType = accountManagerServise.replenishment(accNum, moneyAmount);

    Mockito.verify(accRepo, Mockito.times(1)).findByAccountNumber(accNum);
    Mockito.verify(accRepo, Mockito.times(1)).save(account);

    Assert.assertEquals(ResultType.SUCCESFULLY_WORKED_OUT, resultType);
    Assert.assertEquals(500.0, account.getBalance().doubleValue(), 0.0);
  }

  @Test
  public void replenishmentFailTest() {
    String accNum = "8100000100";

    String moneyAmount = "500";

    Account account = new Account();
    account.setAccountNumber(accNum);
    account.setBalance(new BigDecimal("0.0"));

    Mockito.doReturn(Optional.empty()).when(accRepo).findByAccountNumber(accNum);

    ResultType resultType = accountManagerServise.replenishment(accNum, moneyAmount);

    Mockito.verify(accRepo, Mockito.times(1)).findByAccountNumber(accNum);

    Assert.assertEquals(ResultType.ACCOUNT_DOES_NOT_EXIST, resultType);
    Assert.assertEquals(0.0, account.getBalance().doubleValue(), 0.0);
  }

  @Test
  public void withdrawalTest() {
    String accNum = "8100000100";
    String moneyAmount = "500";

    Account account = new Account();
    account.setAccountNumber(accNum);
    account.setBalance(new BigDecimal("1500.0"));
    account.setAccStatus(AccountStatus.NORMAL);

    Mockito.doReturn(Optional.of(account)).when(accRepo).findByAccountNumber(accNum);

    ResultType resultType = accountManagerServise.withdrawal(accNum, moneyAmount);

    Mockito.verify(accRepo, Mockito.times(1)).findByAccountNumber(accNum);
    Mockito.verify(accRepo, Mockito.times(1)).save(account);

    Assert.assertEquals(ResultType.SUCCESFULLY_WORKED_OUT, resultType);
    Assert.assertEquals(1000.0, account.getBalance().doubleValue(), 0.0);
  }

  @Test
  public void withdrawalFailTest() {
    String accNum = "8100000100";
    String moneyAmount = "500";

    Mockito.doReturn(Optional.empty()).when(accRepo).findByAccountNumber(accNum);

    ResultType resultType = accountManagerServise.withdrawal(accNum, moneyAmount);

    Mockito.verify(accRepo, Mockito.times(1)).findByAccountNumber(accNum);
    Assert.assertEquals(ResultType.ACCOUNT_DOES_NOT_EXIST, resultType);

    Account account = new Account();
    account.setAccountNumber(accNum);
    account.setBalance(new BigDecimal("200.0"));
    account.setAccStatus(AccountStatus.LOCKED);

    Mockito.doReturn(Optional.of(account)).when(accRepo).findByAccountNumber(accNum);

    resultType = accountManagerServise.withdrawal(accNum, moneyAmount);

    Mockito.verify(accRepo, Mockito.times(2)).findByAccountNumber(accNum);
    Assert.assertEquals(ResultType.THE_STATUS_OF_THE_ACCOUNT_IS_NOT_NORMAL, resultType);
    Assert.assertEquals(200.0, account.getBalance().doubleValue(), 0.0);

    account.setAccStatus(AccountStatus.NORMAL);
    account.setBalance(new BigDecimal("499.99"));

    Mockito.doReturn(Optional.of(account)).when(accRepo).findByAccountNumber(accNum);

    resultType = accountManagerServise.withdrawal(accNum, moneyAmount);

    Mockito.verify(accRepo, Mockito.times(3)).findByAccountNumber(accNum);
    Assert.assertEquals(ResultType.INSUFFICIENT_FUNDS, resultType);
    Assert.assertEquals(499.99, account.getBalance().doubleValue(), 0.0);
  }

  @Test
  public void transferTest() {

    String fromAccNum = "8100000100";
    String toAccNum = "8100000101";
    String moneyAmount = "499.99";

    Account fromAccount = new Account();
    fromAccount.setAccountNumber(fromAccNum);
    fromAccount.setBalance(new BigDecimal("1500.0"));
    fromAccount.setAccStatus(AccountStatus.NORMAL);

    Account toAccount = new Account();
    toAccount.setAccountNumber(toAccNum);
    toAccount.setBalance(new BigDecimal("1000.0"));
    toAccount.setAccStatus(AccountStatus.NORMAL);

    Mockito.doReturn(Optional.of(fromAccount)).when(accRepo).findByAccountNumber(fromAccNum);
    Mockito.doReturn(Optional.of(toAccount)).when(accRepo).findByAccountNumber(toAccNum);

    ResultType resultType = accountManagerServise.transfer(fromAccNum, toAccNum, moneyAmount);

    Mockito.verify(accRepo, Mockito.times(1)).findByAccountNumber(fromAccNum);
    Mockito.verify(accRepo, Mockito.times(1)).save(fromAccount);
    Mockito.verify(accRepo, Mockito.times(1)).findByAccountNumber(toAccNum);
    Mockito.verify(accRepo, Mockito.times(1)).save(toAccount);

    Assert.assertEquals(ResultType.SUCCESFULLY_WORKED_OUT, resultType);
    Assert.assertEquals(1000.01, fromAccount.getBalance().doubleValue(), 0.0);
    Assert.assertEquals(1499.99, toAccount.getBalance().doubleValue(), 0.0);
  }

  @Test
  public void transferInsufficientFundsFailTest() {

    String fromAccNum = "8100000100";
    String toAccNum = "8100000101";
    String moneyAmount = "499.99";

    Account fromAccount = new Account();
    fromAccount.setAccountNumber(fromAccNum);
    fromAccount.setBalance(new BigDecimal("200.0"));
    fromAccount.setAccStatus(AccountStatus.NORMAL);

    Account toAccount = new Account();
    toAccount.setAccountNumber(toAccNum);
    toAccount.setBalance(new BigDecimal("1000.0"));
    toAccount.setAccStatus(AccountStatus.NORMAL);

    Mockito.doReturn(Optional.of(fromAccount)).when(accRepo).findByAccountNumber(fromAccNum);
    Mockito.doReturn(Optional.of(toAccount)).when(accRepo).findByAccountNumber(toAccNum);

    ResultType resultType = accountManagerServise.transfer(fromAccNum, toAccNum, moneyAmount);

    Mockito.verify(accRepo, Mockito.times(1)).findByAccountNumber(fromAccNum);
    Mockito.verify(accRepo, Mockito.times(1)).findByAccountNumber(toAccNum);

    Assert.assertEquals(ResultType.INSUFFICIENT_FUNDS, resultType);
    Assert.assertEquals(200.00, fromAccount.getBalance().doubleValue(), 0.0);
    Assert.assertEquals(1000.00, toAccount.getBalance().doubleValue(), 0.0);
  }

  @Test
  public void transferTheStatusOfAccountIsNotNormalFailTest() {

    String fromAccNum = "8100000100";
    String toAccNum = "8100000101";
    String moneyAmount = "499.99";

    Account fromAccount = new Account();
    fromAccount.setAccountNumber(fromAccNum);
    fromAccount.setBalance(new BigDecimal("1500.0"));
    fromAccount.setAccStatus(AccountStatus.LOCKED);

    Account toAccount = new Account();
    toAccount.setAccountNumber(toAccNum);
    toAccount.setBalance(new BigDecimal("1000.0"));
    toAccount.setAccStatus(AccountStatus.NORMAL);

    Mockito.doReturn(Optional.of(fromAccount)).when(accRepo).findByAccountNumber(fromAccNum);
    Mockito.doReturn(Optional.of(toAccount)).when(accRepo).findByAccountNumber(toAccNum);

    ResultType resultType = accountManagerServise.transfer(fromAccNum, toAccNum, moneyAmount);

    Mockito.verify(accRepo, Mockito.times(1)).findByAccountNumber(fromAccNum);
    Mockito.verify(accRepo, Mockito.times(1)).findByAccountNumber(toAccNum);

    Assert.assertEquals(ResultType.THE_STATUS_OF_THE_ACCOUNT_IS_NOT_NORMAL, resultType);
    Assert.assertEquals(1500.00, fromAccount.getBalance().doubleValue(), 0.0);
    Assert.assertEquals(1000.00, toAccount.getBalance().doubleValue(), 0.0);
  }

  @Test
  public void transferAccountDoesNotExistFailTest() {
    String fromAccNum = "8100000100";
    String toAccNum = "8100000101";
    String moneyAmount = "499.99";

    Mockito.doReturn(Optional.empty()).when(accRepo).findByAccountNumber(fromAccNum);
    Mockito.doReturn(Optional.empty()).when(accRepo).findByAccountNumber(toAccNum);

    ResultType resultType = accountManagerServise.transfer(fromAccNum, toAccNum, moneyAmount);

    Mockito.verify(accRepo, Mockito.times(1)).findByAccountNumber(fromAccNum);
    Mockito.verify(accRepo, Mockito.times(1)).findByAccountNumber(toAccNum);

    Assert.assertEquals(ResultType.ACCOUNT_DOES_NOT_EXIST, resultType);

    Account fromAccount = new Account();
    fromAccount.setAccountNumber(fromAccNum);
    fromAccount.setBalance(new BigDecimal("1500.0"));
    fromAccount.setAccStatus(AccountStatus.NORMAL);

    Mockito.doReturn(Optional.of(fromAccount)).when(accRepo).findByAccountNumber(fromAccNum);
    Mockito.doReturn(Optional.empty()).when(accRepo).findByAccountNumber(toAccNum);

    resultType = accountManagerServise.transfer(fromAccNum, toAccNum, moneyAmount);

    Mockito.verify(accRepo, Mockito.times(2)).findByAccountNumber(fromAccNum);
    Mockito.verify(accRepo, Mockito.times(2)).findByAccountNumber(toAccNum);

    Assert.assertEquals(ResultType.ACCOUNT_DOES_NOT_EXIST, resultType);
    Assert.assertEquals(1500.00, fromAccount.getBalance().doubleValue(), 0.0);

    Account toAccount = new Account();
    toAccount.setAccountNumber(toAccNum);
    toAccount.setBalance(new BigDecimal("1000.0"));
    toAccount.setAccStatus(AccountStatus.NORMAL);

    Mockito.doReturn(Optional.empty()).when(accRepo).findByAccountNumber(fromAccNum);
    Mockito.doReturn(Optional.of(toAccount)).when(accRepo).findByAccountNumber(toAccNum);

    resultType = accountManagerServise.transfer(fromAccNum, toAccNum, moneyAmount);

    Mockito.verify(accRepo, Mockito.times(3)).findByAccountNumber(fromAccNum);
    Mockito.verify(accRepo, Mockito.times(3)).findByAccountNumber(toAccNum);

    Assert.assertEquals(1000.00, toAccount.getBalance().doubleValue(), 0.0);
    Assert.assertEquals(ResultType.ACCOUNT_DOES_NOT_EXIST, resultType);

  }
}