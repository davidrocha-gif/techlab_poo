package Techlab.negocio;

import java.time.LocalDateTime;

public class Servidor extends AtivoTI {
    private String ipEstatico;
    private String sistemaOperacional;
    private int capacidadeRAM;

    public Servidor(String codigoPatrimonio, String modelo, String status, LocalDateTime dataAquisicao,
                    String ipEstatico, String sistemaOperacional, int capacidadeRAM){
        this(0, codigoPatrimonio, modelo, status, dataAquisicao, ipEstatico, sistemaOperacional, capacidadeRAM);
    }

    public Servidor(int id, String codigoPatrimonio, String modelo, String status, LocalDateTime dataAquisicao,
                    String ipEstatico, String sistemaOperacional, int capacidadeRAM){
        super(id, codigoPatrimonio, modelo, status, dataAquisicao);
        setIpEstatico(ipEstatico);
        setSistemaOperacional(sistemaOperacional);
        setCapacidadeRAM(capacidadeRAM);
    }

    public String getIpEstatico(){return this.ipEstatico;}
    public String getSistemaOperacional(){return this.sistemaOperacional;}
    public int getCapacidadeRAM(){return this.capacidadeRAM;}

    public void setIpEstatico(String ipEstatico){
        if (!ipValido(ipEstatico)) {
            throw new IllegalArgumentException("IP inválido: " + ipEstatico);
        }
        this.ipEstatico = ipEstatico.trim();
    }

    public void setSistemaOperacional(String sistemaOperacional){
        if (sistemaOperacional == null || sistemaOperacional.isBlank()) {
            throw new IllegalArgumentException("Informe o sistema operacional.");
        }
        this.sistemaOperacional = sistemaOperacional.trim();
    }

    public void setCapacidadeRAM(int capacidadeRAM){
        if (capacidadeRAM <= 0) {
            throw new IllegalArgumentException("A RAM precisa ser maior que zero.");
        }
        this.capacidadeRAM = capacidadeRAM;
    }

    private boolean ipValido(String ip){
        if (ip == null) return false;
        String[] partes = ip.trim().split("\\.");
        if (partes.length != 4) return false;
        for (String parte : partes) {
            try {
                int numero = Integer.parseInt(parte);
                if (numero < 0 || numero > 255) return false;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    @Override
    public double calcularCustoManutencaoMensal(){
        return 400 + (this.capacidadeRAM * 15);
    }

    @Override
    public String toString(){
        return super.toString() + " | IP: " + ipEstatico + " | SO: " + sistemaOperacional
                + " | RAM: " + capacidadeRAM + " GB";
    }
}
