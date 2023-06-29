package model.entity;

public class CartaoCredito {
    private String nomeTitular;
    private String documentoTitular;
    private String numeroCartao;
    private String dataValidade;
    private double limiteCredito;
    private double saldoDisponivel;

    public CartaoCredito(String nomeTitular, String documentoTitular, String numeroCartao,
                         String dataValidade, double limiteCredito) {
        this.nomeTitular = nomeTitular;
        this.documentoTitular = documentoTitular;
        this.numeroCartao = numeroCartao;
        this.dataValidade = dataValidade;
        this.limiteCredito = limiteCredito;
        this.saldoDisponivel = limiteCredito;
    }

    public String getNomeTitular() {
        return nomeTitular;
    }

    public void setNomeTitular(String nomeTitular) {
        this.nomeTitular = nomeTitular;
    }

    public String getDocumentoTitular() {
        return documentoTitular;
    }

    public void setDocumentoTitular(String documentoTitular) {
        this.documentoTitular = documentoTitular;
    }

    public String getNumeroCartao() {
        return numeroCartao;
    }

    public void setNumeroCartao(String numeroCartao) {
        this.numeroCartao = numeroCartao;
    }

    public String getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(String dataValidade) {
        this.dataValidade = dataValidade;
    }

    public double getLimiteCredito() {
        return limiteCredito;
    }

    public void setLimiteCredito(double limiteCredito) {
        this.limiteCredito = limiteCredito;
    }

    public double getSaldoDisponivel() {
        return saldoDisponivel;
    }

    public void setSaldoDisponivel(double saldoDisponivel) {
        this.saldoDisponivel = saldoDisponivel;
    }

    public boolean efetuarPagamento(double valorPagamento, String cartao) {
        if (valorPagamento <= saldoDisponivel) {
            saldoDisponivel -= valorPagamento;
            System.out.println("Pagamento efetuado com sucesso! " + "Saldo disponível: " + saldoDisponivel);
            return true;
        } else {
            return false;
        }
    }

    public void realizarTransacao(double valorTransacao, String estabelecimento) {
        if (valorTransacao <= saldoDisponivel) {
            saldoDisponivel -= valorTransacao;
            System.out.println("Transação realizada com sucesso. Saldo disponível: " + saldoDisponivel);
        } else {
            System.out.println("Limite de crédito excedido.");
        }
    }
}	