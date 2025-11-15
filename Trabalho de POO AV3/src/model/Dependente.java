package model;

import java.util.Objects;

/**
 * Representa um dependente ligado a um funcionário.
 * Mantém referência ao objeto Funcionario conforme requisito.
 */
public class Dependente {
    private Funcionario funcionario; // referência ao objeto Funcionario
    private String nome;

    public Dependente(Funcionario funcionario, String nome) {
        this.funcionario = funcionario;
        this.nome = nome;
    }

    // Getters
    public Funcionario getFuncionario() {
        return funcionario;
    }

    public String getNome() {
        return nome;
    }

    // Setters
    public void setFuncionario(Funcionario funcionario) {
        this.funcionario = funcionario;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dependente that = (Dependente) o;
        return funcionario != null && that.funcionario != null &&
                funcionario.getCodigo() == that.funcionario.getCodigo() &&
                Objects.equals(nome, that.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(funcionario != null ? funcionario.getCodigo() : null, nome);
    }

    @Override
    public String toString() {
        return "Dependente{" +
                "funcionarioCodigo=" + (funcionario != null ? funcionario.getCodigo() : "null") +
                ", nome='" + nome + '\'' +
                '}';
    }
}

