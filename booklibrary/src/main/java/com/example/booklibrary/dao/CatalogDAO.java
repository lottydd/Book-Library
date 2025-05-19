package com.example.booklibrary.dao;

import com.example.booklibrary.model.Catalog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public class CatalogDAO extends BaseDAO<Catalog, Integer> {
    private static final Logger logger = LoggerFactory.getLogger(CatalogDAO.class);

    public CatalogDAO() {
        super(Catalog.class);
    }


    public List<Catalog> findRootCatalogs() {
        logger.debug("Поиск корневых каталогов");
        return entityManager.createQuery(
                "SELECT c FROM Catalog c WHERE c.parent IS NULL",
                Catalog.class
        ).getResultList();
    }

    public List<Catalog> findByParentId(Integer parentId) {
        logger.debug("Поиск каталогов по parentId: {}", parentId);
        List<Catalog> catalogs = entityManager.createQuery(
                        "SELECT c FROM Catalog c WHERE c.parent.id = :parentId",
                        Catalog.class
                )
                .setParameter("parentId", parentId)
                .getResultList();
        if (catalogs.isEmpty()) {
            logger.info("Не найдено каталогов для parentId: {}", parentId);
        } else {
            logger.info("Найдено {} каталогов для parentId: {}", catalogs.size(), parentId);
        }
        return catalogs;
    }
}
