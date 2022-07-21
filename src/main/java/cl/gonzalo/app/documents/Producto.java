package cl.gonzalo.app.documents;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
@Document(collection = "productos")
public class Producto {

    @Id

    private String id;

    @NotEmpty
    private String nombre;
    @NotNull
    private Double precio;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createAt;
    @Valid
    private Categoria categoria;

    private String foto;



    public Producto(String nombre, Double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }


    public Producto(String nombre, Double precio,Categoria categoria) {
        this(nombre, precio);
        this.categoria = categoria;
    }
}
