/*
 * Objetos DTO para gestionar los servicios para la versión 2 de TRAZ
 */
package ar.gob.ambiente.sacvefor.localcompleto.dto;

import java.io.Serializable;

/**
 * Objeto que encapsula los atributos del detalle de piezas para un mismo producto
 * para homologar con el item productivo de TRAZ
 * @author rincostante
 */
public class DetallePiezasCGLResponseDTO implements Serializable {
    
    private Long id;
    private float espesor;
    private float ancho;
    private float largo;
    private float volumen_x_pieza;
    private int cant_piezas;
    private float saldo_piezas;
    private float volumen_total;
    private float saldo_volumen;
    
    public DetallePiezasCGLResponseDTO(){
        
    }

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

    public float getVolumen_x_pieza() {
        return volumen_x_pieza;
    }

    public void setVolumen_x_pieza(float volumen_x_pieza) {
        this.volumen_x_pieza = volumen_x_pieza;
    }

    public int getCant_piezas() {
        return cant_piezas;
    }

    public void setCant_piezas(int cant_piezas) {
        this.cant_piezas = cant_piezas;
    }

    public float getSaldo_piezas() {
        return saldo_piezas;
    }

    public void setSaldo_piezas(float saldo_piezas) {
        this.saldo_piezas = saldo_piezas;
    }

    public float getVolumen_total() {
        return volumen_total;
    }

    public void setVolumen_total(float volumen_total) {
        this.volumen_total = volumen_total;
    }

    public float getSaldo_volumen() {
        return saldo_volumen;
    }

    public void setSaldo_volumen(float saldo_volumen) {
        this.saldo_volumen = saldo_volumen;
    }
}
