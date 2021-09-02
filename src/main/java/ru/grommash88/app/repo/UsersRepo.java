package ru.grommash88.app.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.grommash88.app.model.User;

@Repository
public interface UsersRepo extends CrudRepository<User, Long> {

}
