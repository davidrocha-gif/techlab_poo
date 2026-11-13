package Techlab.negocio;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public abstract class AtivoTI {

    protected static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    protected int id;
    protected String codigoPatrimonio;
    protected String modelo;
    protected String status;
    protected LocalDateTime dataAquisicao;

    // ativo novo, id vem do banco
    public AtivoTI(String codigoPatrimonio, String modelo, String status, LocalDateTime dataAquisicao){
        this(0, codigoPatrimonio, modelo, status, dataAquisicao);
    }

    // ativo lido do banco
    public AtivoTI(int id, String codigoPatrimonio, String modelo, String status, LocalDateTime dataAquisicao){
        if (codigoPatrimonio == null || codigoPatrimonio.isBlank()) {
            throw new IllegalArgumentException("O código de patrimônio não pode ficar vazio.");
        }
        if (modelo == null || modelo.isBlank()) {
            throw new IllegalArgumentException("O modelo não pode ficar vazio.");
        }
        this.id = id;
        this.codigoPatrimonio = codigoPatrimonio.trim();
        this.modelo = modelo.trim();
        this.status = (status == null || status.isBlank()) ? "Ativo" : status;
        this.dataAquisicao = dataAquisicao;
    }

    public int getid(){return this.id;}
    public String getcodigoPatrimonio(){return this.codigoPatrimonio;}
    public String getmodelo(){return this.modelo;}
    public String getstatus(){return this.status;}
    public LocalDateTime getdataAquisicao(){return this.dataAquisicao;}

    public void setid(int id){this.id = id;}
    public void setcodigoPatrimonio(String codigoPatrimonio){this.codigoPatrimonio = codigoPatrimonio;}
    public void setmodelo(String modelo){this.modelo = modelo;}
    public void setstatus(String status){this.status = status;}
    public void setdataAquisicao(LocalDateTime dataAquisicao){this.dataAquisicao = dataAquisicao;}

    public abstract double calcularCustoManutencaoMensal();

    @Override
    public String toString(){
        String data = (dataAquisicao == null) ? "-" : dataAquisicao.format(FORMATO_DATA);
        return "#" + id + " [" + getClass().getSimpleName() + "] " + codigoPatrimonio
                + " - " + modelo + " | status: " + status + " | adquirido em " + data;
    }
}
