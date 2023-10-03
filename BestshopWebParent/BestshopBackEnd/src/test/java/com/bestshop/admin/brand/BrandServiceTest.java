package com.bestshop.admin.brand;

import com.bestshop.common.entity.Brand;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class BrandServiceTest {

    @MockBean
    private BrandRepository repository;

    @InjectMocks
    private BrandService service;

    @Test
    public void testEditExistingBrand(){
        Integer id = 1;
        String name = "Acer";
        Brand brand = new Brand(id, name);

        Mockito.when(repository.findById(1)).thenReturn(Optional.of(brand));

        Brand savedBrand = service.save(brand);

        assertNotNull(brand);
        assertEquals(name, savedBrand.getName());

    }

    @Test
    public void testCheckUniqueInNewModeReturnDuplicate(){
        Integer id = null;
        String name = "Acer";
        Brand brand = new Brand(name);

        Mockito.when(repository.findByName(name)).thenReturn(brand);

        String result = service.checkUnique(id, name);
        assertEquals("Duplicate", result);

    }

    @Test
    public void testCheckUniqueInNewModeReturnOk(){
        Integer id = null;
        String name = "Acer";
        Brand brand = new Brand(name);

        Mockito.when(repository.findByName(name)).thenReturn(brand);

        String result = service.checkUnique(id, "Oracle");
        assertEquals("OK", result);
    }

    @Test
    public void testCheckUniqueInEditModeReturnDuplicate(){
        Integer id = 1;
        String name = "Canon";
        Brand brand = new Brand(name);

        Mockito.when(repository.findByName(name)).thenReturn(brand);

        String result = service.checkUnique(2, "Canon");
        assertEquals("Duplicate", result);

    }

    @Test
    public void testCheckUniqueInEditModeReturnOk(){
        Integer id = 1;
        String name = "Canon";
        Brand brand = new Brand(name);

        Mockito.when(repository.findByName(name)).thenReturn(brand);

        String result = service.checkUnique(2, "Oracle");
        assertEquals("OK", result);

    }

}