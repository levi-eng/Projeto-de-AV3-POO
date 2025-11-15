package model;

import java.util.Objects;

/**
 * Representa um funcionário da empresa.
 * Atributos são encapsulados e o código é imutável após a criação.
 */
public class Funcionario {
    private final int codigo; // único, imutável
    private String nome;
    private String cargo;
    private double salario;

    public Funcionario(int codigo, String nome, String cargo, double salario) {
        this.codigo = codigo;
        this.nome = nome;
        this.cargo = cargo;
        this.salario = salario;
    }

    // Getters
    public int getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public String getCargo() {
        return cargo;
    }

    public double getSalario() {
        return salario;
    }

    // Setters (exceto para codigo)
    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Funcionario that = (Funcionario) o;
        return codigo == that.codigo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
        return "Funcionario{" +
                "codigo=" + codigo +
                ", nome='" + nome + '\'' +
                ", cargo='" + cargo + '\'' +
                ", salario=" + salario +
                '}';
    }
}
