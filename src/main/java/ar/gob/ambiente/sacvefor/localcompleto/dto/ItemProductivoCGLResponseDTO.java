/*
 * Objetos DTO para gestionar los servicios para la versión 2 de TRAZ
 */
package ar.gob.ambiente.sacvefor.localcompleto.dto;

import java.io.Serializable;

/**
 * Objeto que encapsula los datos de un items productivo
 * que constituye el detalle de productos de Guías y Autorizaciones
 * @author rincostante
 */
public class ItemProductivoCGLResponseDTO implements Serializable{
    
    private Long id;
    private String nom_cientifico;
    private String nom_vulgar;
    private String clase;
    private String unidad;
    private Long id_especie_tax;
    private int grupo_especie;
    private Long id_prod;
    private float kilos_x_unidad;
    private float m3_x_unidad;
    private float total_kg;
    private float total_m3;
    private float saldo;
    private float saldo_kg;
    private float saldo_m3;
    private String cod_prod;
    private float total;
    private Long item_origen;
    private DetallePiezasCGLResponseDTO detalle_piezas;
    
    public ItemProductivoCGLResponseDTO(){
        
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom_cientifico() {
        return nom_cientifico;
    }

    public void setNom_cientifico(String nom_cientifico) {
        this.nom_cientifico = nom_cientifico;
    }

    public String getNom_vulgar() {
        return nom_vulgar;
    }

    public void setNom_vulgar(String nom_vulgar) {
        this.nom_vulgar = nom_vulgar;
    }

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public Long getId_especie_tax() {
        return id_especie_tax;
    }

    public void setId_especie_tax(Long id_especie_tax) {
        this.id_especie_tax = id_especie_tax;
    }

    public int getGrupo_especie() {
        return grupo_especie;
    }

    public void setGrupo_especie(int grupo_especie) {
        this.grupo_especie = grupo_especie;
    }

    public Long getId_prod() {
        return id_prod;
    }

    public void setId_prod(Long id_prod) {
        this.id_prod = id_prod;
    }

    public float getKilos_x_unidad() {
        return kilos_x_unidad;
    }

    public void setKilos_x_unidad(float kilos_x_unidad) {
        this.kilos_x_unidad = kilos_x_unidad;
    }

    public float getM3_x_unidad() {
        return m3_x_unidad;
    }

    public void setM3_x_unidad(float m3_x_unidad) {
        this.m3_x_unidad = m3_x_unidad;
    }

    public float getTotal_kg() {
        return total_kg;
    }

    public void setTotal_kg(float total_kg) {
        this.total_kg = total_kg;
    }

    public float getTotal_m3() {
        return total_m3;
    }

    public void setTotal_m3(float total_m3) {
        this.total_m3 = total_m3;
    }

    public float getSaldo() {
        return saldo;
    }

    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }

    public float getSaldo_kg() {
        return saldo_kg;
    }

    public void setSaldo_kg(float saldo_kg) {
        this.saldo_kg = saldo_kg;
    }

    public float getSaldo_m3() {
        return saldo_m3;
    }

    public void setSaldo_m3(float saldo_m3) {
        this.saldo_m3 = saldo_m3;
    }

    public String getCod_prod() {
        return cod_prod;
    }

    public void setCod_prod(String cod_prod) {
        this.cod_prod = cod_prod;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    public Long getItem_origen() {
        return item_origen;
    }

    public void setItem_origen(Long item_origen) {
        this.item_origen = item_origen;
    }

    public DetallePiezasCGLResponseDTO getDetalle_piezas() {
        return detalle_piezas;
    }

    public void setDetalle_piezas(DetallePiezasCGLResponseDTO detalle_piezas) {
        this.detalle_piezas = detalle_piezas;
    }
}
