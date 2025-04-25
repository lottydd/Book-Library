package com.example.booklibrary.dao;

import com.example.booklibrary.model.Catalog;

public class CatalogDAO extends BaseDAO<Catalog, Integer>{
    public CatalogDAO() {
        super(Catalog.class);
    }
}
