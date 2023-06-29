package model.entity;

public class CartaoAdicional extends CartaoCredito {
    private String nomeAdicional;


    public CartaoAdicional(String nomeTitular, String documentoTitular, String numeroCartao,String dataValidade, double limiteCredito, String nomeAdicional) {
        super(nomeTitular, documentoTitular, numeroCartao, dataValidade, limiteCredito);
        this.nomeAdicional = nomeAdicional;
    }

    public String getNomeAdicional() {
        return nomeAdicional;
    }

    public void setNomeAdicional(String nomeAdicional) {
        this.nomeAdicional = nomeAdicional;
    }
}