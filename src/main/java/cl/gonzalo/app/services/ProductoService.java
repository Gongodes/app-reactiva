package cl.gonzalo.app.services;

import cl.gonzalo.app.documents.Producto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductoService {

    public Flux<Producto> findAllConNombreUpperCase();

    public Flux<Producto> findAllConNombreUpperCaseRepeat();

    public Flux<Producto> findAll();

    public Mono<Producto> findById(String id);

    public Mono<Producto> save(Producto producto);

    public Mono<Void> delete(Producto producto);

}
