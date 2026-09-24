package com.lima.medicinatrabalho.model;

import java.util.Objects;

public class Funcionarios {

    private String nome;
    private String funcao;
    private String id;


    public Funcionarios() {
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Funcionarios that = (Funcionarios) o;
        return Objects.equals(nome, that.nome) && Objects.equals(funcao, that.funcao) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome, funcao, id);
    }


    @Override
    public String toString() {
        return
                nome ;

    }
}