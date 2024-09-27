package com.taskmanagementee.model;

import java.time.LocalDate;
import java.util.Date;

public class Tarefa {
    private int id;
    private String descricao;
    private String status;
    private LocalDate prazo;
    private String responsavel;
    private int responsavel_id;
    private String responsavelNome;
    private String projeto;
    private int projecto_id;
    private String gestor;
    private String gestorEmail;
    private Date dataConclusao;

    public Tarefa(int id, String descricao, String status, LocalDate prazo, String responsavel, String projeto) {
        this.id = id;
        this.descricao = descricao;
        this.status = status;
        this.prazo = prazo;
        this.responsavel = responsavel;
//        this.responsavel_id = responsavel_id;
        this.projeto = projeto;
//        this.projecto_id = projecto_id;
    }
    public Tarefa (){

    }

    // Getters e setters para todos os campos

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getPrazo() {
        return prazo;
    }

    public void setPrazo(LocalDate prazo) {
        this.prazo = prazo;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public String getProjeto() {
        return projeto;
    }

    public void setProjeto(String projeto) {
        this.projeto = projeto;
    }

    public int getResponsavel_id() {
        return responsavel_id;
    }

    public void setResponsavel_id(int responsavel_id) {
        this.responsavel_id = responsavel_id;
    }

    public int getProjecto_id() {
        return projecto_id;
    }

    public void setProjecto_id(int projecto_id) {
        this.projecto_id = projecto_id;
    }

    public String getResponsavelNome() {
        return responsavelNome;
    }

    public void setResponsavelNome(String responsavelNome) {
        this.responsavelNome = responsavelNome;
    }

    public String getGestor() {
        return gestor;
    }

    public void setGestor(String gestor) {
        this.gestor = gestor;
    }

    public String getGestorEmail() {
        return gestorEmail;
    }

    public void setGestorEmail(String gestorEmail) {
        this.gestorEmail = gestorEmail;
    }

    public Date getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(Date dataConclusao) {
        this.dataConclusao = dataConclusao;
    }
}