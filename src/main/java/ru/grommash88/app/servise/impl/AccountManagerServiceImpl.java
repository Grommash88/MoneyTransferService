package ru.grommash88.app.servise.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.grommash88.app.model.Account;
import ru.grommash88.app.model.User;
import ru.grommash88.app.model.enums.AccountStatus;
import ru.grommash88.app.model.enums.ResultType;
import ru.grommash88.app.repo.AccRepo;
import ru.grommash88.app.servise.AccountBuilder;
import ru.grommash88.app.servise.AccountsManagerService;


@Service
@RequiredArgsConstructor
public class AccountManagerServiceImpl implements AccountsManagerService {

  private final AccRepo accRepo;

  private ResultType transferMoney(Account fromAcc, Account toAcc, String amount) {

    if (fromAcc.getBalance().compareTo(new BigDecimal(amount)) >= 0) {

      synchronized (fromAcc.getBalance()) {
        fromAcc.setBalance(fromAcc.getBalance().subtract(new BigDecimal(amount)));
        accRepo.save(fromAcc);
      }

      synchronized (toAcc.getBalance()) {
        toAcc.setBalance(toAcc.getBalance().add(new BigDecimal(amount)));
        accRepo.save(toAcc);
      }
      return ResultType.SUCCESFULLY_WORKED_OUT;
    }
    return ResultType.INSUFFICIENT_FUNDS;
  }

  private ResultType withdrawalMoney(Account account, String amount) {
    if (account.getBalance().compareTo(new BigDecimal(amount)) >= 0) {
      account.setBalance(account.getBalance().subtract(new BigDecimal(amount)));
      accRepo.save(account);
      return ResultType.SUCCESFULLY_WORKED_OUT;

    } else {
      return ResultType.INSUFFICIENT_FUNDS;
    }
  }

  @Override
  public Account createAccount(User user) {

    Account createdAcc = AccountBuilder.createAccount(user);
    accRepo.save(createdAcc);
    return createdAcc;
  }

  @Override
  public Optional getAccount(Long id) {
    Optional<Account> optionalAccount = accRepo.findById(id);
    return optionalAccount;
  }

  @Override
  public Optional getAccount(String accNum) {
    Optional<Account> optionalAccount = accRepo.findByAccountNumber(accNum);
    return optionalAccount;
  }

  @Override
  public List<Account> getAccounts() {
    return accRepo.findAll();
  }

  @Override
  public ResultType replenishment(String accNum, String amount) {

    Optional<Account> optionalAccount = accRepo.findByAccountNumber(accNum);

    if (!optionalAccount.isPresent()) {
      return ResultType.ACCOUNT_DOES_NOT_EXIST;
    }

    Account account = optionalAccount.get();

    synchronized (account.getBalance()) {
      account.setBalance(account.getBalance().add(new BigDecimal(amount)));
      accRepo.save(account);
    }

    return ResultType.SUCCESFULLY_WORKED_OUT;
  }

  @Override
  public ResultType withdrawal(String accNum, String amount) {

    Optional<Account> optionalAccount = accRepo.findByAccountNumber(accNum);

    if (!optionalAccount.isPresent()) {
      return ResultType.ACCOUNT_DOES_NOT_EXIST;
    } else {

      Account account = optionalAccount.get();

      if (account.getAccStatus() == AccountStatus.NORMAL) {
        return withdrawalMoney(account, amount);
      }
      return ResultType.THE_STATUS_OF_THE_ACCOUNT_IS_NOT_NORMAL;
    }
  }

  @Override
  public ResultType transfer(String from, String to, String amount) {

    Optional<Account> optionalFromAccount = accRepo.findByAccountNumber(from);
    Optional<Account> optionalToAccount = accRepo.findByAccountNumber(to);

    if (optionalFromAccount.isPresent() && optionalToAccount.isPresent()) {

      Account fromAcc = optionalFromAccount.get();
      Account toAcc = optionalToAccount.get();

      if (fromAcc.getAccStatus().equals(AccountStatus.NORMAL)) {

        return transferMoney(fromAcc, toAcc, amount);

      } else {
        return ResultType.THE_STATUS_OF_THE_ACCOUNT_IS_NOT_NORMAL;
      }

    } else {
      return ResultType.ACCOUNT_DOES_NOT_EXIST;
    }
  }


}
