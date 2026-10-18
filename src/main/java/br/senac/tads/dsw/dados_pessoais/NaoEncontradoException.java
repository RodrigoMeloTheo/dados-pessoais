package br.senac.tads.dsw.dados_pessoais;

// RuntimeException não obriga o código chamador a usar try/catch
public class NaoEncontradoException extends RuntimeException {

    public NaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}