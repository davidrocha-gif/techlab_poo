package Techlab.negocio;

import Techlab.interfaces.INotificavel;
import java.time.LocalDateTime;

public class DispositivoRede extends AtivoTI implements INotificavel {
    private int numeroPortas;

    public DispositivoRede(String codigoPatrimonio, String modelo, String status,
        LocalDateTime dataAquisicao, int numeroPortas){
        this(0, codigoPatrimonio, modelo, status, dataAquisicao, numeroPortas);
    }

    public DispositivoRede(int id, String codigoPatrimonio, String modelo, String status,
        LocalDateTime dataAquisicao, int numeroPortas){
        super(id, codigoPatrimonio, modelo, status, dataAquisicao);
        setNumeroPortas(numeroPortas);
    }

    public int getNumeroPortas(){return this.numeroPortas;}

    public void setNumeroPortas(int numeroPortas){
        if (numeroPortas <= 0) {
            throw new IllegalArgumentException("O número de portas precisa ser maior que zero.");
        }
        this.numeroPortas = numeroPortas;
    }

    @Override
    public double calcularCustoManutencaoMensal(){
        return this.numeroPortas * 200;
    }

    @Override
    public String gerarNotificacao(){
        return "[ALERTA DE REDE] " + modelo + " (" + codigoPatrimonio + ") com "
                + numeroPortas + " portas está com status: " + status;
    }

    @Override
    public String toString(){
        return super.toString() + " | Portas: " + numeroPortas;
    }
}
