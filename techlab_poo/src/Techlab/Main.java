package Techlab;

import Techlab.dao.AtivoDAO;
import Techlab.dao.ChamadoDAO;
import Techlab.db.ConexaoBanco;
import Techlab.interfaces.IConexaoBD;
import Techlab.interfaces.INotificavel;
import Techlab.negocio.AtivoTI;
import Techlab.negocio.ChamadoSuporte;
import Techlab.negocio.DispositivoRede;
import Techlab.negocio.EstacaoTrabalho;
import Techlab.negocio.Servidor;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {

    private static final Scanner teclado = new Scanner(System.in, "UTF-8");
    private static final AtivoDAO ativoDAO = new AtivoDAO();
    private static final ChamadoDAO chamadoDAO = new ChamadoDAO();
    private static final String[] STATUS_ATIVO = {"Ativo", "Em manutenção", "Inativo"};
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    public static void main(String[] args) {
        IConexaoBD conexao = new ConexaoBanco();
        conexao.inicializarBanco();

        int opcao = -1;
        try {
            while (opcao != 0) {
                mostrarMenu();
                opcao = lerInt("Escolha uma opção: ");
                executar(opcao);
            }
        } catch (NoSuchElementException e) {
            System.out.println("\nEntrada encerrada.");
        }
        System.out.println("Sistema encerrado. Até mais!");
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("========== TECHLAB INVENTORY & SERVICE DESK ==========");
        System.out.println("--- Ativos ---");
        System.out.println(" 1 - Cadastrar ativo");
        System.out.println(" 2 - Listar ativos");
        System.out.println(" 3 - Buscar ativo por ID");
        System.out.println(" 4 - Atualizar ativo");
        System.out.println(" 5 - Remover ativo");
        System.out.println("--- Chamados ---");
        System.out.println(" 6 - Abrir chamado");
        System.out.println(" 7 - Listar chamados");
        System.out.println(" 8 - Atualizar status de um chamado");
        System.out.println(" 9 - Ver chamados de um ativo");
        System.out.println("10 - Remover chamado");
        System.out.println("--- Outros ---");
        System.out.println("11 - Relatório de custos e chamados");
        System.out.println("12 - Carregar dados de exemplo");
        System.out.println(" 0 - Sair");
        System.out.println("======================================================");
    }

    private static void executar(int opcao) {
        try {
            switch (opcao) {
                case 1: cadastrarAtivo(); break;
                case 2: listarAtivos(); break;
                case 3: buscarAtivo(); break;
                case 4: atualizarAtivo(); break;
                case 5: removerAtivo(); break;
                case 6: abrirChamado(); break;
                case 7: listarChamados(); break;
                case 8: atualizarStatusChamado(); break;
                case 9: chamadosDoAtivo(); break;
                case 10: removerChamado(); break;
                case 11: gerarRelatorio(); break;
                case 12: carregarExemplos(); break;
                case 0: break;
                default: System.out.println("Opção inválida.");
            }
        } catch (SQLException e) {
            tratarErroBanco(e);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    // ---------- ativos ----------

    private static void cadastrarAtivo() throws SQLException {
        System.out.println("Tipo do ativo:");
        System.out.println(" 1 - Servidor");
        System.out.println(" 2 - Estação de trabalho");
        System.out.println(" 3 - Dispositivo de rede");
        int tipo = lerInt("Tipo: ");

        if (tipo < 1 || tipo > 3) {
            System.out.println("Tipo inválido.");
            return;
        }

        String patrimonio = lerTexto("Código de patrimônio: ");
        String modelo = lerTexto("Modelo: ");
        LocalDateTime data = lerData("Data de aquisição (dd/MM/aaaa, Enter = hoje): ");
        AtivoTI ativo;

        if (tipo == 1) {
            String ip = lerTexto("IP estático: ");
            String so = lerTexto("Sistema operacional: ");
            int ram = lerInt("Memória RAM (GB): ");
            ativo = new Servidor(patrimonio, modelo, "Ativo", data, ip, so, ram);
        } else if (tipo == 2) {
            String depto = lerTexto("Departamento: ");
            String usuario = lerTexto("Usuário alocado: ");
            ativo = new EstacaoTrabalho(patrimonio, modelo, "Ativo", data, depto, usuario);
        } else {
            int portas = lerInt("Número de portas: ");
            ativo = new DispositivoRede(patrimonio, modelo, "Ativo", data, portas);
        }

        ativoDAO.salvar(ativo);
        System.out.println("Cadastrado: " + ativo);
    }

    private static void listarAtivos() throws SQLException {
        List<AtivoTI> ativos = ativoDAO.listarTodos();
        if (ativos.isEmpty()) {
            System.out.println("Nenhum ativo cadastrado.");
            return;
        }
        System.out.println("--- Ativos cadastrados (" + ativos.size() + ") ---");
        for (AtivoTI ativo : ativos) {
            System.out.println(ativo);
        }
    }

    private static void buscarAtivo() throws SQLException {
        int id = lerInt("ID do ativo: ");
        AtivoTI ativo = ativoDAO.buscarPorId(id);
        if (ativo == null) {
            System.out.println("Ativo não encontrado.");
            return;
        }
        System.out.println(ativo);
        System.out.printf("Custo de manutenção mensal: R$ %.2f%n", ativo.calcularCustoManutencaoMensal());
    }

    private static void atualizarAtivo() throws SQLException {
        int id = lerInt("ID do ativo: ");
        AtivoTI ativo = ativoDAO.buscarPorId(id);
        if (ativo == null) {
            System.out.println("Ativo não encontrado.");
            return;
        }

        System.out.println("Atual: " + ativo);
        System.out.println("(deixe em branco para manter o valor atual)");

        String modelo = lerTexto("Novo modelo: ");
        if (!modelo.isEmpty()) {
            ativo.setmodelo(modelo);
        }

        if (confirmar("Alterar o status? (s/n): ")) {
            ativo.setstatus(escolher("Novo status:", STATUS_ATIVO));
        }

        if (ativo instanceof Servidor) {
            Servidor servidor = (Servidor) ativo;
            String ip = lerTexto("Novo IP: ");
            if (!ip.isEmpty()) servidor.setIpEstatico(ip);
            String so = lerTexto("Novo sistema operacional: ");
            if (!so.isEmpty()) servidor.setSistemaOperacional(so);
            Integer ram = lerIntOpcional("Nova RAM (GB): ");
            if (ram != null) servidor.setCapacidadeRAM(ram);
        } else if (ativo instanceof EstacaoTrabalho) {
            EstacaoTrabalho estacao = (EstacaoTrabalho) ativo;
            String depto = lerTexto("Novo departamento: ");
            if (!depto.isEmpty()) estacao.setDepartamento(depto);
            String usuario = lerTexto("Novo usuário: ");
            if (!usuario.isEmpty()) estacao.setUsuarioAlocado(usuario);
        } else if (ativo instanceof DispositivoRede) {
            DispositivoRede rede = (DispositivoRede) ativo;
            Integer portas = lerIntOpcional("Novo número de portas: ");
            if (portas != null) rede.setNumeroPortas(portas);
        }

        ativoDAO.atualizar(ativo);
        System.out.println("Atualizado: " + ativo);

        if (ativo instanceof INotificavel && !ativo.getstatus().equals("Ativo")) {
            System.out.println(((INotificavel) ativo).gerarNotificacao());
        }
    }

    private static void removerAtivo() throws SQLException {
        int id = lerInt("ID do ativo: ");
        AtivoTI ativo = ativoDAO.buscarPorId(id);
        if (ativo == null) {
            System.out.println("Ativo não encontrado.");
            return;
        }
        System.out.println(ativo);
        System.out.println("Atenção: os chamados deste ativo também serão apagados.");
        if (confirmar("Tem certeza? (s/n): ")) {
            ativoDAO.deletar(id);
        } else {
            System.out.println("Operação cancelada.");
        }
    }

    // ---------- chamados ----------

    private static void abrirChamado() throws SQLException {
        int ativoId = lerInt("ID do ativo com problema: ");
        AtivoTI ativo = ativoDAO.buscarPorId(ativoId);
        if (ativo == null) {
            System.out.println("Ativo não encontrado. Cadastre o ativo primeiro.");
            return;
        }
        System.out.println("Ativo: " + ativo);

        String titulo = lerTexto("Título do chamado: ");
        String descricao = lerTexto("Descrição do problema: ");
        String prioridade = escolher("Prioridade:", ChamadoSuporte.PRIORIDADES);

        ChamadoSuporte chamado = new ChamadoSuporte(titulo, descricao, prioridade, ativo);
        chamadoDAO.salvar(chamado);
        System.out.println(chamado.gerarNotificacao());
    }

    private static void listarChamados() throws SQLException {
        List<ChamadoSuporte> chamados = chamadoDAO.listarTodos();
        if (chamados.isEmpty()) {
            System.out.println("Nenhum chamado registrado.");
            return;
        }
        System.out.println("--- Chamados (" + chamados.size() + ") ---");
        for (ChamadoSuporte chamado : chamados) {
            System.out.println(chamado);
        }
    }

    private static void atualizarStatusChamado() throws SQLException {
        int id = lerInt("ID do chamado: ");
        ChamadoSuporte chamado = chamadoDAO.buscarPorId(id);
        if (chamado == null) {
            System.out.println("Chamado não encontrado.");
            return;
        }
        System.out.println(chamado);

        String novoStatus = escolher("Novo status:", ChamadoSuporte.STATUS_VALIDOS);
        chamado.mudarStatus(novoStatus);
        chamadoDAO.atualizar(chamado);
        System.out.println(chamado.gerarNotificacao());
    }

    private static void chamadosDoAtivo() throws SQLException {
        int ativoId = lerInt("ID do ativo: ");
        AtivoTI ativo = ativoDAO.buscarPorId(ativoId);
        if (ativo == null) {
            System.out.println("Ativo não encontrado.");
            return;
        }
        List<ChamadoSuporte> chamados = chamadoDAO.listarPorAtivo(ativoId);
        System.out.println("Ativo: " + ativo);
        if (chamados.isEmpty()) {
            System.out.println("Esse ativo não tem chamados.");
            return;
        }
        for (ChamadoSuporte chamado : chamados) {
            System.out.println(chamado);
        }
    }

    private static void removerChamado() throws SQLException {
        int id = lerInt("ID do chamado: ");
        ChamadoSuporte chamado = chamadoDAO.buscarPorId(id);
        if (chamado == null) {
            System.out.println("Chamado não encontrado.");
            return;
        }
        System.out.println(chamado);
        if (confirmar("Remover este chamado? (s/n): ")) {
            chamadoDAO.deletar(id);
        } else {
            System.out.println("Operação cancelada.");
        }
    }

    // ---------- relatório ----------

    private static void gerarRelatorio() throws SQLException {
        List<AtivoTI> ativos = ativoDAO.listarTodos();
        List<ChamadoSuporte> chamados = chamadoDAO.listarTodos();

        System.out.println("--- Custo de manutenção mensal ---");
        double total = 0;
        for (AtivoTI ativo : ativos) {
            // polimorfismo aqui
            double custo = ativo.calcularCustoManutencaoMensal();
            total += custo;
            System.out.printf("%-12s %-16s R$ %9.2f%n", ativo.getcodigoPatrimonio(),
                    ativo.getClass().getSimpleName(), custo);
        }
        System.out.printf("Total mensal: R$ %.2f (anual: R$ %.2f)%n", total, total * 12);

        System.out.println("--- Chamados por status ---");
        for (String status : ChamadoSuporte.STATUS_VALIDOS) {
            int contador = 0;
            for (ChamadoSuporte chamado : chamados) {
                if (chamado.getStatus().equals(status)) contador++;
            }
            System.out.println(status + ": " + contador);
        }

        System.out.println("--- Chamados críticos pendentes ---");
        boolean achou = false;
        for (ChamadoSuporte chamado : chamados) {
            if (chamado.getPrioridade().equals("Crítica") && !chamado.estaEncerrado()) {
                System.out.println(chamado.gerarNotificacao());
                achou = true;
            }
        }
        if (!achou) {
            System.out.println("Nenhum.");
        }
    }

    // ---------- exemplos ----------

    private static void carregarExemplos() throws SQLException {
        LocalDateTime hoje = LocalDateTime.now();
        AtivoTI[] exemplos = {
            new Servidor("SRV-001", "Dell PowerEdge R740", "Ativo", hoje.minusYears(2), "192.168.0.10", "Ubuntu Server 22.04", 64),
            new Servidor("SRV-002", "HP ProLiant DL380", "Ativo", hoje.minusYears(3), "192.168.0.11", "Debian 12", 32),
            new EstacaoTrabalho("EST-001", "Dell OptiPlex 7090", "Ativo", hoje.minusMonths(8), "Financeiro", "Mariana Lima"),
            new EstacaoTrabalho("EST-002", "Lenovo ThinkCentre M70", "Ativo", hoje.minusMonths(14), "Vendas", "Carlos Souza"),
            new DispositivoRede("NET-001", "Cisco Catalyst 2960", "Ativo", hoje.minusYears(1), 24),
            new DispositivoRede("NET-002", "TP-Link TL-SG1048", "Ativo", hoje.minusMonths(5), 48)
        };

        for (AtivoTI ativo : exemplos) {
            if (ativoDAO.buscarPorPatrimonio(ativo.getcodigoPatrimonio()) == null) {
                ativoDAO.salvar(ativo);
            }
        }

        if (chamadoDAO.listarTodos().isEmpty()) {
            AtivoTI servidor = ativoDAO.buscarPorPatrimonio("SRV-001");
            AtivoTI estacao = ativoDAO.buscarPorPatrimonio("EST-002");
            AtivoTI rede = ativoDAO.buscarPorPatrimonio("NET-001");

            chamadoDAO.salvar(new ChamadoSuporte("Servidor fora do ar", "Não responde ao ping desde cedo.", "Crítica", servidor));
            chamadoDAO.salvar(new ChamadoSuporte("PC muito lento", "Demora mais de 5 minutos para ligar.", "Média", estacao));
            chamadoDAO.salvar(new ChamadoSuporte("Porta 12 sem link", "Cabo trocado e a porta continua sem sinal.", "Alta", rede));
        }
        System.out.println("Dados de exemplo carregados.");
    }

    // ---------- entrada de dados ----------

    private static String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return teclado.nextLine().trim();
    }

    private static int lerInt(String mensagem) {
        while (true) {
            String texto = lerTexto(mensagem);
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro.");
            }
        }
    }

    private static Integer lerIntOpcional(String mensagem) {
        while (true) {
            String texto = lerTexto(mensagem);
            if (texto.isEmpty()) {
                return null;
            }
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                System.out.println("Digite um número inteiro ou deixe em branco.");
            }
        }
    }

    private static LocalDateTime lerData(String mensagem) {
        while (true) {
            String texto = lerTexto(mensagem);
            if (texto.isEmpty()) {
                return LocalDateTime.now();
            }
            try {
                return LocalDate.parse(texto, FORMATO_DATA).atStartOfDay();
            } catch (DateTimeParseException e) {
                System.out.println("Data inválida. Use o formato dd/MM/aaaa.");
            }
        }
    }

    private static boolean confirmar(String mensagem) {
        String resposta = lerTexto(mensagem);
        return resposta.equalsIgnoreCase("s");
    }

    private static String escolher(String titulo, String[] opcoes) {
        System.out.println(titulo);
        for (int i = 0; i < opcoes.length; i++) {
            System.out.println(" " + (i + 1) + " - " + opcoes[i]);
        }
        while (true) {
            int numero = lerInt("Opção: ");
            if (numero >= 1 && numero <= opcoes.length) {
                return opcoes[numero - 1];
            }
            System.out.println("Escolha um número da lista.");
        }
    }

    private static void tratarErroBanco(SQLException e) {
        String mensagem = e.getMessage();
        if (mensagem != null && mensagem.contains("UNIQUE")) {
            System.out.println("Erro: já existe um ativo com esse código de patrimônio.");
        } else {
            System.out.println("Erro no banco de dados: " + mensagem);
        }
    }
}
