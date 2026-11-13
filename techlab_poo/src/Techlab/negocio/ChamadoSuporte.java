package Techlab.negocio;

import Techlab.interfaces.INotificavel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChamadoSuporte implements INotificavel {

    public static final String[] PRIORIDADES = {"Baixa", "Média", "Alta", "Crítica"};
    public static final String[] STATUS_VALIDOS = {"Aberto", "Em andamento", "Resolvido", "Fechado"};

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private int id;
    private String titulo;
    private String descricao;
    private String prioridade;
    private String status;
    private LocalDateTime dataAbertura;
    private AtivoTI ativoRelacionado;

    // chamado novo
    public ChamadoSuporte(String titulo, String descricao, String prioridade, AtivoTI ativoRelacionado){
        this(0, titulo, descricao, prioridade, "Aberto", LocalDateTime.now(), ativoRelacionado);
    }

    // chamado lido do banco
    public ChamadoSuporte(int id, String titulo, String descricao, String prioridade, String status,
                          LocalDateTime dataAbertura, AtivoTI ativoRelacionado){
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("O título do chamado não pode ficar vazio.");
        }
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("A descrição do chamado não pode ficar vazia.");
        }
        if (!contem(PRIORIDADES, prioridade)) {
            throw new IllegalArgumentException("Prioridade inválida: " + prioridade);
        }
        if (!contem(STATUS_VALIDOS, status)) {
            throw new IllegalArgumentException("Status inválido: " + status);
        }
        if (ativoRelacionado == null) {
            throw new IllegalArgumentException("Todo chamado precisa estar ligado a um ativo.");
        }
        this.id = id;
        this.titulo = titulo.trim();
        this.descricao = descricao.trim();
        this.prioridade = prioridade;
        this.status = status;
        this.dataAbertura = dataAbertura;
        this.ativoRelacionado = ativoRelacionado;
    }

    public int getId(){return this.id;}
    public String getTitulo(){return this.titulo;}
    public String getDescricao(){return this.descricao;}
    public String getPrioridade(){return this.prioridade;}
    public String getStatus(){return this.status;}
    public LocalDateTime getDataAbertura(){return this.dataAbertura;}
    public AtivoTI getAtivoRelacionado(){return this.ativoRelacionado;}

    public void setId(int id){this.id = id;}

    public void mudarStatus(String novoStatus){
        if (!contem(STATUS_VALIDOS, novoStatus)) {
            throw new IllegalArgumentException("Status inválido: " + novoStatus);
        }
        if (this.status.equals("Fechado")) {
            throw new IllegalStateException("Chamado fechado não pode ser alterado.");
        }
        this.status = novoStatus;
    }

    public boolean estaEncerrado(){
        return status.equals("Resolvido") || status.equals("Fechado");
    }

    private static boolean contem(String[] lista, String valor){
        for (String item : lista) {
            if (item.equals(valor)) return true;
        }
        return false;
    }

    @Override
    public String gerarNotificacao(){
        String texto = "[NOTIFICAÇÃO] Chamado #" + id + " (" + prioridade + ") sobre "
                + ativoRelacionado.getcodigoPatrimonio() + " está '" + status + "'.";
        if (prioridade.equals("Crítica") && !estaEncerrado()) {
            texto += " ATENÇÃO: prioridade crítica!";
        }
        return texto;
    }

    @Override
    public String toString(){
        String data = (dataAbertura == null) ? "-" : dataAbertura.format(FORMATO);
        return "Chamado #" + id + " [" + prioridade + "] " + titulo + " | " + status
                + " | aberto em " + data + "\n    Ativo: " + ativoRelacionado.getcodigoPatrimonio()
                + " (" + ativoRelacionado.getmodelo() + ")\n    Descrição: " + descricao;
    }
}
