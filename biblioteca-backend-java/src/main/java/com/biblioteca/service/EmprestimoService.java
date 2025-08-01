package com.biblioteca.service;

import com.biblioteca.dto.EmprestimoDTO;
import com.biblioteca.model.Emprestimo;
import com.biblioteca.model.Livro;
import com.biblioteca.model.StatusEmprestimo;
import com.biblioteca.model.Usuario;
import com.biblioteca.repository.EmprestimoRepository;
import com.biblioteca.repository.LivroRepository;
import com.biblioteca.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Serviço para operações de negócio relacionadas a Empréstimos.
 * 
 * Implementa conceitos de POO:
 * - Encapsulamento: lógica de negócio complexa encapsulada
 * - Abstração: interface de serviço abstrai complexidade
 * - Polimorfismo: diferentes comportamentos baseados no status
 * 
 * @author Emerson Marques Cardoso dos Santos
 */
@Service
@Transactional
public class EmprestimoService {

    @Autowired
    private EmprestimoRepository emprestimoRepository;

    @Autowired
    private LivroRepository livroRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ModelMapper modelMapper;

    private static final int DIAS_EMPRESTIMO_PADRAO = 14;
    private static final int LIMITE_EMPRESTIMOS_POR_USUARIO = 3;

    /**
     * Lista todos os empréstimos.
     * 
     * @return lista de DTOs de empréstimos
     */
    @Transactional(readOnly = true)
    public List<EmprestimoDTO> listarTodos() {
        List<Emprestimo> emprestimos = emprestimoRepository.findAll();
        return emprestimos.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca um empréstimo por ID.
     * 
     * @param id ID do empréstimo
     * @return DTO do empréstimo se encontrado
     * @throws RuntimeException se não encontrado
     */
    @Transactional(readOnly = true)
    public EmprestimoDTO buscarPorId(Long id) {
        Optional<Emprestimo> emprestimo = emprestimoRepository.findById(id);
        if (emprestimo.isPresent()) {
            return converterParaDTO(emprestimo.get());
        }
        throw new RuntimeException("Empréstimo não encontrado com ID: " + id);
    }

    /**
     * Cria um novo empréstimo.
     * 
     * @param emprestimoDTO dados do empréstimo
     * @return DTO do empréstimo criado
     * @throws RuntimeException se validações falharem
     */
    public EmprestimoDTO criar(EmprestimoDTO emprestimoDTO) {
        // Validar usuário
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(emprestimoDTO.getUsuarioId());
        if (!usuarioOpt.isPresent()) {
            throw new RuntimeException("Usuário não encontrado com ID: " + emprestimoDTO.getUsuarioId());
        }
        Usuario usuario = usuarioOpt.get();

        // Validar se usuário está ativo
        if (!usuario.isAtivo()) {
            throw new RuntimeException("Usuário está inativo e não pode fazer empréstimos.");
        }

        // Validar limite de empréstimos
        long emprestimosAtivos = emprestimoRepository.contarEmprestimosAtivosPorUsuario(usuario.getId());
        if (emprestimosAtivos >= LIMITE_EMPRESTIMOS_POR_USUARIO) {
            throw new RuntimeException("Usuário já atingiu o limite de " + LIMITE_EMPRESTIMOS_POR_USUARIO + " empréstimos simultâneos.");
        }

        // Validar livro
        Optional<Livro> livroOpt = livroRepository.findById(emprestimoDTO.getLivroId());
        if (!livroOpt.isPresent()) {
            throw new RuntimeException("Livro não encontrado com ID: " + emprestimoDTO.getLivroId());
        }
        Livro livro = livroOpt.get();

        // Validar se livro está disponível
        if (!livro.isDisponivel()) {
            throw new RuntimeException("Livro não está disponível para empréstimo.");
        }

        // Criar empréstimo
        Emprestimo emprestimo = new Emprestimo();
        emprestimo.setUsuario(usuario);
        emprestimo.setLivro(livro);
        emprestimo.setDataEmprestimo(LocalDateTime.now());
        
        // Definir data de devolução
        if (emprestimoDTO.getDataDevolucaoPrevista() != null) {
            emprestimo.setDataDevolucaoPrevista(emprestimoDTO.getDataDevolucaoPrevista());
        } else {
            emprestimo.setDataDevolucaoPrevista(LocalDateTime.now().plusDays(DIAS_EMPRESTIMO_PADRAO));
        }

        // Marcar livro como emprestado
        livro.marcarComoEmprestado();
        livroRepository.save(livro);

        Emprestimo emprestimoSalvo = emprestimoRepository.save(emprestimo);
        return converterParaDTO(emprestimoSalvo);
    }

    /**
     * Finaliza um empréstimo (devolução).
     * 
     * @param id ID do empréstimo
     * @return DTO do empréstimo finalizado
     * @throws RuntimeException se não encontrado ou já finalizado
     */
    public EmprestimoDTO finalizar(Long id) {
        Optional<Emprestimo> emprestimoOpt = emprestimoRepository.findById(id);
        if (!emprestimoOpt.isPresent()) {
            throw new RuntimeException("Empréstimo não encontrado com ID: " + id);
        }

        Emprestimo emprestimo = emprestimoOpt.get();
        
        if (emprestimo.getStatusEmprestimo() == StatusEmprestimo.DEVOLVIDO) {
            throw new RuntimeException("Empréstimo já foi finalizado.");
        }

        // Finalizar empréstimo
        emprestimo.finalizar();
        
        Emprestimo emprestimoSalvo = emprestimoRepository.save(emprestimo);
        return converterParaDTO(emprestimoSalvo);
    }

    /**
     * Renova um empréstimo.
     * 
     * @param id ID do empréstimo
     * @param diasAdicionais dias adicionais para renovação
     * @return DTO do empréstimo renovado
     * @throws RuntimeException se não pode ser renovado
     */
    public EmprestimoDTO renovar(Long id, int diasAdicionais) {
        Optional<Emprestimo> emprestimoOpt = emprestimoRepository.findById(id);
        if (!emprestimoOpt.isPresent()) {
            throw new RuntimeException("Empréstimo não encontrado com ID: " + id);
        }

        Emprestimo emprestimo = emprestimoOpt.get();
        
        if (!emprestimo.podeSerRenovado()) {
            throw new RuntimeException("Empréstimo não pode ser renovado. Status: " + emprestimo.getStatusEmprestimo());
        }

        // Renovar empréstimo
        emprestimo.renovar(diasAdicionais > 0 ? diasAdicionais : DIAS_EMPRESTIMO_PADRAO);
        
        Emprestimo emprestimoSalvo = emprestimoRepository.save(emprestimo);
        return converterParaDTO(emprestimoSalvo);
    }

    /**
     * Lista empréstimos por status.
     * 
     * @param status status do empréstimo
     * @return lista de DTOs de empréstimos
     */
    @Transactional(readOnly = true)
    public List<EmprestimoDTO> listarPorStatus(StatusEmprestimo status) {
        List<Emprestimo> emprestimos = emprestimoRepository.findByStatusEmprestimo(status);
        return emprestimos.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista empréstimos ativos.
     * 
     * @return lista de DTOs de empréstimos ativos
     */
    @Transactional(readOnly = true)
    public List<EmprestimoDTO> listarAtivos() {
        List<Emprestimo> emprestimos = emprestimoRepository.findEmprestimosAtivos();
        return emprestimos.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista empréstimos atrasados.
     * 
     * @return lista de DTOs de empréstimos atrasados
     */
    @Transactional(readOnly = true)
    public List<EmprestimoDTO> listarAtrasados() {
        List<Emprestimo> emprestimos = emprestimoRepository.findEmprestimosAtrasados();
        return emprestimos.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista empréstimos de um usuário.
     * 
     * @param usuarioId ID do usuário
     * @return lista de DTOs de empréstimos do usuário
     */
    @Transactional(readOnly = true)
    public List<EmprestimoDTO> listarPorUsuario(Long usuarioId) {
        List<Emprestimo> emprestimos = emprestimoRepository.findByUsuarioId(usuarioId);
        return emprestimos.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista empréstimos ativos de um usuário.
     * 
     * @param usuarioId ID do usuário
     * @return lista de DTOs de empréstimos ativos do usuário
     */
    @Transactional(readOnly = true)
    public List<EmprestimoDTO> listarAtivosPorUsuario(Long usuarioId) {
        List<Emprestimo> emprestimos = emprestimoRepository.findEmprestimosAtivosPorUsuario(usuarioId);
        return emprestimos.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista empréstimos que vencem hoje.
     * 
     * @return lista de DTOs de empréstimos que vencem hoje
     */
    @Transactional(readOnly = true)
    public List<EmprestimoDTO> listarQueVencemHoje() {
        List<Emprestimo> emprestimos = emprestimoRepository.findEmprestimosQueVencemHoje();
        return emprestimos.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lista empréstimos que vencem nos próximos dias.
     * 
     * @param dias número de dias
     * @return lista de DTOs de empréstimos
     */
    @Transactional(readOnly = true)
    public List<EmprestimoDTO> listarQueVencemEm(int dias) {
        LocalDateTime dataLimite = LocalDateTime.now().plusDays(dias);
        List<Emprestimo> emprestimos = emprestimoRepository.findEmprestimosQueVencemEm(dataLimite);
        return emprestimos.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    /**
     * Atualiza status de empréstimos vencidos.
     * Método para ser executado periodicamente.
     * 
     * @return número de empréstimos atualizados
     */
    public int atualizarEmprestimosVencidos() {
        List<Emprestimo> emprestimosVencidos = emprestimoRepository.findEmprestimosVencidos();
        
        for (Emprestimo emprestimo : emprestimosVencidos) {
            emprestimo.atualizarStatus();
            emprestimoRepository.save(emprestimo);
        }
        
        return emprestimosVencidos.size();
    }

    /**
     * Calcula estatísticas de empréstimos.
     * 
     * @return mapa com estatísticas
     */
    @Transactional(readOnly = true)
    public EmprestimoEstatisticasDTO obterEstatisticas() {
        EmprestimoEstatisticasDTO stats = new EmprestimoEstatisticasDTO();
        
        stats.setTotalEmprestimos(emprestimoRepository.count());
        long ativos = emprestimoRepository.countByStatusEmprestimo(StatusEmprestimo.ATIVO);
        long renovados = emprestimoRepository.countByStatusEmprestimo(StatusEmprestimo.RENOVADO);
        stats.setEmprestimosAtivos(ativos + renovados);
        stats.setEmprestimosAtrasados(emprestimoRepository.countByStatusEmprestimo(StatusEmprestimo.ATRASADO));
        stats.setEmprestimosDevolvidos(emprestimoRepository.countByStatusEmprestimo(StatusEmprestimo.DEVOLVIDO));
        stats.setTotalMultas(emprestimoRepository.calcularTotalMultas());
        
        return stats;
    }

    /**
     * Converte entidade Emprestimo para DTO com informações adicionais.
     * 
     * @param emprestimo entidade
     * @return DTO com dados completos
     */
    private EmprestimoDTO converterParaDTO(Emprestimo emprestimo) {
        EmprestimoDTO dto = modelMapper.map(emprestimo, EmprestimoDTO.class);
        
        // Adicionar informações do usuário
        if (emprestimo.getUsuario() != null) {
            dto.setUsuarioId(emprestimo.getUsuario().getId());
            dto.setNomeUsuario(emprestimo.getUsuario().getNome());
            dto.setEmailUsuario(emprestimo.getUsuario().getEmail());
        }
        
        // Adicionar informações do livro
        if (emprestimo.getLivro() != null) {
            dto.setLivroId(emprestimo.getLivro().getId());
            dto.setTituloLivro(emprestimo.getLivro().getTitulo());
            dto.setAutorLivro(emprestimo.getLivro().getAutor());
            dto.setIsbnLivro(emprestimo.getLivro().getIsbn());
        }
        
        return dto;
    }

    /**
     * Classe interna para estatísticas de empréstimos.
     */
    public static class EmprestimoEstatisticasDTO {
        private long totalEmprestimos;
        private long emprestimosAtivos;
        private long emprestimosAtrasados;
        private long emprestimosDevolvidos;
        private Double totalMultas;

        // Getters e Setters
        public long getTotalEmprestimos() { return totalEmprestimos; }
        public void setTotalEmprestimos(long totalEmprestimos) { this.totalEmprestimos = totalEmprestimos; }
        
        public long getEmprestimosAtivos() { return emprestimosAtivos; }
        public void setEmprestimosAtivos(long emprestimosAtivos) { this.emprestimosAtivos = emprestimosAtivos; }
        
        public long getEmprestimosAtrasados() { return emprestimosAtrasados; }
        public void setEmprestimosAtrasados(long emprestimosAtrasados) { this.emprestimosAtrasados = emprestimosAtrasados; }
        
        public long getEmprestimosDevolvidos() { return emprestimosDevolvidos; }
        public void setEmprestimosDevolvidos(long emprestimosDevolvidos) { this.emprestimosDevolvidos = emprestimosDevolvidos; }
        
        public Double getTotalMultas() { return totalMultas; }
        public void setTotalMultas(Double totalMultas) { this.totalMultas = totalMultas; }
    }
}

