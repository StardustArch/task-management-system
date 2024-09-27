package com.taskmanagementee.model;

import java.util.Date;

public class TarefaProjetoDetalhes {
    private String tarefaDescricao;
    private String status;
    private Date tarefaPrazo;
    private String dataConclusao;
    private String projetoNome;
    private String projetoDescricao;
    private String gestorNome;

    // Construtor
    public TarefaProjetoDetalhes(String tarefaDescricao, String status, Date tarefaPrazo,
                                 String dataConclusao, String projetoNome, String projetoDescricao, String gestorNome) {
        this.tarefaDescricao = tarefaDescricao;
        this.status = status;
        this.tarefaPrazo = tarefaPrazo;
        this.dataConclusao = dataConclusao;
        this.projetoNome = projetoNome;
        this.projetoDescricao = projetoDescricao;
        this.gestorNome = gestorNome;
    }

    // Getters e Setters
    public String getTarefaDescricao() { return tarefaDescricao; }
    public String getStatus() { return status; }
    public Date getTarefaPrazo() { return tarefaPrazo; }
    public String getDataConclusao() { return dataConclusao; }
    public String getProjetoNome() { return projetoNome; }
    public String getProjetoDescricao() { return projetoDescricao; }
    public String getGestorNome() { return gestorNome; }
}
