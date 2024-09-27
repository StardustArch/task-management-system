package com.taskmanagementee.model;

public class Membro extends Usuario {
    private String funcao;

    public Membro(String nome, String email, String senha, String funcao) {
        super(nome, email, senha);
        this.funcao = funcao;
    }

    // Getters e Setters
    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    @Override
    public String toString() {
        return "Membro{" +
                "nome='" + getNome() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", funcao='" + funcao + '\'' +
                '}';
    }
}
