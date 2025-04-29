package com.example.booklibrary.dao;

import com.example.booklibrary.model.Catalog;
import org.springframework.stereotype.Repository;

@Repository

public class CatalogDAO extends BaseDAO<Catalog, Integer>{
    public CatalogDAO() {
        super(Catalog.class);
    }
}
