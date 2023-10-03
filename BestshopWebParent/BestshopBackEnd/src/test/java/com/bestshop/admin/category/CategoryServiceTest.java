package com.bestshop.admin.category;

import com.bestshop.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class CategoryServiceTest {

    @MockBean
    private CategoryReposiroty reposiroty;

    @InjectMocks
    private CategoryService service;

    @Test
    void testCheckUniqueInNewModeReturnDuplicateName() {
        Integer id = null;
        String name = "Computers";
        String alias = "abc";

        Category category = new Category(id, name, alias);

        Mockito.when(reposiroty.findByName(name)).thenReturn(category);
        Mockito.when(reposiroty.findByAlias(alias)).thenReturn(null);

        assertEquals(service.checkUnique(id, name, alias), "DuplicatedName");
    }

    @Test
    void testCheckUniqueInNewModeReturnDuplicateAlias() {
        Integer id = null;
        String name = "NameABC";
        String alias = "abc";

        Category category = new Category(id, name, alias);

        Mockito.when(reposiroty.findByName(name)).thenReturn(null);
        Mockito.when(reposiroty.findByAlias(alias)).thenReturn(category);

        assertEquals(service.checkUnique(id, name, alias), "DuplicatedAlias");
    }

    @Test
    void testCheckUniqueInNewModeReturnOK() {
        Integer id = null;
        String name = "NameABC";
        String alias = "abc";

        Category category = new Category(id, name, alias);

        Mockito.when(reposiroty.findByName(name)).thenReturn(null);
        Mockito.when(reposiroty.findByAlias(alias)).thenReturn(null);

        assertEquals(service.checkUnique(id, name, alias), "OK");
    }

    @Test
    public void testCheckUniqueInEditModeReturnDuplicateName() {
        Integer id = 1;
        String name = "Computers";
        String alias = "abc";

        Category category = new Category(2, name, alias);

        Mockito.when(reposiroty.findByName(name)).thenReturn(category);
        Mockito.when(reposiroty.findByAlias(alias)).thenReturn(null);

        String result = service.checkUnique(id, name, alias);

        assertEquals(result, "DuplicateName");
    }

    @Test
    public void testCheckUniqueInEditModeReturnDuplicateAlias() {
        Integer id = 1;
        String name = "NameABC";
        String alias = "computers";

        Category category = new Category(2, name, alias);

        Mockito.when(reposiroty.findByName(name)).thenReturn(null);
        Mockito.when(reposiroty.findByAlias(alias)).thenReturn(category);

        String result = service.checkUnique(id, name, alias);

        assertEquals(result, "DuplicateAlias");
    }

    @Test
    public void testCheckUniqueInEditModeReturnOK() {
        Integer id = 1;
        String name = "NameABC";
        String alias = "computers";

        Category category = new Category(id, name, alias);

        Mockito.when(reposiroty.findByName(name)).thenReturn(null);
        Mockito.when(reposiroty.findByAlias(alias)).thenReturn(category);

        String result = service.checkUnique(id, name, alias);

        assertEquals(result, "OK");
    }

}