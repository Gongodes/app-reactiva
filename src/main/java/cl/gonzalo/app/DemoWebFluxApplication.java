package cl.gonzalo.app;

import cl.gonzalo.app.dao.ProductoDao;
import cl.gonzalo.app.documents.Producto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.Date;

@SpringBootApplication
public class DemoWebFluxApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DemoWebFluxApplication.class);
    @Autowired
    private ProductoDao dao;

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    public static void main(String[] args) {
        SpringApplication.run(DemoWebFluxApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        mongoTemplate.dropCollection("productos").subscribe();

        Flux.just(
                new Producto("Tv Panasonic", 156.000),
                new Producto("Camara Sony", 126.000),
                new Producto("Notebook Hp", 356.000),
                new Producto("Mouse Mac", 6.000),
                new Producto("Escritorio Gamer", 466.000)

        ).flatMap(producto -> {
            producto.setCreateAt(new Date());
            return dao.save(producto);
        }).subscribe(producto -> log.info("insert: " +
                producto.getId() + " " +
                producto.getNombre()));
    }
}
