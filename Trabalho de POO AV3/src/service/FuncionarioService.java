package service;

import model.Funcionario;
import model.Dependente;
import exceptions.FuncDuplicadoEx;
import exceptions.FuncInexistenteEx;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Responsável por armazenar e manipular funcionários e dependentes.
 */
public class FuncionarioService {
    private final Map<Integer, Funcionario> funcionarios;
    private final List<Dependente> dependentes;

    public FuncionarioService() {
        this.funcionarios = new HashMap<>();
        this.dependentes = new ArrayList<>();
    }

    /**
     * Cadastra um funcionário e seus dependentes.
     *
     * @param f                objeto Funcionario
     * @param nomesDependentes lista de nomes dos dependentes (pode ser vazia)
     * @throws FuncDuplicadoEx se já existir funcionário com mesmo código
     */
    public void cadastrarFuncionario(Funcionario f, List<String> nomesDependentes) throws FuncDuplicadoEx {
        Objects.requireNonNull(f, "Funcionario não pode ser null");
        int codigo = f.getCodigo();
        if (funcionarios.containsKey(codigo)) {
            throw new FuncDuplicadoEx();
        }
        funcionarios.put(codigo, f);

        if (nomesDependentes != null) {
            for (String nomeDep : nomesDependentes) {
                if (nomeDep != null && !nomeDep.trim().isEmpty()) {
                    Dependente d = new Dependente(f, nomeDep.trim());
                    dependentes.add(d);
                }
            }
        }
    }

    /**
     * Retorna o número de dependentes de um funcionário.
     */
    public int contarDependentes(int codigoFuncionario) {
        return (int) dependentes.stream()
                .filter(d -> d.getFuncionario() != null && d.getFuncionario().getCodigo() == codigoFuncionario)
                .count();
    }

    /**
     * Calcula o bônus para um salário e quantidade de dependentes:
     * bônus = salario * 0.02 * qtdDependentes
     */
    public double calcBonus(double salario, int qtdDependentes) {
        if (qtdDependentes <= 0) return 0.0;
        return salario * 0.02 * qtdDependentes;
    }

    /**
     * Gera uma lista de linhas formatadas com nome; qtd dependentes; bônus.
     * Essas linhas são apropriadas para exibir ou gravar em arquivo.
     */
    public List<String> listarBonusFormatado(Locale locale) {
        List<String> linhas = new ArrayList<>();
        // Cabeçalho
        linhas.add("Relatório de Bônus Mensal");
        linhas.add("-------------------------");
        for (Funcionario f : funcionarios.values().stream()
                .sorted(Comparator.comparing(Funcionario::getNome, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList())) {

            int qtd = contarDependentes(f.getCodigo());
            double bonus = calcBonus(f.getSalario(), qtd);
            // Formatação simples (formato ponto decimal) — UI pode formatar moeda ao exibir
            String linha = String.format("Nome: %s; Dependentes: %d; Bonus: %.2f",
                    f.getNome(), qtd, bonus);
            linhas.add(linha);
        }
        return linhas;
    }

    /**
     * Exclui funcionário e todos os seus dependentes.
     *
     * @throws FuncInexistenteEx se o funcionário não estiver cadastrado
     */
    public void excluirFuncionario(int codigo) throws FuncInexistenteEx {
        if (!funcionarios.containsKey(codigo)) {
            throw new FuncInexistenteEx();
        }
        // Remove funcionário
        funcionarios.remove(codigo);
        // Remove dependentes associados
        dependentes.removeIf(d -> d.getFuncionario() != null && d.getFuncionario().getCodigo() == codigo);
    }

    /**
     * Altera o salário do funcionário identificado por código.
     *
     * @throws FuncInexistenteEx se o funcionário não estiver cadastrado
     */
    public void alterarSalario(int codigo, double novoSalario) throws FuncInexistenteEx {
        Funcionario f = funcionarios.get(codigo);
        if (f == null) {
            throw new FuncInexistenteEx();
        }
        f.setSalario(novoSalario);
    }

    /**
     * Retorna uma cópia não modificável da coleção de funcionários (útil para testes/exibição).
     */
    public Collection<Funcionario> listarFuncionarios() {
        return Collections.unmodifiableCollection(funcionarios.values());
    }
}
