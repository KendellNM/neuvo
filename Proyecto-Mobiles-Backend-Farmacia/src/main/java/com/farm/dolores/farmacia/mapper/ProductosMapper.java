package com.farm.dolores.farmacia.mapper;

import com.farm.dolores.farmacia.dto.ProductCatalogDto;
import com.farm.dolores.farmacia.dto.ProductDetailDto;
import com.farm.dolores.farmacia.dto.ProductStockDto;
import com.farm.dolores.farmacia.dto.ProductMobileDto;
import com.farm.dolores.farmacia.entity.Productos;

public final class ProductosMapper {
    private ProductosMapper() {}

    public static ProductCatalogDto toCatalogDto(Productos p) {
        return ProductCatalogDto.builder()
                .id(p.getIdProductos())
                .nombre(p.getNombre())
                .descripcion(p.getDescripcion())
                .principioActivo(p.getPrincipioActivo())
                .imagenUrl(p.getImagenUrl())
                .precio(p.getPrecio())
                .precioOferta(p.getPrecioOferta())
                .requerireReceta(p.getRequerireReceta())
                .categoriaId(p.getCategoria() != null ? p.getCategoria().getId() : null)
                .categoriaNombre(p.getCategoria() != null ? p.getCategoria().getNombre() : null)
                .laboratorioId(p.getLaboratorios() != null ? p.getLaboratorios().getIdLaboratorios() : null)
                .laboratorioNombre(p.getLaboratorios() != null ? p.getLaboratorios().getNombre() : null)
                .build();
    }

    public static ProductDetailDto toDetailDto(Productos p) {
        return ProductDetailDto.builder()
                .id(p.getIdProductos())
                .codigoBarras(p.getCodigoBarras())
                .nombre(p.getNombre())
                .descripcion(p.getDescripcion())
                .principioActivo(p.getPrincipioActivo())
                .concentracion(p.getConcentracion())
                .requerireReceta(p.getRequerireReceta())
                .precio(p.getPrecio())
                .precioOferta(p.getPrecioOferta())
                .stock(p.getStock())
                .stockMin(p.getStockMin())
                .imagenUrl(p.getImagenUrl())
                .indicaciones(p.getIndicaciones())
                .contraindicaciones(p.getContraindicaciones())
                .efectosSecundarios(p.getEfectosSecundarios())
                .dosidificador(p.getDosidificador())
                .estado(p.getEstado())
                .categoriaId(p.getCategoria() != null ? p.getCategoria().getId() : null)
                .categoriaNombre(p.getCategoria() != null ? p.getCategoria().getNombre() : null)
                .laboratorioId(p.getLaboratorios() != null ? p.getLaboratorios().getIdLaboratorios() : null)
                .laboratorioNombre(p.getLaboratorios() != null ? p.getLaboratorios().getNombre() : null)
                .build();
    }

    public static ProductStockDto toStockDto(Productos p) {
        return ProductStockDto.builder()
                .id(p.getIdProductos())
                .nombre(p.getNombre())
                .codigoBarras(p.getCodigoBarras())
                .stock(p.getStock())
                .stockMin(p.getStockMin())
                .build();
    }

    public static ProductMobileDto toMobileDto(Productos p) {
        return ProductMobileDto.builder()
                .id(p.getIdProductos())
                .codigoBarras(p.getCodigoBarras())
                .nombre(p.getNombre())
                .descripcion(p.getDescripcion())
                .principioActivo(p.getPrincipioActivo())
                .concentracion(p.getConcentracion())
                .requerireReceta(p.getRequerireReceta())
                .precio(p.getPrecio())
                .precioOferta(p.getPrecioOferta())
                .stock(p.getStock())
                .imagenUrl(p.getImagenUrl())
                .indicaciones(p.getIndicaciones())
                .contraindicaciones(p.getContraindicaciones())
                .efectosSecundarios(p.getEfectosSecundarios())
                .dosidificador(p.getDosidificador())
                .categoriaNombre(p.getCategoria() != null ? p.getCategoria().getNombre() : null)
                .laboratorioNombre(p.getLaboratorios() != null ? p.getLaboratorios().getNombre() : null)
                .disponible(p.getStock() != null && p.getStock() > 0)
                .build();
    }
}
