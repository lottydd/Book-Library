package com.example.booklibrary.dao;

import com.example.booklibrary.model.Catalog;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public class CatalogDAO extends BaseDAO<Catalog, Integer>{
    public CatalogDAO() {
        super(Catalog.class);
    }



    public List<Catalog> findRootCatalogsWithChildren() {
        return entityManager.createQuery(
                        "SELECT DISTINCT c FROM Catalog c " +
                                "LEFT JOIN FETCH c.children " +
                                "WHERE c.parent IS NULL",
                        Catalog.class)
                .getResultList();
    }

    public List<Catalog> findByParentIdWithChildren(Integer parentId) {
        return entityManager.createQuery(
                        "SELECT DISTINCT c FROM Catalog c " +
                                "LEFT JOIN FETCH c.children " +
                                "WHERE c.parent.id = :parentId",
                        Catalog.class)
                .setParameter("parentId", parentId)
                .getResultList();
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
