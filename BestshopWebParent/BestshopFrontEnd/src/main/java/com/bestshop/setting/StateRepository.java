package com.bestshop.setting;

import com.bestshop.common.entity.Country;
import com.bestshop.common.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StateRepository extends JpaRepository<State, Integer> {

    public List<State> findByCountryOrderByNameAsc(Country country);
}