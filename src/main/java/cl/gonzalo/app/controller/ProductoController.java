package cl.gonzalo.app.controller;

import cl.gonzalo.app.documents.Categoria;
import cl.gonzalo.app.documents.Producto;
import cl.gonzalo.app.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Date;
import java.util.UUID;

@SessionAttributes("producto")
@Controller
public class ProductoController {
    @Autowired
    private ProductoService service;



    @Value("${config.uploads.path}")
    private String path;

    @ModelAttribute("categorias")
    public Flux<Categoria> categorias() {
        return service.findAllCategoria();
    }


    @GetMapping("/uploads/img/{nombreFoto:.+}")
    public Mono<ResponseEntity<Resource>>verFoto(@PathVariable String nombreFoto) throws MalformedURLException {
        Path ruta = Paths.get(path).resolve(nombreFoto).toAbsolutePath();
        Resource imagen = new UrlResource(ruta.toUri());

        return Mono.just(
                ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + imagen.getFilename() + "\"")
                        .body(imagen)
        );
    }

    @GetMapping("/ver/{id}")
    public Mono<String> ver(Model model, @PathVariable String id){

        return service.findById(id).doOnNext(p ->{

            model.addAttribute("producto",p);
            model.addAttribute("titulo", "Detalle  Producto");
        }).switchIfEmpty(Mono.just(new Producto())).flatMap(p ->{
            if (p.getId() == null) {
                return Mono.error(new InterruptedException("No existe le producto"));
            }
            return Mono.just(p);
        }).then(Mono.just("ver"))
                .onErrorResume(ex -> Mono.just("redirect:/listar?error=no+existe+el+producto"));
    }

    @GetMapping({"/listar", "/"})
    public Mono<String> listar(Model model) {

        Flux<Producto> productos = service.findAllConNombreUpperCase();

        productos.subscribe();
        model.addAttribute("productos", productos);
        model.addAttribute("titulo", "Listado de Productos");

        return Mono.just("listar");

    }

    @GetMapping("/form")
    public Mono<String> crear(Model model) {

        model.addAttribute("producto", new Producto());
        model.addAttribute("titulo", "Formulario de Producto");
        model.addAttribute("boton", "crear");
        return Mono.just("form");
    }


    @GetMapping("/form-v2/{id}")
    public Mono<String> editarv2(@PathVariable String id, Model model) {

        return service.findById(id).doOnNext(p -> {

                    model.addAttribute("titulo", "Editar Producto");
                    model.addAttribute("boton", "editar");
                    model.addAttribute("producto", p);
                }).defaultIfEmpty(new Producto())
                .flatMap(p -> {
                    if (p.getId() == null) {
                        return Mono.error(new InterruptedException("No existe le producto"));
                    }
                    return Mono.just(p);
                })
                .then(Mono.just("form"))
                .onErrorResume(ex -> Mono.just("redirect:/listar?error=no+existe+el+producto"));


    }


    @GetMapping("/form/{id}")
    public Mono<String> editar(@PathVariable String id, Model model) {

        Mono<Producto> productoMono = service.findById(id).defaultIfEmpty(new Producto());
        model.addAttribute("titulo", "Editar Producto");
        model.addAttribute("boton", "Editar");
        model.addAttribute("producto", productoMono);


        return Mono.just("form");
    }


    @PostMapping("/form")
    public Mono<String> guardar(@Valid Producto producto, BindingResult result,
                                SessionStatus status, Model model,@RequestPart FilePart file) {
        if (result.hasErrors()) {
            model.addAttribute("titulo", "Editar Producto");
            model.addAttribute("boton", "Guardar");
            return Mono.just("form");

        } else {
            status.setComplete();


            Mono<Categoria> categoria = service.findCategoriaById(producto.getCategoria().getId());

            return categoria.flatMap(c -> {
                if (producto.getCreateAt() == null) {

                    producto.setCreateAt(new Date());
                }
                if (!file.filename().isEmpty()
                ) {
                    producto.setFoto(UUID.randomUUID().toString()+"-"+ file.filename()
                            .replace(" ","")
                            .replace(":","")
                            .replace("\\",""));
                }
                producto.setCategoria(c);
                return service.save(producto);
            }).doOnNext(p -> {

            })
                    .flatMap(p->{
                        if(!file.filename().isEmpty()){
                            return file.transferTo(new File(path+p.getFoto()));
                        }
                        return Mono.empty();
                    })
                    .thenReturn("redirect:/listar?success=producto+guardado+con+exito");
        }
    }

    @GetMapping("/eliminar/{id}")
    public Mono<String> eliminar(@PathVariable String id) {

        return service.findById(id)
                .defaultIfEmpty(new Producto())
                .flatMap(p -> {
                    if (p.getId() == null) {
                        return Mono.error(new InterruptedException("No existe le producto a eliminar"));
                    }
                    return Mono.just(p);
                })
                .flatMap(p -> {

                    return service.delete(p);
                }).then(Mono.just("redirect:/listar?success=producto+eliminado+con+exito"))
                .onErrorResume(ex -> Mono.just("redirect:/listar?error=no+existe+el+producto+a+eliminar"));
    }

    @GetMapping("/listar-datadriver")
    public String listarDataDriver(Model model) {

        Flux<Producto> productos = service.findAllConNombreUpperCase().delayElements(Duration.ofSeconds(1));

        productos.subscribe();
        model.addAttribute("productos",
                new ReactiveDataDriverContextVariable(productos, 1));
        model.addAttribute("titulo", "Listado de Productos");

        return "listar";

    }

    @GetMapping("/listar-full")
    public String listarFull(Model model) {

        Flux<Producto> productos = service.findAllConNombreUpperCaseRepeat();


        model.addAttribute("productos",
                new ReactiveDataDriverContextVariable(productos, 1));
        model.addAttribute("titulo", "Listado de Productos");

        return "listar";

    }


    @GetMapping("/listar-chunked")
    public String listarChunked(Model model) {

        Flux<Producto> productos = service.findAllConNombreUpperCaseRepeat();


        model.addAttribute("productos",
                new ReactiveDataDriverContextVariable(productos, 1));
        model.addAttribute("titulo", "Listado de Productos");

        return "listar-chunked";

    }

}
