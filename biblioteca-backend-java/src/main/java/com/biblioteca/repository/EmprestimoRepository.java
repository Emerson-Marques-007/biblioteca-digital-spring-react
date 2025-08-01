package com.biblioteca.repository;

import com.biblioteca.model.Emprestimo;
import com.biblioteca.model.StatusEmprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositório para operações de banco de dados da entidade Emprestimo.
 * 
 * Implementa o padrão Repository, fornecendo abstração para
 * acesso aos dados de empréstimos.
 * 
 * @author Emerson Marques Cardoso dos Santos
 */
@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    /**
     * Busca empréstimos por status.
     * 
     * @param status status do empréstimo
     * @return lista de empréstimos com o status especificado
     */
    List<Emprestimo> findByStatusEmprestimo(StatusEmprestimo status);

    /**
     * Busca empréstimos ativos.
     * 
     * @return lista de empréstimos ativos
     */
    @Query("SELECT e FROM Emprestimo e WHERE e.statusEmprestimo = 'ATIVO'")
    List<Emprestimo> findEmprestimosAtivos();

    /**
     * Busca empréstimos atrasados.
     * 
     * @return lista de empréstimos atrasados
     */
    @Query("SELECT e FROM Emprestimo e WHERE e.statusEmprestimo = 'ATRASADO'")
    List<Emprestimo> findEmprestimosAtrasados();

    /**
     * Busca empréstimos devolvidos.
     * 
     * @return lista de empréstimos devolvidos
     */
    @Query("SELECT e FROM Emprestimo e WHERE e.statusEmprestimo = 'DEVOLVIDO'")
    List<Emprestimo> findEmprestimosDevolvidos();

    /**
     * Busca empréstimos por usuário.
     * 
     * @param usuarioId ID do usuário
     * @return lista de empréstimos do usuário
     */
    List<Emprestimo> findByUsuarioId(Long usuarioId);

    /**
     * Busca empréstimos ativos por usuário.
     * 
     * @param usuarioId ID do usuário
     * @return lista de empréstimos ativos do usuário
     */
    @Query("SELECT e FROM Emprestimo e WHERE e.usuario.id = :usuarioId " +
           "AND e.statusEmprestimo IN ('ATIVO', 'ATRASADO', 'RENOVADO')")
    List<Emprestimo> findEmprestimosAtivosPorUsuario(@Param("usuarioId") Long usuarioId);

    /**
     * Busca empréstimos por livro.
     * 
     * @param livroId ID do livro
     * @return lista de empréstimos do livro
     */
    List<Emprestimo> findByLivroId(Long livroId);

    /**
     * Busca empréstimo ativo de um livro específico.
     * 
     * @param livroId ID do livro
     * @return empréstimo ativo do livro, se existir
     */
    @Query("SELECT e FROM Emprestimo e WHERE e.livro.id = :livroId " +
           "AND e.statusEmprestimo IN ('ATIVO', 'ATRASADO', 'RENOVADO')")
    List<Emprestimo> findEmprestimoAtivoPorLivro(@Param("livroId") Long livroId);

    /**
     * Busca empréstimos realizados em um período.
     * 
     * @param dataInicio data inicial (inclusive)
     * @param dataFim data final (inclusive)
     * @return lista de empréstimos no período
     */
    List<Emprestimo> findByDataEmprestimoBetween(LocalDateTime dataInicio, LocalDateTime dataFim);

    /**
     * Busca empréstimos com devolução prevista em um período.
     * 
     * @param dataInicio data inicial (inclusive)
     * @param dataFim data final (inclusive)
     * @return lista de empréstimos com devolução no período
     */
    List<Emprestimo> findByDataDevolucaoPrevistaBetween(LocalDateTime dataInicio, LocalDateTime dataFim);

    /**
     * Busca empréstimos que vencem hoje.
     * 
     * @return lista de empréstimos que vencem hoje
     */
    @Query("SELECT e FROM Emprestimo e WHERE DATE(e.dataDevolucaoPrevista) = CURRENT_DATE " +
           "AND e.statusEmprestimo IN ('ATIVO', 'RENOVADO')")
    List<Emprestimo> findEmprestimosQueVencemHoje();

    /**
     * Busca empréstimos que vencem nos próximos dias.
     * 
     * @param dias número de dias para verificar
     * @return lista de empréstimos que vencem nos próximos dias
     */
    @Query("SELECT e FROM Emprestimo e WHERE e.dataDevolucaoPrevista <= :dataLimite " +
           "AND e.statusEmprestimo IN ('ATIVO', 'RENOVADO')")
    List<Emprestimo> findEmprestimosQueVencemEm(@Param("dataLimite") LocalDateTime dataLimite);

    /**
     * Busca empréstimos que já passaram da data de devolução.
     * 
     * @return lista de empréstimos em atraso
     */
    @Query("SELECT e FROM Emprestimo e WHERE e.dataDevolucaoPrevista < CURRENT_TIMESTAMP " +
           "AND e.statusEmprestimo IN ('ATIVO', 'RENOVADO')")
    List<Emprestimo> findEmprestimosVencidos();

    /**
     * Conta empréstimos por status.
     * 
     * @param status status do empréstimo
     * @return número de empréstimos com o status
     */
    long countByStatusEmprestimo(StatusEmprestimo status);

    /**
     * Conta empréstimos ativos de um usuário.
     * 
     * @param usuarioId ID do usuário
     * @return número de empréstimos ativos
     */
    @Query("SELECT COUNT(e) FROM Emprestimo e WHERE e.usuario.id = :usuarioId " +
           "AND e.statusEmprestimo IN ('ATIVO', 'ATRASADO', 'RENOVADO')")
    long contarEmprestimosAtivosPorUsuario(@Param("usuarioId") Long usuarioId);

    /**
     * Busca empréstimos com multa.
     * 
     * @return lista de empréstimos com multa aplicada
     */
    @Query("SELECT e FROM Emprestimo e WHERE e.valorMulta > 0")
    List<Emprestimo> findEmprestimosComMulta();

    /**
     * Calcula o valor total de multas pendentes.
     * 
     * @return valor total das multas
     */
    @Query("SELECT COALESCE(SUM(e.valorMulta), 0) FROM Emprestimo e " +
           "WHERE e.statusEmprestimo IN ('ATRASADO', 'DEVOLVIDO') AND e.valorMulta > 0")
    Double calcularTotalMultas();

    /**
     * Busca empréstimos renovados.
     * 
     * @return lista de empréstimos que foram renovados
     */
    List<Emprestimo> findByStatusEmprestimoOrderByDataEmprestimoDesc(StatusEmprestimo status);

    /**
     * Busca histórico de empréstimos de um usuário.
     * 
     * @param usuarioId ID do usuário
     * @return lista de empréstimos ordenados por data
     */
    @Query("SELECT e FROM Emprestimo e WHERE e.usuario.id = :usuarioId " +
           "ORDER BY e.dataEmprestimo DESC")
    List<Emprestimo> findHistoricoEmprestimosPorUsuario(@Param("usuarioId") Long usuarioId);

    /**
     * Busca histórico de empréstimos de um livro.
     * 
     * @param livroId ID do livro
     * @return lista de empréstimos ordenados por data
     */
    @Query("SELECT e FROM Emprestimo e WHERE e.livro.id = :livroId " +
           "ORDER BY e.dataEmprestimo DESC")
    List<Emprestimo> findHistoricoEmprestimosPorLivro(@Param("livroId") Long livroId);

    /**
     * Busca empréstimos realizados hoje.
     * 
     * @return lista de empréstimos de hoje
     */
    @Query("SELECT e FROM Emprestimo e WHERE DATE(e.dataEmprestimo) = CURRENT_DATE")
    List<Emprestimo> findEmprestimosDeHoje();

    /**
     * Busca empréstimos realizados esta semana.
     * 
     * @param inicioSemana data de início da semana
     * @return lista de empréstimos da semana
     */
    @Query("SELECT e FROM Emprestimo e WHERE e.dataEmprestimo >= :inicioSemana")
    List<Emprestimo> findEmprestimosDestaSemana(@Param("inicioSemana") LocalDateTime inicioSemana);

    /**
     * Verifica se um usuário tem empréstimos ativos.
     * 
     * @param usuarioId ID do usuário
     * @return true se tem empréstimos ativos
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
           "FROM Emprestimo e WHERE e.usuario.id = :usuarioId " +
           "AND e.statusEmprestimo IN ('ATIVO', 'ATRASADO', 'RENOVADO')")
    boolean usuarioTemEmprestimosAtivos(@Param("usuarioId") Long usuarioId);

    /**
     * Verifica se um livro está emprestado.
     * 
     * @param livroId ID do livro
     * @return true se está emprestado
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END " +
           "FROM Emprestimo e WHERE e.livro.id = :livroId " +
           "AND e.statusEmprestimo IN ('ATIVO', 'ATRASADO', 'RENOVADO')")
    boolean livroEstaEmprestado(@Param("livroId") Long livroId);
}

