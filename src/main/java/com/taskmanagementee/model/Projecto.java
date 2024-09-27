package com.taskmanagementee.model;

import com.taskmanagementee.model.DAO.ProjectoDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Projecto {
    private String nome;
    private int id;
    private String descricao;
    private LocalDate prazo;
    private LocalDate data_criacao;
    private List<Tarefa> tarefas;
    private int gestorId;
    private String gestorNome;
    private TarefaProjetoDetalhes tarefasAtribuidas;

    // Construtor
    public Projecto( String nome, String descricao, LocalDate prazo, int gestorId) {
        this.nome = nome;
        this.descricao = descricao;
        this.prazo = prazo;
        this.gestorId = gestorId;
        this.tarefas = new ArrayList<>();

    }
    public Projecto(){

    }


    // Adicionar uma tarefa
    public void adicionarTarefa(Tarefa tarefa) {
        tarefas.add(tarefa);
    }

    // Listar todas as tarefas
    public void listarTarefas() {
        for (Tarefa t : tarefas) {
            System.out.println(t);
        }
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

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

    public LocalDate getPrazo() {
        return prazo;
    }

    public void setPrazo(LocalDate prazo) {
        this.prazo = prazo;
    }

    public List<Tarefa> getTarefas() {
        return tarefas;
    }

    public void setTarefas(List<Tarefa> tarefas) {
        this.tarefas = tarefas;
    }

    public int getGestorId() {
        return gestorId;
    }

    public void setGestorId(int gestorId) {
        this.gestorId = gestorId;
    }

    public String getGestorNome() {
        return gestorNome;
    }

    public void setGestorNome(String gestorNome) {
        this.gestorNome = gestorNome;
    }

    @Override
    public String toString() {
        return "Projecto{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", prazo=" + prazo +
                '}';

    }

    public LocalDate getData_criacao() {
        return data_criacao;
    }

    public void setData_criacao(LocalDate data_criacao) {
        this.data_criacao = data_criacao;
    }

    public void setTarefasAtribuidas(TarefaProjetoDetalhes tarefasAtribuidas) {
        this.tarefasAtribuidas = tarefasAtribuidas;
    }
}
