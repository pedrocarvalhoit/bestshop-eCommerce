package com.bestshop.admin.setting;


import com.bestshop.common.entity.Setting;
import com.bestshop.common.entity.SettingCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettingRepository extends JpaRepository<Setting, String>  {

    public List<Setting> findByCategory(SettingCategory category);
}
