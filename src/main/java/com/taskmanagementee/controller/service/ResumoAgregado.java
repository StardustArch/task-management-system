package com.taskmanagementee.controller.service;

public class ResumoAgregado {
    private int totalProjetos;
    private double taxaConclusaoMedia;
    private int totalTarefas;
    private int totalConcluidas;
    private int totalPendentes;
    private int totalAtrasadas;
    private int totalAndamento;
    private double porcentagemProjetosConcluidos;

    // Construtor, getters e setters
    public ResumoAgregado(int totalProjetos, double taxaConclusaoMedia, int totalTarefas, int totalConcluidas, int totalPendentes, int totalAtrasadas, int totalAndamento, double porcentagemProjetosConcluidos) {
        this.totalProjetos = totalProjetos;
        this.taxaConclusaoMedia = taxaConclusaoMedia;
        this.totalTarefas = totalTarefas;
        this.totalConcluidas = totalConcluidas;
        this.totalPendentes = totalPendentes;
        this.totalAtrasadas = totalAtrasadas;
        this.totalAndamento = totalAndamento;
        this.porcentagemProjetosConcluidos = porcentagemProjetosConcluidos;
    }

    // Getters
    public int getTotalProjetos() { return totalProjetos; }
    public double getTaxaConclusaoMedia() { return taxaConclusaoMedia; }
    public int getTotalTarefas() { return totalTarefas; }
    public int getTotalConcluidas() { return totalConcluidas; }
    public int getTotalPendentes() { return totalPendentes; }
    public int getTotalAtrasadas() { return totalAtrasadas; }
    public int getTotalAndamento() { return totalAndamento; }
    public double getPorcentagemProjetosConcluidos() { return porcentagemProjetosConcluidos; }
}

