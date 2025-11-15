package exceptions;

/**
 * Exceção lançada quando uma operação tenta acessar um funcionário que não existe.
 * A mensagem padrão segue o enunciado: "Funcionário Inexistente".
 */
public class FuncInexistenteEx extends Exception {
    public FuncInexistenteEx() {
        super("Funcionário Inexistente");
    }

    public FuncInexistenteEx(String message) {
        super(message);
    }
}
