package com.example.booklibrary.dao;

import com.example.booklibrary.model.Catalog;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public class CatalogDAO extends BaseDAO<Catalog, Integer>{

    public CatalogDAO() {
        super(Catalog.class);
    }


    public List<Catalog> findRootCatalogs() {
        return entityManager.createQuery(
                "SELECT c FROM Catalog c WHERE c.parent IS NULL",
                Catalog.class
        ).getResultList();
    }

    public List<Catalog> findByParentId(Integer parentId) {
        return entityManager.createQuery(
                        "SELECT c FROM Catalog c WHERE c.parent.id = :parentId",
                        Catalog.class
                )
                .setParameter("parentId", parentId)
                .getResultList();
    }

}
