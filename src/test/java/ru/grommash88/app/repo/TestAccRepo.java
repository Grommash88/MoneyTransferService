package ru.grommash88.app.repo;

import java.util.Optional;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.grommash88.app.model.Account;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestAccRepo {

  @MockBean
  AccRepo accRepo;

  private final String ACC_NUM = "8100000100";

  @Test
  public void findByAccountNumberTest() {

    Mockito.doReturn(Optional.of(Account.builder().accountNumber(ACC_NUM).build())).when(accRepo)
        .findByAccountNumber(ACC_NUM);

    Optional<Account> optionalAccount = accRepo.findByAccountNumber(ACC_NUM);

    Mockito.verify(accRepo, Mockito.times(1))
        .findByAccountNumber(ArgumentMatchers.anyString());
    Assert.assertTrue(optionalAccount.isPresent());
  }

  @Test
  public void findByAccountNumberFailTest() {

    Mockito.doReturn(Optional.empty()).when(accRepo).findByAccountNumber(ACC_NUM);

    Optional<Account> optionalAccount = accRepo.findByAccountNumber(ACC_NUM);

    Mockito.verify(accRepo, Mockito.times(1))
        .findByAccountNumber(ArgumentMatchers.anyString());
      Assert.assertFalse(optionalAccount.isPresent());

  }
}
