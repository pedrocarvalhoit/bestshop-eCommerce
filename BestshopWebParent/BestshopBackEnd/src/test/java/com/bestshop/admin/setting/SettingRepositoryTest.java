package com.bestshop.admin.setting;

import com.bestshop.common.entity.setting.Setting;
import com.bestshop.common.entity.setting.SettingCategory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SettingRepositoryTest {

    @Autowired
    SettingRepository repo;

    @Test
    @Rollback(value = false)
    public void testCreateGeneralSettings() {
        Setting siteName = new Setting("SITE_NAME", "Bestshop", SettingCategory.GENERAL);
        Setting siteLogo = new Setting("SITE_LOGO", "Bestshop.png", SettingCategory.GENERAL);
        Setting copyright = new Setting("COPYRIGHT", "Copyright (C) 2023 Bestshop Ltd.", SettingCategory.GENERAL);

        repo.saveAll(List.of(siteName, siteLogo, copyright));

        Iterable<Setting> iterable = repo.findAll();

       assertTrue(iterable.iterator().hasNext());
    }

    @Test
    @Rollback(value = false)
    public void testCreateCurrencySettings() {
        Setting currencyId = new Setting("CURRENCY_ID", "1", SettingCategory.CURRENCY);
        Setting symbol = new Setting("CURRENCY_SYMBOL", "$", SettingCategory.CURRENCY);
        Setting symbolPosition = new Setting("CURRENCY_SYMBOL_POSITION", "before", SettingCategory.CURRENCY);
        Setting decimalPointType = new Setting("DECIMAL_POINT_TYPE", "POINT", SettingCategory.CURRENCY);
        Setting decimalDigits = new Setting("DECIMAL_DIGITS", "2", SettingCategory.CURRENCY);
        Setting thousandsPointType = new Setting("THOUSANDS_POINT_TYPE", "COMMA", SettingCategory.CURRENCY);

        repo.saveAll(List.of(currencyId, symbol, symbolPosition, decimalPointType,
                decimalDigits, thousandsPointType));

    }

    @Test
    public void testListSettingsByCategory() {
        List<Setting> settings = repo.findByCategory(SettingCategory.GENERAL);

        settings.forEach(System.out::println);
    }


}