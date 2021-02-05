
package ar.gob.ambiente.sacvefor.localcompleto.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Entidad que encapsula los datos correspondientes al detalle de piezas
 * que pudieran corresponder a un producto, ya transformado
 * para las guías de fiscalización de removido.
 * Objeto requerido para homologar los productos con TRAZ
 */

@Entity
public class DetallePiezas implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "espesor", nullable = false)
    private float espesor;
    
    @Column(name = "ancho", nullable = false)
    private float ancho;
    
    @Column(name = "largo", nullable = false)
    private float largo;
    
    @Column(name = "volumen_x_pieza", nullable = false)
    private float volumenXPieza;
    
    @Column(name = "cant_piezas", nullable = false)
    private int cantPiezas;
    
    @Column(name = "saldo_piezas", nullable = false)
    private float saldoPiezas;
    
    @Column(name = "volumen_total", nullable = false)
    private float volumenTotal;
    
    @Column(name = "saldo_volumen", nullable = false)
    private float saldoVolumen;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public float getEspesor() {
        return espesor;
    }

    public void setEspesor(float espesor) {
        this.espesor = espesor;
    }

    public float getAncho() {
        return ancho;
    }

    public void setAncho(float ancho) {
        this.ancho = ancho;
    }

    public float getLargo() {
        return largo;
    }

    public void setLargo(float largo) {
        this.largo = largo;
    }

    public float getVolumenXPieza() {
        return volumenXPieza;
    }

    public void setVolumenXPieza(float volumenXPieza) {
        this.volumenXPieza = volumenXPieza;
    }

    public int getCantPiezas() {
        return cantPiezas;
    }

    public void setCantPiezas(int cantPiezas) {
        this.cantPiezas = cantPiezas;
    }

    public float getSaldoPiezas() {
        return saldoPiezas;
    }

    public void setSaldoPiezas(float saldoPiezas) {
        this.saldoPiezas = saldoPiezas;
    }

    public float getVolumenTotal() {
        return volumenTotal;
    }

    public void setVolumenTotal(float volumenTotal) {
        this.volumenTotal = volumenTotal;
    }

    public float getSaldoVolumen() {
        return saldoVolumen;
    }

    public void setSaldoVolumen(float saldoVolumen) {
        this.saldoVolumen = saldoVolumen;
    }
}
