package com.farm.dolores.farmacia.repository.spec;

import com.farm.dolores.farmacia.entity.Productos;
import org.springframework.data.jpa.domain.Specification;

public final class ProductosSpecifications {
    private ProductosSpecifications() {}

    public static Specification<Productos> build(String q, Long categoriaId, Long laboratorioId, Boolean requiereReceta) {
        Specification<Productos> spec = (root, query, cb) -> cb.conjunction();

        if (q != null && !q.isBlank()) {
            String term = "%" + q.trim().toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("nombre")), term),
                    cb.like(cb.lower(root.get("descripcion")), term),
                    cb.like(cb.lower(root.get("principioActivo")), term)
            ));
        }

        if (categoriaId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("categoria").get("id"), categoriaId));
        }

        if (laboratorioId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("laboratorios").get("idLaboratorios"), laboratorioId));
        }

        if (requiereReceta != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("requerireReceta"), requiereReceta));
        }

        return spec;
    }
}
