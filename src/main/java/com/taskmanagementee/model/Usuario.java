package com.taskmanagementee.model;

public class Usuario {
    private String nome;
    private String email;
    private String senha;
    private String papel;
    private String funcao_membro;
    private int tarefasAtribuidas;
    private String status;
    private int id;


    public Usuario( String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
    }
    public Usuario( String nome, String email, String senha, String papel) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.papel = papel;
    }
    public Usuario(int id, String nome, String email, String senha,String papel,int tarefas, String status) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.id = id;
        this.tarefasAtribuidas = tarefas;
        this.status = status;
        this.papel = papel;
    }
    public Usuario(){}

    public int getTarefasAtribuidas() {
        return tarefasAtribuidas;
    }

    public void setTarefasAtribuidas(int tarefasAtribuidas) {
        this.tarefasAtribuidas = tarefasAtribuidas;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getters e Setters
    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public boolean autenticar(String email, String senha) {
        return this.email.equals(email) && this.senha.equals(senha);
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public String getPapel() {
        return papel;
    }

    public void setPapel(String papel) {
        this.papel = papel;
    }

    public String getFuncao_membro() {
        return funcao_membro;
    }

    public void setFuncao_membro(String funcao_membro) {
        this.funcao_membro = funcao_membro;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}

