package cl.gonzalo.app.services;


import cl.gonzalo.app.dao.CategoriaDao;
import cl.gonzalo.app.dao.ProductoDao;
import cl.gonzalo.app.documents.Categoria;
import cl.gonzalo.app.documents.Producto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductoServiceImpl implements  ProductoService{

    @Autowired
    private ProductoDao dao;
    @Autowired
    private CategoriaDao catDao;

    @Override
    public Flux<Producto> findAllConNombreUpperCase() {
        return dao.findAll().map(producto -> {

            producto.setNombre(producto.getNombre().toUpperCase());

            return producto;
        });
    }

    @Override
    public Flux<Producto> findAllConNombreUpperCaseRepeat() {
        return findAllConNombreUpperCase().repeat(5000);
    }

    @Override
    public Flux<Producto> findAll() {
        return dao.findAll();
    }

    @Override
    public Mono<Producto> findById(String id) {
        return dao.findById(id);
    }

    @Override
    public Mono<Producto> save(Producto producto) {
        return dao.save(producto);
    }

    @Override
    public Mono<Void> delete(Producto producto) {
        return dao.delete(producto);
    }

    @Override
    public Flux<Categoria> findAllCategoria() {
        return catDao.findAll();
    }

    @Override
    public Mono<Categoria> findCategoriaById(String id) {
        return catDao.findById(id);
    }

    @Override
    public Mono<Categoria> save(Categoria categoria) {
        return catDao.save(categoria);
    }
}
