package ru.grommash88.app.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.grommash88.app.model.Account;

public interface AccRepo extends JpaRepository<Account, Long> {

  Optional<Account> findByAccountNumber(String accNum);
}
