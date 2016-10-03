package com.dmitring.yainterfaceliftdownloader.repositories;

import com.dmitring.yainterfaceliftdownloader.domain.ApplicationVariable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationVariableRepository extends CrudRepository<ApplicationVariable, String> {

}
