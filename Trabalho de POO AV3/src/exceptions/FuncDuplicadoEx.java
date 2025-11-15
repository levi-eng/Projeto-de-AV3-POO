package exceptions;

/**
 * Exceção lançada quando é tentado cadastrar um funcionário
 * com um código que já existe no sistema.
 */
public class FuncDuplicadoEx extends Exception {
    public FuncDuplicadoEx() {
        super("Funcionário já cadastrado");
    }

    public FuncDuplicadoEx(String message) {
        super(message);
    }
}
