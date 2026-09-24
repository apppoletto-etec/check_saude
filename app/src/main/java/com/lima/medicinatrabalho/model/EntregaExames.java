package com.lima.medicinatrabalho.model;

import java.util.Objects;

public class EntregaExames {



    private String dataEntrega;
    private String dataProximaEntrega;
    private String nomeFuncExame;

    private Funcionarios funcionario;

    private String id;
    private String tipoExame;
    private String statusExame;


    public EntregaExames() {
    }

    public Funcionarios getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(Funcionarios funcionario) {
        this.funcionario = funcionario;
    }

    public String getDataEntrega() {
        return dataEntrega;
    }

    public void setDataEntrega(String dataEntrega) {
        this.dataEntrega = dataEntrega;
    }

    public String getDataProximaEntrega() {
        return dataProximaEntrega;
    }

    public void setDataProximaEntrega(String dataProximaEntrega) {
        this.dataProximaEntrega = dataProximaEntrega;
    }

    public String getNomeFuncExame() {
        return nomeFuncExame;
    }

    public void setNomeFuncExame(String nomeFuncExame) {
        this.nomeFuncExame = nomeFuncExame;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipoExame() {
        return tipoExame;
    }

    public void setTipoExame(String tipoExame) {
        this.tipoExame = tipoExame;
    }

    public String getStatusExame() {
        return statusExame;
    }

    public void setStatusExame(String statusExame) {
        this.statusExame = statusExame;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntregaExames that = (EntregaExames) o;
        return Objects.equals(dataEntrega, that.dataEntrega) && Objects.equals(dataProximaEntrega, that.dataProximaEntrega) && Objects.equals(nomeFuncExame, that.nomeFuncExame) && Objects.equals(funcionario, that.funcionario) && Objects.equals(id, that.id) && Objects.equals(tipoExame, that.tipoExame) && Objects.equals(statusExame, that.statusExame);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataEntrega, dataProximaEntrega, nomeFuncExame, funcionario, id, tipoExame, statusExame);
    }

    @Override
    public String toString() {
        return
                "Funcionário  : " + nomeFuncExame
                        + "\nExame  : " + tipoExame ;

    }
}
