package com.bestshop.setting;

import com.bestshop.common.entity.setting.Setting;
import com.bestshop.common.entity.setting.SettingCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SettingRepositoryTest {

    @Autowired
    SettingRepository repo;

    @Test
    @Rollback(false)
    public void testFindByTwoCategories() {
        List<Setting> settings = repo.findByTwoCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
        settings.forEach(System.out::println);
    }

}