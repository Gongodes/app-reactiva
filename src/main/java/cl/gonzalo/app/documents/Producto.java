package cl.gonzalo.app.documents;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
@Data
@NoArgsConstructor
@Document(collection = "productos")
public class Producto {

    @Id
    private String id;

    private String nombre;
    private double precio;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createAt;



    public Producto(String nombre, double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }


}
