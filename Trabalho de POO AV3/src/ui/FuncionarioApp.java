package ui;

import model.Funcionario;
import service.FuncionarioService;
import util.FileUtil;
import exceptions.FuncDuplicadoEx;
import exceptions.FuncInexistenteEx;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.*;

/**
 * Interface simples com JOptionPane para interagir com o usuário.
 */
public class FuncionarioApp {
    private final FuncionarioService service;
    private final Locale localeBR = new Locale("pt", "BR");
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(localeBR);

    public FuncionarioApp() {
        this.service = new FuncionarioService();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FuncionarioApp app = new FuncionarioApp();
            app.run();
        });
    }

    private void run() {
        String menu = "Escolha uma opção:\n"
                + "1 - Cadastrar funcionário\n"
                + "2 - Mostrar bônus mensal de cada funcionário\n"
                + "3 - Excluir funcionário\n"
                + "4 - Alterar salário de um funcionário\n"
                + "5 - Sair";

        while (true) {
            String input = JOptionPane.showInputDialog(null, menu, "Gerenciamento RH", JOptionPane.QUESTION_MESSAGE);
            if (input == null) break; // usuário cancelou -> sai
            input = input.trim();
            if (input.isEmpty()) continue;

            switch (input) {
                case "1":
                    cadastrarFluxo();
                    break;
                case "2":
                    mostrarBonusFluxo();
                    break;
                case "3":
                    excluirFluxo();
                    break;
                case "4":
                    alterarSalarioFluxo();
                    break;
                case "5":
                    JOptionPane.showMessageDialog(null, "Encerrando aplicação.", "Sair", JOptionPane.INFORMATION_MESSAGE);
                    return;
                default:
                    JOptionPane.showMessageDialog(null, "Opção inválida. Informe 1-5.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cadastrarFluxo() {
        try {
            Integer codigo = lerInteiro("Informe o código do funcionário:");
            if (codigo == null) return;

            String nome = lerTextoObrigatorio("Informe o nome do funcionário:");
            if (nome == null) return;

            String cargo = lerTextoObrigatorio("Informe o cargo do funcionário:");
            if (cargo == null) return;

            Double salario = lerDouble("Informe o salário do funcionário (ex: 2500.50):");
            if (salario == null) return;

            Integer qtdDeps = lerInteiroComMinimo("Quantos dependentes esse funcionário tem? (0 ou mais):", 0);
            if (qtdDeps == null) return;

            List<String> nomesDeps = new ArrayList<>();
            for (int i = 1; i <= qtdDeps; i++) {
                String nomeDep = lerTextoObrigatorio("Nome do dependente " + i + " de " + qtdDeps + ": (ou CANCEL para abortar cadastro)");
                if (nomeDep == null) {
                    // Usuário cancelou a entrada dos dependentes => abortar cadastro inteiro
                    JOptionPane.showMessageDialog(null, "Cadastro abortado.", "Aviso", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                nomesDeps.add(nomeDep);
            }

            Funcionario f = new Funcionario(codigo, nome, cargo, salario);
            service.cadastrarFuncionario(f, nomesDeps);
            JOptionPane.showMessageDialog(null, "Funcionário cadastrado com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (FuncDuplicadoEx ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro inesperado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarBonusFluxo() {
        try {
            List<String> linhas = service.listarBonusFormatado(localeBR);
            // Formatar valores monetários para exibição (substitui a parte "Bonus: X.XX" por moeda)
            List<String> linhasFormatadas = new ArrayList<>();
            for (String linha : linhas) {
                if (linha.startsWith("Nome: ")) {
                    // tenta extrair o valor decimal no final
                    int idx = linha.lastIndexOf("Bonus:");
                    if (idx >= 0) {
                        String principio = linha.substring(0, idx);
                        String valorTxt = linha.substring(idx + "Bonus:".length()).trim();
                        try {
                            double valor = Double.parseDouble(valorTxt);
                            String valorFmt = currencyFormat.format(valor);
                            linhasFormatadas.add(principio + "Bonus: " + valorFmt);
                        } catch (NumberFormatException e) {
                            linhasFormatadas.add(linha);
                        }
                    } else {
                        linhasFormatadas.add(linha);
                    }
                } else {
                    linhasFormatadas.add(linha);
                }
            }

            // Exibe no diálogo (concatena com linhas)
            String texto = String.join("\n", linhasFormatadas);
            JOptionPane.showMessageDialog(null, texto, "Bônus Mensal", JOptionPane.INFORMATION_MESSAGE);

            // Pergunta se deseja salvar em arquivo
            int salvar = JOptionPane.showConfirmDialog(null, "Deseja salvar este relatório em arquivo?", "Salvar", JOptionPane.YES_NO_OPTION);
            if (salvar == JOptionPane.YES_OPTION) {
                String caminho = JOptionPane.showInputDialog(null, "Informe o caminho e nome do arquivo (ex: bonus_mensal.txt):", "bonus_mensal.txt");
                if (caminho != null && !caminho.trim().isEmpty()) {
                    try {
                        Path caminhoPath = Path.of(caminho.trim());
                        FileUtil.escreverLinhasEmArquivo(linhasFormatadas, caminhoPath);
                        JOptionPane.showMessageDialog(null, "Arquivo salvo em: " + caminhoPath.toAbsolutePath(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException ioEx) {
                        JOptionPane.showMessageDialog(null, "Erro ao salvar arquivo: " + ioEx.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Caminho inválido. Operação de salvar cancelada.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro ao gerar relatório: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void excluirFluxo() {
        try {
            Integer codigo = lerInteiro("Informe o código do funcionário a excluir:");
            if (codigo == null) return;
            try {
                service.excluirFuncionario(codigo);
                JOptionPane.showMessageDialog(null, "Funcionário e dependentes excluídos.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (FuncInexistenteEx ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Funcionário Inexistente", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro inesperado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void alterarSalarioFluxo() {
        try {
            Integer codigo = lerInteiro("Informe o código do funcionário cujo salário será alterado:");
            if (codigo == null) return;
            Double novoSalario = lerDouble("Informe o novo salário (ex: 3000.00):");
            if (novoSalario == null) return;

            try {
                service.alterarSalario(codigo, novoSalario);
                JOptionPane.showMessageDialog(null, "Salário alterado com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (FuncInexistenteEx ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Funcionário Inexistente", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erro inesperado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ----- helpers de leitura com validação -----

    /**
     * Lê um inteiro do usuário; retorna null se o usuário cancelar.
     */
    private Integer lerInteiro(String mensagem) {
        while (true) {
            String s = JOptionPane.showInputDialog(null, mensagem);
            if (s == null) return null;
            s = s.trim();
            if (s.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Entrada vazia. Informe um número válido ou cancele.", "Erro", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Número inválido. Tente novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Lê um inteiro com valor mínimo (inclusive). Retorna null se cancelar.
     */
    private Integer lerInteiroComMinimo(String mensagem, int minimo) {
        while (true) {
            Integer val = lerInteiro(mensagem);
            if (val == null) return null;
            if (val < minimo) {
                JOptionPane.showMessageDialog(null, "Informe um número maior ou igual a " + minimo + ".", "Erro", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            return val;
        }
    }

    /**
     * Lê um double do usuário; retorna null se cancelar.
     */
    private Double lerDouble(String mensagem) {
        while (true) {
            String s = JOptionPane.showInputDialog(null, mensagem);
            if (s == null) return null;
            s = s.trim().replace(',', '.'); // aceita vírgula como decimal
            if (s.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Entrada vazia. Informe um número válido ou cancele.", "Erro", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Número inválido. Tente novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Lê texto obrigatório; se o usuário cancelar retorna null.
     */
    private String lerTextoObrigatorio(String mensagem) {
        while (true) {
            String s = JOptionPane.showInputDialog(null, mensagem);
            if (s == null) return null;
            s = s.trim();
            if (s.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Entrada vazia. Informe um valor ou cancele.", "Erro", JOptionPane.ERROR_MESSAGE);
                continue;
            }
            return s;
        }
    }
}
