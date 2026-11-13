package Techlab.negocio;

import java.time.LocalDateTime;

public class EstacaoTrabalho extends AtivoTI {
    private String departamento;
    private String usuarioAlocado;

    public EstacaoTrabalho(String codigoPatrimonio, String modelo, String status, LocalDateTime dataAquisicao,
                           String departamento, String usuarioAlocado){
        this(0, codigoPatrimonio, modelo, status, dataAquisicao, departamento, usuarioAlocado);
    }

    public EstacaoTrabalho(int id, String codigoPatrimonio, String modelo, String status, LocalDateTime dataAquisicao,
                           String departamento, String usuarioAlocado){
        super(id, codigoPatrimonio, modelo, status, dataAquisicao);
        setDepartamento(departamento);
        setUsuarioAlocado(usuarioAlocado);
    }

    public String getDepartamento(){return this.departamento;}
    public String getUsuarioAlocado(){return this.usuarioAlocado;}

    public void setDepartamento(String departamento){
        if (departamento == null || departamento.isBlank()) {
            throw new IllegalArgumentException("Informe o departamento.");
        }
        this.departamento = departamento.trim();
    }

    public void setUsuarioAlocado(String usuarioAlocado){
        if (usuarioAlocado == null || usuarioAlocado.isBlank()) {
            throw new IllegalArgumentException("Informe o usuário alocado.");
        }
        this.usuarioAlocado = usuarioAlocado.trim();
    }

    @Override
    public double calcularCustoManutencaoMensal(){
        return 150.0;
    }

    @Override
    public String toString(){
        return super.toString() + " | Depto: " + departamento + " | Usuário: " + usuarioAlocado;
    }
}
