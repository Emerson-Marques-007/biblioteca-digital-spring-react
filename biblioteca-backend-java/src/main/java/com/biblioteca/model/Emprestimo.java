package com.biblioteca.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * Entidade que representa um empréstimo de livro no sistema.
 * 
 * Implementa conceitos de POO:
 * - Encapsulamento: lógica de negócio encapsulada em métodos
 * - Abstração: representa um empréstimo real
 * - Polimorfismo: diferentes comportamentos baseados no status
 * 
 * @author Emerson Marques Cardoso dos Santos
 */
@Entity
@Table(name = "emprestimos")
public class Emprestimo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Data de empréstimo é obrigatória")
    @Column(name = "data_emprestimo", nullable = false)
    private LocalDateTime dataEmprestimo;

    @NotNull(message = "Data de devolução prevista é obrigatória")
    @Column(name = "data_devolucao_prevista", nullable = false)
    private LocalDateTime dataDevolucaoPrevista;

    @Column(name = "data_devolucao_real")
    private LocalDateTime dataDevolucaoReal;

    @NotNull(message = "Status do empréstimo é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "status_emprestimo", nullable = false)
    private StatusEmprestimo statusEmprestimo = StatusEmprestimo.ATIVO;

    @DecimalMin(value = "0.0", message = "Valor da multa não pode ser negativo")
    @Column(name = "valor_multa", precision = 10, scale = 2)
    private BigDecimal valorMulta = BigDecimal.ZERO;

    // Relacionamentos
    @NotNull(message = "Usuário é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @NotNull(message = "Livro é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "livro_id", nullable = false)
    private Livro livro;

    // Constantes para cálculo de multa
    private static final BigDecimal VALOR_MULTA_DIARIA = new BigDecimal("2.00");
    private static final int DIAS_EMPRESTIMO_PADRAO = 14;

    // Construtor padrão
    public Emprestimo() {
        this.dataEmprestimo = LocalDateTime.now();
        this.statusEmprestimo = StatusEmprestimo.ATIVO;
        this.valorMulta = BigDecimal.ZERO;
    }

    // Construtor com parâmetros principais
    public Emprestimo(Usuario usuario, Livro livro, int diasEmprestimo) {
        this();
        this.usuario = usuario;
        this.livro = livro;
        this.dataDevolucaoPrevista = LocalDateTime.now().plusDays(diasEmprestimo);
    }

    // Métodos de negócio

    /**
     * Calcula a multa por atraso na devolução.
     * Implementa encapsulamento da lógica de cálculo.
     * 
     * @return valor da multa
     */
    public BigDecimal calcularMulta() {
        LocalDateTime dataReferencia = dataDevolucaoReal != null ? 
                                     dataDevolucaoReal : LocalDateTime.now();
        
        if (dataReferencia.isAfter(dataDevolucaoPrevista)) {
            long diasAtraso = ChronoUnit.DAYS.between(dataDevolucaoPrevista, dataReferencia);
            return VALOR_MULTA_DIARIA.multiply(BigDecimal.valueOf(diasAtraso));
        }
        
        return BigDecimal.ZERO;
    }

    /**
     * Verifica se o empréstimo está atrasado.
     * 
     * @return true se atrasado
     */
    public boolean isAtrasado() {
        if (statusEmprestimo == StatusEmprestimo.DEVOLVIDO) {
            return false;
        }
        return LocalDateTime.now().isAfter(dataDevolucaoPrevista);
    }

    /**
     * Finaliza o empréstimo, marcando como devolvido.
     * Implementa polimorfismo - comportamento específico para devolução.
     */
    public void finalizar() {
        this.dataDevolucaoReal = LocalDateTime.now();
        this.statusEmprestimo = StatusEmprestimo.DEVOLVIDO;
        this.valorMulta = calcularMulta();
        
        // Marca o livro como disponível
        if (livro != null) {
            livro.marcarComoDisponivel();
        }
    }

    /**
     * Renova o empréstimo por mais dias.
     * 
     * @param diasAdicionais número de dias para renovar
     * @throws IllegalStateException se não pode ser renovado
     */
    public void renovar(int diasAdicionais) {
        if (!statusEmprestimo.podeRenovar()) {
            throw new IllegalStateException("Empréstimo não pode ser renovado no status: " + statusEmprestimo);
        }
        
        this.dataDevolucaoPrevista = this.dataDevolucaoPrevista.plusDays(diasAdicionais);
        this.statusEmprestimo = StatusEmprestimo.RENOVADO;
    }

    /**
     * Atualiza o status do empréstimo baseado na data atual.
     * Implementa polimorfismo - comportamento específico para cada status.
     */
    public void atualizarStatus() {
        if (statusEmprestimo != StatusEmprestimo.DEVOLVIDO) {
            if (isAtrasado()) {
                this.statusEmprestimo = StatusEmprestimo.ATRASADO;
                this.valorMulta = calcularMulta();
            }
        }
    }

    /**
     * Verifica se o empréstimo pode ser renovado.
     * 
     * @return true se pode ser renovado
     */
    public boolean podeSerRenovado() {
        return statusEmprestimo.podeRenovar() && !isAtrasado();
    }

    /**
     * Calcula quantos dias restam para a devolução.
     * 
     * @return dias restantes (negativo se atrasado)
     */
    public long diasRestantes() {
        return ChronoUnit.DAYS.between(LocalDateTime.now(), dataDevolucaoPrevista);
    }

    /**
     * Retorna uma descrição do empréstimo.
     * 
     * @return descrição textual
     */
    public String getDescricao() {
        return String.format("Empréstimo #%d - %s para %s - Status: %s", 
                           id, 
                           livro != null ? livro.getTitulo() : "Livro não informado",
                           usuario != null ? usuario.getNome() : "Usuário não informado",
                           statusEmprestimo.getDescricao());
    }

    // Callbacks JPA
    @PrePersist
    public void prePersist() {
        if (dataEmprestimo == null) {
            dataEmprestimo = LocalDateTime.now();
        }
        if (dataDevolucaoPrevista == null) {
            dataDevolucaoPrevista = dataEmprestimo.plusDays(DIAS_EMPRESTIMO_PADRAO);
        }
        if (statusEmprestimo == null) {
            statusEmprestimo = StatusEmprestimo.ATIVO;
        }
        if (valorMulta == null) {
            valorMulta = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    public void preUpdate() {
        atualizarStatus();
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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Livro getLivro() {
        return livro;
    }

    public void setLivro(Livro livro) {
        this.livro = livro;
    }

    // equals e hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Emprestimo that = (Emprestimo) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Emprestimo{" +
                "id=" + id +
                ", dataEmprestimo=" + dataEmprestimo +
                ", dataDevolucaoPrevista=" + dataDevolucaoPrevista +
                ", statusEmprestimo=" + statusEmprestimo +
                ", valorMulta=" + valorMulta +
                '}';
    }
}

