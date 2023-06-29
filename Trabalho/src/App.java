import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.Statement;

import model.dao.ConectaBD;
import model.entity.CartaoAdicional;
import model.entity.CartaoCredito;

public class App {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        CartaoCredito cartaoPrincipal = null;
        CartaoAdicional cartaoAdicional = null;

        boolean sair = false;
        while (!sair) {
            System.out.println("----- Menu -----");
            System.out.println("1. Emitir cartão");
            System.out.println("2. Emitir cartão adicional");
            System.out.println("3. Realizar transação");
            System.out.println("4. Efetuar pagamento");
            System.out.println("5. Consultar saldo disponível");
            System.out.println("6. Sair");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    System.out.println("----- Emitir cartão -----");
                    System.out.print("Nome do titular: ");
                    String nomeTitular = scanner.nextLine();
                    System.out.print("Documento do titular: ");
                    String documentoTitular = scanner.nextLine();
                    System.out.print("Número do cartão: ");
                    String numeroCartao = scanner.nextLine();
                    System.out.print("Data de validade: ");
                    String dataValidade = scanner.nextLine();
                    System.out.print("Limite de crédito: ");
                    double limiteCredito = scanner.nextDouble();

                    // Salvar os dados do cartão principal no banco de dados
                    ConectaBD conexaoBD1 = new ConectaBD();
                    Connection conexao = conexaoBD1.getConexao();
                    String queryCartaoPrincipal = "INSERT INTO cartoes (nome_titular, documento_titular, numero_cartao, data_validade, limite_credito) VALUES (?, ?, ?, ?, ?)";

                    try (PreparedStatement stmt = conexao.prepareStatement(queryCartaoPrincipal)) {
                        stmt.setString(1, nomeTitular);
                        stmt.setString(2, documentoTitular);
                        stmt.setString(3, numeroCartao);
                        stmt.setString(4, dataValidade);
                        stmt.setDouble(5, limiteCredito);

                        stmt.executeUpdate();
                        System.out.println("Cartão principal emitido com sucesso.");
                    } catch (SQLException e) {
                        System.out.println("Erro ao emitir cartão principal: " + e.getMessage());
                        break;
                    }

                    // Restante do código omitido para brevidade
                    break;

                case 2:
                    System.out.println("----- Emitir cartão adicional -----");

                    // Verificar se há um cartão principal registrado
                    boolean cartaoPrincipalRegistrado = false;
                    int idCartaoPrincipal = -1;

                    ConectaBD conexaoBD2 = new ConectaBD();
                    Connection conexao2 = conexaoBD2.getConexao();

                    String queryVerificarCartaoPrincipal = "SELECT id FROM cartoes";
                    try (Statement stmt = conexao2.createStatement()) {
                        ResultSet rs = stmt.executeQuery(queryVerificarCartaoPrincipal);
                        if (rs.next()) {
                            cartaoPrincipalRegistrado = true;
                            idCartaoPrincipal = rs.getInt("id");
                        }
                    } catch (SQLException e) {
                        System.out.println("Erro ao verificar cartão principal: " + e.getMessage());
                        break;
                    }

                    if (!cartaoPrincipalRegistrado) {
                        System.out.println("Erro: Não há cartão principal emitido.");
                        break;
                    }

                    // Solicitar os dados do cartão adicional ao usuário
                    System.out.print("Documento do titular: ");
                    String documentoTitularAdicional = scanner.nextLine();

                    // Verificar se o documento do titular é válido
                    String queryVerificarDocumentoTitular = "SELECT COUNT(*) FROM cartoes WHERE documento_titular = ?";
                    try (PreparedStatement stmtVerificarDocumento = conexao2
                            .prepareStatement(queryVerificarDocumentoTitular)) {
                        stmtVerificarDocumento.setString(1, documentoTitularAdicional);
                        ResultSet rs = stmtVerificarDocumento.executeQuery();
                        rs.next();
                        int count = rs.getInt(1);
                        if (count == 0) {
                            System.out.println("Erro: Documento do titular inválido.");
                            break;
                        }
                    } catch (SQLException e) {
                        System.out.println("Erro ao verificar documento do titular: " + e.getMessage());
                        break;
                    }

                    System.out.print("Nome do adicional: ");
                    String nomeAdicional = scanner.nextLine();
                    System.out.print("Número do cartão adicional: ");
                    String numeroCartaoAdicional = scanner.nextLine();
                    System.out.print("Data de validade do cartão adicional: ");
                    String dataValidadeAdicional = scanner.nextLine();
                    System.out.print("Limite de crédito do cartão adicional: ");
                    double limiteCreditoAdicional = scanner.nextDouble();

                    // Verificar se o limite do segundo cartão é maior que o primeiro
                    String queryLimitePrimeiroCartao = "SELECT limite_credito FROM cartoes WHERE documento_titular = ?";
                    double limiteCreditoPrimeiroCartao = 0.0;
                    try (PreparedStatement stmtLimitePrimeiroCartao = conexao2
                            .prepareStatement(queryLimitePrimeiroCartao)) {
                        stmtLimitePrimeiroCartao.setString(1, documentoTitularAdicional);
                        ResultSet rs = stmtLimitePrimeiroCartao.executeQuery();
                        if (rs.next()) {
                            limiteCreditoPrimeiroCartao = rs.getDouble("limite_credito");
                        }
                    } catch (SQLException e) {
                        System.out.println("Erro ao verificar limite do primeiro cartão: " + e.getMessage());
                        break;
                    }

                    if (limiteCreditoAdicional > limiteCreditoPrimeiroCartao) {
                        System.out.println("Erro: Limite do segundo cartão não pode ser maior que o primeiro.");
                        break;
                    }

                    // Inserir os dados do cartão adicional no banco de dados
                    String queryInserirCartaoAdicional = "INSERT INTO cartoes_adicionais (documento_titular, nome_adicional, numero_cartao, data_validade, limite_credito, id_cartao_principal) "
                            + "VALUES (?, ?, ?, ?, ?, ?)";
                    try (PreparedStatement stmt = conexao2.prepareStatement(queryInserirCartaoAdicional)) {
                        stmt.setString(1, documentoTitularAdicional);
                        stmt.setString(2, nomeAdicional);
                        stmt.setString(3, numeroCartaoAdicional);
                        stmt.setString(4, dataValidadeAdicional);
                        stmt.setDouble(5, limiteCreditoAdicional);
                        stmt.setInt(6, idCartaoPrincipal);
                        stmt.executeUpdate();
                        System.out.println("Cartão adicional emitido com sucesso!");
                    } catch (SQLException e) {
                        System.out.println("Erro ao emitir cartão adicional: " + e.getMessage());
                    }

                    break;

                case 3:
                    System.out.println("----- Realizar transação -----");

                    ConectaBD conexaoBD3 = new ConectaBD();
                    Connection conexao3 = conexaoBD3.getConexao();
                                    
                    // Solicitar o documento do titular ao usuário
                    System.out.print("Documento do titular: ");
                    String documentoTitularTransacao = scanner.nextLine();
                                    
                    // Verificar se o documento do titular é válido
                    String queryVerificarDocumentoTitularTransacao = "SELECT COUNT(*) FROM cartoes WHERE documento_titular = ?";
                    try (PreparedStatement stmtVerificarDocumentoTransacao = conexao3.prepareStatement(queryVerificarDocumentoTitularTransacao)) {
                        stmtVerificarDocumentoTransacao.setString(1, documentoTitularTransacao);
                        ResultSet rs = stmtVerificarDocumentoTransacao.executeQuery();
                        rs.next();
                        int count = rs.getInt(1);
                        if (count == 0) {
                            System.out.println("Erro: Documento do titular inválido.");
                            break;
                        }
                    } catch (SQLException e) {
                        System.out.println("Erro ao verificar documento do titular: " + e.getMessage());
                        break;
                    }
                    
                    // Solicitar os dados da transação ao usuário
                    System.out.print("Valor da transação: ");
                    double valorTransacao = scanner.nextDouble();
                    scanner.nextLine(); // Limpar o buffer do teclado
                    
                    System.out.println("Escolha o cartão para realizar a transação:");
                    System.out.println("1. Cartão de Crédito Principal");
                    System.out.println("2. Cartão Adicional");
                    System.out.print("Opção: ");
                    int opcaoCartao = scanner.nextInt();
                    scanner.nextLine(); // Limpar o buffer do teclado
                    
                    double saldoDisponivel = 0.0;
                    String tipoCartao = "";
                    
                    if (opcaoCartao == 1) {
                        tipoCartao = "cartoes";
                    } else if (opcaoCartao == 2) {
                        tipoCartao = "cartoes_adicionais";
                    } else {
                        System.out.println("Opção inválida.");
                        break;
                    }
                    
                    // Verificar saldo no cartão selecionado
                    String querySaldo = "SELECT limite_credito FROM " + tipoCartao + " WHERE documento_titular = ?";
                    try (PreparedStatement stmt = conexao3.prepareStatement(querySaldo)) {
                        stmt.setString(1, documentoTitularTransacao);
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                            saldoDisponivel = rs.getDouble("limite_credito");
                        }
                    } catch (SQLException e) {
                        System.out.println("Erro ao consultar saldo do cartão: " + e.getMessage());
                        break;
                    }
                    
                    // Verificar se o saldo é suficiente para a transação
                    if (saldoDisponivel >= valorTransacao) {
                        // Atualizar saldo no cartão selecionado
                        String queryAtualizarSaldo = "UPDATE " + tipoCartao + " SET limite_credito = limite_credito - ? WHERE documento_titular = ?";
                        try (PreparedStatement stmt = conexao3.prepareStatement(queryAtualizarSaldo)) {
                            stmt.setDouble(1, valorTransacao);
                            stmt.setString(2, documentoTitularTransacao);
                            stmt.executeUpdate();
                        } catch (SQLException e) {
                            System.out.println("Erro ao atualizar saldo do cartão: " + e.getMessage());
                            break;
                        }
                    
                        System.out.println("Transação realizada com sucesso!");
                    } else {
                        System.out.println("Saldo insuficiente para realizar a transação.");
                    }
                    
                    break;

                case 4:

                    ConectaBD conexaoBD4 = new ConectaBD();
                    Connection conexao4 = conexaoBD4.getConexao();

                    System.out.println("----- Efetuar pagamento -----");

                    // Solicitar o documento do titular ao usuário
                    System.out.print("Documento do titular: ");
                    String documentoTitularPagamento = scanner.nextLine();

                    // Verificar se o documento do titular é válido
                    String queryVerificarDocumentoTitularPagamento = "SELECT COUNT(*) FROM cartoes WHERE documento_titular = ?";
                    try (PreparedStatement stmtVerificarDocumentoPagamento = conexao4.prepareStatement(queryVerificarDocumentoTitularPagamento)) {
                        stmtVerificarDocumentoPagamento.setString(1, documentoTitularPagamento);
                        ResultSet rs = stmtVerificarDocumentoPagamento.executeQuery();
                        rs.next();
                        int count = rs.getInt(1);
                        if (count == 0) {
                            System.out.println("Erro: Documento do titular inválido.");
                            break;
                        }
                    } catch (SQLException e) {
                        System.out.println("Erro ao verificar documento do titular: " + e.getMessage());
                        break;
                    }

                    // Solicitar o tipo de cartão ao usuário
                    System.out.println("Escolha o tipo de cartão:");
                    System.out.println("1. Cartão de Crédito Principal");
                    System.out.println("2. Cartão Adicional");
                    System.out.print("Opção: ");
                    int opcaoCartaoPagamento = scanner.nextInt();
                    scanner.nextLine(); // Limpar o buffer do teclado

                    String tipoCartaoPagamento = "";

                    if (opcaoCartaoPagamento == 1) {
                        tipoCartaoPagamento = "cartoes";
                    } else if (opcaoCartaoPagamento == 2) {
                        tipoCartaoPagamento = "cartoes_adicionais";
                    } else {
                        System.out.println("Opção inválida.");
                        break;
                    }

                    // Solicitar o valor a ser pago ao usuário
                    System.out.print("Valor a ser pago: ");
                    double valorPagamento = scanner.nextDouble();
                    scanner.nextLine(); // Limpar o buffer do teclado


                    // Atualizar saldo no cartão selecionado
                    String queryAtualizarSaldo = "UPDATE " + tipoCartaoPagamento + " SET limite_credito = limite_credito - ? WHERE documento_titular = ?";
                    try (PreparedStatement stmt = conexao4.prepareStatement(queryAtualizarSaldo)) {
                        stmt.setDouble(1, valorPagamento);
                        stmt.setString(2, documentoTitularPagamento);
                        stmt.executeUpdate();
                        System.out.println("Pagamento efetuado com sucesso!");
                    } catch (SQLException e) {
                        System.out.println("Erro ao efetuar pagamento: " + e.getMessage());
                    }

                    break;

                case 5:
                    System.out.println("----- Consultar saldo disponível -----");

                    // Solicitar o documento do cartão ao usuário
                    System.out.print("Digite o seu documento: ");
                    String documentoCartao = scanner.nextLine();

                    // Consultar saldo no cartão principal
                    double saldoPrincipal = 0.0;

                    ConectaBD conexaoBD5 = new ConectaBD();
                    Connection conexao5 = conexaoBD5.getConexao();

                    String querySaldoPrincipal2 = "SELECT limite_credito FROM cartoes WHERE documento_titular = ?";
                    try (PreparedStatement stmt = conexao5.prepareStatement(querySaldoPrincipal2)) {
                        stmt.setString(1, documentoCartao);
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                            saldoPrincipal = rs.getDouble("limite_credito");
                            System.out.println("Saldo disponível no cartão principal: R$" + saldoPrincipal);
                        } else {
                            System.out.println("Erro ao consultar saldo do cartão principal.");
                        }
                    } catch (SQLException e) {
                        System.out.println("Erro ao consultar saldo do cartão principal: " + e.getMessage());
                        break;
                    }

                    // Consultar saldo no cartão adicional, se existir
                    String querySaldoAdicional = "SELECT limite_credito FROM cartoes_adicionais WHERE documento_titular = ?";
                    try (PreparedStatement stmt = conexao5.prepareStatement(querySaldoAdicional)) {
                        stmt.setString(1, documentoCartao);
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                            double saldoAdicional = rs.getDouble("limite_credito");
                            System.out.println("Saldo disponível no cartão adicional: R$" + saldoAdicional);
                        }
                    } catch (SQLException e) {
                        System.out.println("Erro ao consultar saldo do cartão adicional: " + e.getMessage());
                        break;
                    }

                    break;

                case 6:
                    System.out.println("Saindo do programa...");
                    sair = true;
                    break;

                default:
                    System.out.println("Opção inválida. Tente novamente.");
                    break;
            }

        }

        scanner.close();

    }
}
