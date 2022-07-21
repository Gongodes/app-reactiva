package cl.gonzalo.app;


import cl.gonzalo.app.documents.Categoria;
import cl.gonzalo.app.documents.Producto;
import cl.gonzalo.app.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;

import java.util.Date;

@SpringBootApplication
public class DemoWebFluxApplication implements CommandLineRunner {


    @Autowired
    private ProductoService service;



    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    public static void main(String[] args) {
        SpringApplication.run(DemoWebFluxApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        mongoTemplate.dropCollection("productos").subscribe();
        mongoTemplate.dropCollection("categorias").subscribe();

        Categoria electronico = new Categoria("Electronico");
        Categoria deporte = new Categoria("Deporte");
        Categoria computacion = new Categoria("Computacion");
        Categoria muebles = new Categoria("Muebles");

        Flux.just(electronico,computacion,deporte,muebles)
                .flatMap(service::save).thenMany( Flux.just(
                        new Producto("Tv Panasonic", 156.000,electronico),
                        new Producto("Camara Sony", 126.000,electronico),
                        new Producto("Notebook Hp", 356.000,computacion),
                        new Producto("Mouse Mac", 6.000,computacion),
                        new Producto("Escritorio Gamer", 466.000,muebles)

                ).flatMap(producto -> {
                    producto.setCreateAt(new Date());
                    return service.save(producto);
                }))

       .subscribe();
    }
}
