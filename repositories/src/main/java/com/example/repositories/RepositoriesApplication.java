package com.example.repositories;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

/*
@TypeHint (
	types = SimpleR2dbcRepository.class ,
	access = TypeAccess.AUTO_DETECT
)*/
@SpringBootApplication
public class RepositoriesApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(RepositoriesApplication.class, args);
		Thread.currentThread().join();
	}

	@Bean
	ApplicationRunner runner(UserRepository customerRepository) {
		return args ->
			customerRepository
				.create(new User(UUID.randomUUID().toString(), "Bob"))
				.thenMany(customerRepository.findAll())
				.subscribe(System.out::println);
	}
}

@Repository
interface UserRepository extends R2dbcRepository<User, String>, UserRepositoryInternal {
	Mono<Long> count();
}

@Component
interface UserRepositoryInternal {
	Mono<User> findOneWithAuthoritiesByLogin(String login);

	Mono<User> create(User user);
}


class UserRepositoryInternalImpl implements UserRepositoryInternal {


	private final R2dbcEntityTemplate r2dbcEntityTemplate;

	public UserRepositoryInternalImpl(R2dbcEntityTemplate r2dbcEntityTemplate) {
		this.r2dbcEntityTemplate = r2dbcEntityTemplate;
	}

	@Override
	public Mono<User> findOneWithAuthoritiesByLogin(String login) {
		return findOneWithAuthoritiesBy("login", login);
	}


	@Override
	public Mono<User> create(User user) {
		return r2dbcEntityTemplate.insert(User.class).using(user).defaultIfEmpty(user);
	}

	private Mono<User> findOneWithAuthoritiesBy(String fieldName, Object fieldValue) {
		return Mono.empty();
	}


}

@Table("users")
record User(@Id String id, String name) {
}




