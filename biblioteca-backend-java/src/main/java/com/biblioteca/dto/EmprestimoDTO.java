package com.biblioteca.dto;

import com.biblioteca.model.StatusEmprestimo;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para transferência de dados de Emprestimo.
 * 
 * Implementa o conceito de abstração, separando a representação
 * de dados da entidade de domínio.
 * 
 * @author Emerson Marques Cardoso dos Santos
 */
public class EmprestimoDTO {

    private Long id;

    private LocalDateTime dataEmprestimo;

    @NotNull(message = "Data de devolução prevista é obrigatória")
    private LocalDateTime dataDevolucaoPrevista;

    private LocalDateTime dataDevolucaoReal;

    private StatusEmprestimo statusEmprestimo;

    @DecimalMin(value = "0.0", message = "Valor da multa não pode ser negativo")
    private BigDecimal valorMulta;

    @NotNull(message = "ID do usuário é obrigatório")
    private Long usuarioId;

    @NotNull(message = "ID do livro é obrigatório")
    private Long livroId;

    // Campos adicionais para exibição
    private String nomeUsuario;
    private String emailUsuario;
    private String tituloLivro;
    private String autorLivro;
    private String isbnLivro;

    // Construtor padrão
    public EmprestimoDTO() {
        this.statusEmprestimo = StatusEmprestimo.ATIVO;
        this.valorMulta = BigDecimal.ZERO;
    }

    // Construtor com parâmetros principais
    public EmprestimoDTO(Long usuarioId, Long livroId, LocalDateTime dataDevolucaoPrevista) {
        this();
        this.usuarioId = usuarioId;
        this.livroId = livroId;
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
        this.dataEmprestimo = LocalDateTime.now();
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDataEmprestimo() {
        return dataEmprestimo;
    }

    public void setDataEmprestimo(LocalDateTime dataEmprestimo) {
        this.dataEmprestimo = dataEmprestimo;
    }

    public LocalDateTime getDataDevolucaoPrevista() {
        return dataDevolucaoPrevista;
    }

    public void setDataDevolucaoPrevista(LocalDateTime dataDevolucaoPrevista) {
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
    }

    public LocalDateTime getDataDevolucaoReal() {
        return dataDevolucaoReal;
    }

    public void setDataDevolucaoReal(LocalDateTime dataDevolucaoReal) {
        this.dataDevolucaoReal = dataDevolucaoReal;
    }

    public StatusEmprestimo getStatusEmprestimo() {
        return statusEmprestimo;
    }

    public void setStatusEmprestimo(StatusEmprestimo statusEmprestimo) {
        this.statusEmprestimo = statusEmprestimo;
    }

    public BigDecimal getValorMulta() {
        return valorMulta;
    }

    public void setValorMulta(BigDecimal valorMulta) {
        this.valorMulta = valorMulta;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getLivroId() {
        return livroId;
    }

    public void setLivroId(Long livroId) {
        this.livroId = livroId;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public String getTituloLivro() {
        return tituloLivro;
    }

    public void setTituloLivro(String tituloLivro) {
        this.tituloLivro = tituloLivro;
    }

    public String getAutorLivro() {
        return autorLivro;
    }

    public void setAutorLivro(String autorLivro) {
        this.autorLivro = autorLivro;
    }

    public String getIsbnLivro() {
        return isbnLivro;
    }

    public void setIsbnLivro(String isbnLivro) {
        this.isbnLivro = isbnLivro;
    }

    @Override
    public String toString() {
        return "EmprestimoDTO{" +
                "id=" + id +
                ", dataEmprestimo=" + dataEmprestimo +
                ", dataDevolucaoPrevista=" + dataDevolucaoPrevista +
                ", statusEmprestimo=" + statusEmprestimo +
                ", usuarioId=" + usuarioId +
                ", livroId=" + livroId +
                '}';
    }
}

